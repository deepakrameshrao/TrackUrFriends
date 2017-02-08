package com.deepak;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.Service;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Config;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

public class ShareService extends Service {

	private LocationManager locationManager;
	public LocListener loclst = new LocListener();
	String addressString;
	static double lat;
	static double lng;
	static double alt;
	String PhoneNumber;
	android.telephony.SmsManager sm1;
	Handler handl = new Handler();
	Builder alert;
	SQLiteDatabase permDb;
	Cursor permission;
	ContentObserver cb;
	Cursor cur;
	IntentFilter smsfilter;
	//SmsReceiver smsReceiver = new SmsReceiver();
	
	static HashMap<String, String> lastKnownLocation = new HashMap<String, String>();
	static ArrayList<String> lastLocation = new ArrayList<String>();
	
	@Override
	public IBinder onBind(Intent arg0) {
		if(Config.DEBUG){
			Log.d("","");
		}
		// TODO Auto-generated method stub
		return null;
	}
	
	public void onCreate(){
		super.onCreate();
		lat = 12.983456;
		lng = 78.563432;
		alt = 800.06733;
		PhoneNumber = null;
		addressString = null;
		sm1 = android.telephony.SmsManager.getDefault();
		
		Log.d("Service","Inside service oncreate");
		
		cur = this.getContentResolver().query(Uri.parse("content://sms/"), null, null, null, null);
        cb = new ContentObserver(handl) {
        	@Override
        	public void onChange(boolean selfChange) {
        		// TODO Auto-generated method stub
        		super.onChange(selfChange);
        		ContentResolver cr = getContentResolver();
        		Cursor curs = cr.query(Uri.parse("content://sms/inbox/"), null, null, null, null);
        		curs.moveToFirst();
        		Log.d("Service","Deepak: count = "+curs.getColumnCount());
        		
        		
        		String[] str = curs.getColumnNames();
        		for(String str11: str){
        			Toast.makeText(getBaseContext(), " "+str11, Toast.LENGTH_LONG).show();
        		}
        		
        		
        		
        		//String s = curs.getString(11);
        		if(curs.getCount() > 0){
	        		//String s = curs.getString(12);// FBW
        			String s = curs.getString(11); //Froyo or eclair
	        		String number = curs.getString(2);
	        		Toast.makeText(getBaseContext(), "s(11)="+s+"\n s(2) = "+number, Toast.LENGTH_LONG).show();
	        		//if(s!=null)
		        	if(s.contains("Where are you??")) {
		        		Log.d("Deepak","Deepak: Inside Where are you??");
		        		permDb = openOrCreateDatabase("permission", 0, null);
		        		permission = permDb.query("permissions", null, "Number="+number, null, null, null, null);
		        		if(permission != null && permission.getCount()!=0){
			        		SendLocation(curs.getString(2));
			        		Log.d("Deepak","Deepak: Inside send num="+curs.getString(2));
			        		Toast.makeText(ShareService.this, "SMS Body : "+s+" Address = "+curs.getString(2)+" Person = "+curs.getString(3), Toast.LENGTH_LONG).show();
			        		cr.delete(Uri.parse("content://sms/conversations/"+curs.getString(1)), null, null);
			        		NotificationManager noti = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			        		noti.cancelAll();
			        		permission.close();
			        		permDb.close();
		        		}
		        	}
		        	else if(s.contains("Lat:")){
		        		lastKnownLocation.put(curs.getString(2), s);
		        		lastLocation.add(curs.getString(2));
		        		lastLocation.add(s);
		        		Intent in = new Intent(ShareService.this, RecvActivity.class);
		        		in.putExtra("Number", curs.getString(2));
		        		in.putExtra("Body", s);
		        		in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        		Toast.makeText(ShareService.this, "SMS Body : "+s+" Address = "+curs.getString(2)+" Person = "+curs.getString(3), Toast.LENGTH_LONG).show();
		        		cr.delete(Uri.parse("content://sms/conversations/"+curs.getString(1)), null, null);
		        		NotificationManager noti = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		        		noti.cancelAll();
		        		startActivity(in);
		        		Log.d("Deepak","Deepak: Inside showdialog");
		        	}
		        	
		            
        		}
        		Log.d("Deepak","Deepak: On change called");
        	}
			
			
		};
		
		cur.registerContentObserver(cb);
		
		//smsfilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		//registerReceiver(smsReceiver, smsfilter);
		Log.d("Deepak","Deepak: Inside service");
	}
	
	
	public boolean SendLocation(String number){
		PhoneNumber = number;
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, loclst);
		return true;
	}
	
	public class LocListener implements LocationListener{

		public void onLocationChanged(Location location) {
			lat = location.getLatitude();
			lng = location.getLongitude();
			alt = location.getAltitude();
			float speed = location.getSpeed();
			
			addressString = null;
			Geocoder gc = new Geocoder(getBaseContext(), Locale.getDefault());
		    try {
		      List<Address> addresses = gc.getFromLocation(lat, lng, 1);
		      StringBuilder sb = new StringBuilder();
		      Log.d("Geocoder","Deepak: Inside try ");
		      Log.d("Geocoder","Deepak: Addr size: "+addresses.size());
		     // if(addresses != null){
			      if (addresses.size() > 0) {
			    	  Log.d("Geocoder","Inside if loop");
			        Address address = addresses.get(0);
			        for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
			           sb.append(address.getAddressLine(i)).append("\n");
			           sb.append(address.getLocality()).append("\n");
			           sb.append(address.getPostalCode()).append("\n");
			           sb.append(address.getCountryName());
			      }
			      addressString = sb.toString();
		//	 }
		    } catch (IOException e) {
		    	Log.d("Geocoder","Deepak: IOexception");
		    }
		    
			sm1.sendTextMessage(PhoneNumber, null, "Lat:"+lat+"Lng:"+lng+"Addr="+addressString, null, null);
		    Log.d("Geocoder","Deepak: Sending message... ");
		    
		    ContentResolver cr = getContentResolver();
    		Cursor curs = cr.query(Uri.parse("content://sms/conversations"), null, null, null, null);
    		//cr.delete(Uri.parse("content://sms/conversations/"+curs.getString(1)), null, null);		    
    		locationManager.removeUpdates(loclst);

		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
	}
	
	/*
	public class SmsReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
			ContentResolver cr = getContentResolver();
    		Cursor curs = cr.query(Uri.parse("content://sms/"), null, null, null, null);
    		curs.moveToFirst();
    		Log.d("Service","Deepak: count = "+curs.getColumnCount());
    		/*String[] str = curs.getColumnNames();
    		for(String str11: str){
    			Toast.makeText(getBaseContext(), " "+str11, Toast.LENGTH_LONG).show();
    		}
    		//String s = curs.getString(11);
    		if(curs.getCount() > 0){
        		String s = curs.getString(12);
        		
        		String number = curs.getString(2);
        		Toast.makeText(getBaseContext(), "s(11)="+s+"\n s(2) = "+number, Toast.LENGTH_LONG).show();
        		//if(s!=null)
	        	if(s.contains("Where are you??")) {
	        		Log.d("Deepak","Deepak: Inside Where are you??");
	        		//permDb = openOrCreateDatabase("permission", 0, null);
	        		//permission = permDb.query("permissions", null, "Number="+number, null, null, null, null);
	        		//if(permission != null && permission.getCount()!=0){
		        		SendLocation(curs.getString(2));
		        		Log.d("Deepak","Deepak: Inside send num="+curs.getString(2));
		        		Toast.makeText(ShareService.this, "SMS Body : "+s+" Address = "+curs.getString(2)+" Person = "+curs.getString(3), Toast.LENGTH_LONG).show();
		        		cr.delete(Uri.parse("content://sms/conversations/"+curs.getString(1)), null, null);
		        		NotificationManager noti = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		        		noti.cancelAll();
		        		//permission.close();
		        		//permDb.close();
	        		//}
	        	}
	        	else if(s.contains("Lat:")){
	        		lastKnownLocation.put(curs.getString(2), s);
	        		lastLocation.add(curs.getString(2));
	        		lastLocation.add(s);
	        		Intent in = new Intent(ShareService.this, RecvActivity.class);
	        		in.putExtra("Number", curs.getString(2));
	        		in.putExtra("Body", s);
	        		in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        		Toast.makeText(ShareService.this, "SMS Body : "+s+" Address = "+curs.getString(2)+" Person = "+curs.getString(3), Toast.LENGTH_LONG).show();
	        		cr.delete(Uri.parse("content://sms/conversations/"+curs.getString(1)), null, null);
	        		NotificationManager noti = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	        		noti.cancelAll();
	        		startActivity(in);
	        		Log.d("Deepak","Deepak: Inside showdialog");
	        	}
	        	
	            
    		}
    		Log.d("Deepak","Deepak: On receive called called");
		}
		
	}*/
}
