package com.deepak;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

public class RecvActivity extends Activity {
	
	AlertDialog.Builder alert;
	String body;
	static double latit;
	static double lngi;
	String lat;
	String lng;
	String address;
	List<Address> addresses;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.d("Deepak","Deepak: Inside RecvActivity..");
		Intent in = getIntent();
		String number = in.getStringExtra("Number");
		body = in.getStringExtra("Body");
		showRecvDialog(number,body);
		alert.show();
	}
	
	public void showRecvDialog(String number, final String body) {
	     alert = new AlertDialog.Builder(this);
	     alert.setTitle("Location info for "+number);
	     int latpos = body.indexOf("Lat");
	     int lngpos = body.indexOf("Lng");
	     int addrpos = body.indexOf("Addr");
	     lat = body.substring(latpos+4, lngpos-1);
	     lng = body.substring(lngpos+4, addrpos-1);
	     address = body.substring(addrpos+4);
	     latit = Double.valueOf(lat.trim()).doubleValue();
	     lngi = Double.valueOf(lng.trim()).doubleValue();
	    //latit = 12.983446;
	    //lngi = 77.938352;
	     Button b = new Button(this);
	     
	     b.setText("Latitiude:"+lat+"\n"+"Longitude:"+lng+"\nAddress:"+address);
	     alert.setView(b);
	     
	     alert.setPositiveButton("Show on map", new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				Intent in = new Intent(getBaseContext(), ShareUrLocMap.class);
				Log.d("RecvActivity","Deepak: lat n lng ="+lat+" "+lng);
				
				Log.d("RecvActivity","Deepak: latit n lngi ="+latit+" "+lngi);
				in.putExtra("Latitude", latit);
				in.putExtra("Longitude", lngi);
				startActivity(in);
				finish();
			}
		});
	     alert.setNegativeButton("Cancel", new OnClickListener(){

			public void onClick(DialogInterface dialog, int which) {
				finish();
				
			}
	    	 
	     });
	     	     
	}

}
