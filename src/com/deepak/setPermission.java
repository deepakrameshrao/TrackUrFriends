package com.deepak;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.xml.parsers.SAXParser;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class setPermission extends Activity {

	myAdapter adapter;
	int dId;
	int dNumberIndex;
	int dNameIndex;
	String number;
	String id;
	String name;
	Cursor c;
	SQLiteDatabase permDb;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Cursor c = getContentResolver().query(ContactsContract.AUTHORITY_URI, null, null, null, null);
        
        c = getContentResolver().query(android.provider.Contacts.Phones.CONTENT_URI, null, null, null, null);
        String[] str = c.getColumnNames();
		//Toast.makeText(getBaseContext(), "Col count= "+c.getColumnCount()+"Col names = ", Toast.LENGTH_LONG).show();
		int len = str.length;
		//Toast.makeText(getBaseContext(), "len = "+len, Toast.LENGTH_LONG).show();
		//for(int i=0; i<len ; i++){
			//Toast.makeText(getBaseContext(), "s["+i+"] = "+str[i] , Toast.LENGTH_LONG).show();
		//}
		
		permDb = openOrCreateDatabase("permission", 0, null);
		
		permDb.execSQL("CREATE TABLE IF NOT EXISTS permissions(id integer, Number text);");
		
        adapter = new myAdapter(this, R.layout.permission, c);

        ListView lv = new ListView(this);
        lv.setAdapter(adapter);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		setContentView(lv);
		setTitle("Allow to access ur location");
	}
	@Override
	protected void onResume() {
		//TODO Auto-generated method stub
		super.onResume();
		//adapter = new myAdapter(this, R.layout.main, c);
	}
	
	public class myAdapter extends ResourceCursorAdapter{

		public myAdapter(Context context, int layout, Cursor c) {
			super(context, layout, c);
			try{
				
				dId = c.getColumnIndexOrThrow(android.provider.Contacts.Phones._ID);
				dNumberIndex = c.getColumnIndexOrThrow(Contacts.Phones.NUMBER);
				dNameIndex = c.getColumnIndexOrThrow(Contacts.Phones.NAME);
				
			}
			catch (IllegalArgumentException e){
				Log.e("PermissionActivity","Exception caught : "+e);
			}
			
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			final CheckBox chk = (CheckBox) view.findViewById(R.id.CheckBox01);
			final String name = cursor.getString(dNameIndex);
			final String number = cursor.getString(dNumberIndex);
			chk.setText(name);
			if(name == null || name.equals(""))
				chk.setText(number);
			//Toast.makeText(context, "Number="+number, Toast.LENGTH_LONG).show();
			Cursor c = permDb.query("permissions", null, "Number="+number, null, null, null, null);
			if(c.getCount()!=0){
				chk.setChecked(true);
			}
			else 
				chk.setChecked(false);
			//Toast.makeText(context, "Col name="+c.getColumnName(0)+"Hi", Toast.LENGTH_LONG).show();
			chk.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					boolean flag = false;
					if(chk.isChecked()){
						//Toast.makeText(getBaseContext(), "Checked", Toast.LENGTH_LONG).show();
						ContentValues values = new ContentValues();
						values.put("id", 1);
						values.put("Number", number);
						String[] args = {number}; 
						permDb.insert("permissions", null, values);
						Log.d("Setperm","Deepak: Inside isChecked");
						//dNumbers.add(number);
						//permDb.delete("permissions", "Number", args);
					}
					else{
						Cursor c = permDb.query("permissions", null, null, null, null, null, null);
						c.moveToFirst();
						Toast.makeText(getBaseContext(), "Number ="+number, Toast.LENGTH_LONG).show();
						for(int i=0; i<c.getCount(); i++){
							if(c.getString(1).equals(number))
								flag = true;
						}
						if(flag)
							permDb.delete("permissions", "Number="+number, null);
						/*if(dNumbers.contains(number))
							dNumbers.remove(number);*/
						//ContentValues cv = new ContentValues();
						//cv.put("Number", 1111111111);
						
						//permDb.update("permissions", cv,"Number="+number, null);
						Log.d("Setperm","Deepak: Inside (!isChecked)");
					}
				}
			});
		}
		
	}
	
	
}
