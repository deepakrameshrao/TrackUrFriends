package com.deepak;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LocationWidget extends Activity {
	
	String TAG = "LocationWidget";
    /** Called when the activity is first created. */
   
    	AlertDialog.Builder alert;
    	AlertDialog.Builder alert1;
    	private LocationManager locationManager;
    	public LocListener1 loclst = new LocListener1();
    	public static String addressString;
    	static double lat;
    	static double lng;
    	static double alt;
    	String PhoneNumber;
    	final ShareService s = new ShareService();
    	SQLiteDatabase sdb;
    	Geocoder gc1;
    	Address address = new Address(Locale.US);
    	static String text1;
    	static String text11;
    	static String text2;
    	static String text22;
    	static String text3;
    	static String text33;
    	
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Intent service = new Intent();
            service.setClass(this, ShareService.class);
            startService(service);
            setContentView(R.layout.main1);
            
            //sdb = openOrCreateDatabase("permission", 0, null);
            //sdb.execSQL("CREATE TABLE perm(Number char(50));");
            
            ContentValues values = new ContentValues();
            values.put("Number", "9611602053");
            //sdb.insert("perm", null, values);
            //LinearLayout ll = (LinearLayout) findViewById(R.id.linear_layout);
            if(!ShareService.lastLocation.isEmpty()){
	            text1 = ShareService.lastLocation.get(0);
	            text11 = ShareService.lastLocation.get(1);
	            TextView t1 = (TextView)findViewById(R.id.TextView01);
	            t1.bringToFront();
	            t1.setFocusableInTouchMode(true);
	            t1.setEllipsize(TruncateAt.MARQUEE);
	            t1.setSingleLine();
	            gc1 = new Geocoder(this, Locale.getDefault());
	            try {
					List<Address> addresses = gc1.getFromLocation(RecvActivity.latit, RecvActivity.lngi, 2);
					if(!addresses.isEmpty()){
						address = addresses.get(0);
						Log.d("LocationWidget", "Got address");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	           // t1.setText(text1+" is at Lat:"+RecvActivity.latit+" Lng:"+RecvActivity.lngi+"Adress = "+address.getCountryName());
	            //t1.setEllipsize(TruncateAt.MARQUEE);
	           // t1.setTextColor(0xffff00ff);
	            //t1.setText("I'm at loc..");
	            //setContentView(ll);
            }
            
            if((ShareService.lastLocation.size()>=3)){
            	TextView t2 = (TextView)findViewById(R.id.TextView02);
	            text2 = ShareService.lastLocation.get(2);
	            text22 = ShareService.lastLocation.get(3);
	           // t2.setText(text2+" is at Lat:"+RecvActivity.latit+" Lng:"+RecvActivity.lngi+"Adress = "+address.getCountryName());
	           // t2.bringToFront();
	           // t2.setFocusableInTouchMode(true);
	           // t2.setEllipsize(TruncateAt.MARQUEE);
	            //t2.setSingleLine();
	           // t2.setTextColor(0xffff00ff);
	           // t2.setText(text2+" is at Lat:"+RecvActivity.latit+" Lng:"+RecvActivity.lngi+"Adress = "+address.getCountryName());

            }
            
            if((ShareService.lastLocation.size()>=5)){
            	TextView t3 = (TextView)findViewById(R.id.TextView03);
	            text3 = ShareService.lastLocation.get(4);
	            text33 = ShareService.lastLocation.get(5);
	            
	            /*t3.setText(text3+" is at Lat:"+RecvActivity.latit+" Lng:"+RecvActivity.lngi+"Adress = "+address.getCountryName());
	            t3.setEllipsize(TruncateAt.MARQUEE);
	            t3.setTextColor(0xf00000ff);
	            */
            }
            
        }
        
        public boolean onCreateOptionsMenu(Menu menu) {
    	    MenuInflater inflater = getMenuInflater();
    	    inflater.inflate(R.menu.menu, menu);
    	    return true;
    	}
        
    	public boolean onOptionsItemSelected(MenuItem item) {
    	    switch (item.getItemId()) {
    	    case (R.id.share_location):
    	    	showalert();
    	        alert.show();
    	        break;
    	    case (R.id.find_friends):
    	    	findfriends();
    	        alert1.show();
    	    	break;
    	    case (R.id.settings):
    	    	setPermissions();
    	    	break;
    	    case (R.id.quit):
    	    	if(locationManager != null)
    	    		locationManager.removeUpdates(loclst);
    	        Log.d("DEEPAK","Closing Session, updates removed");
    	        finish();
    	        Log.d("DEEPAK","On finish called");
    	        return true;
    	    }
    	    return false;
    	}
        
        public void showalert()
    	{
    		alert = new AlertDialog.Builder(this);  
    	    
    	    alert.setTitle("Details");  
    	    alert.setMessage("Enter the number");    
    	       
    	     // Set an EditText view to get user input   
    	     final EditText input = new EditText(this);
    	     input.setInputType(2);
    	     alert.setView(input);  
    	       
    	     alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
    	     public void onClick(DialogInterface dialog, int whichButton) {  
    	    	 PhoneNumber = input.getText().toString();
    	    	 locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    			 locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, loclst);	       }  
    	     });  
    	       
    	     alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {  
    	       public void onClick(DialogInterface dialog, int whichButton) {  
    	         // Cancelled.  
    	       }  
    	     });  
    	}
        
        public void findfriends(){
            alert1 = new AlertDialog.Builder(this);  
    	    
    	    alert1.setTitle("Details");  
    	    alert1.setMessage("Enter the number");    
    	       
    	     // Set an EditText view to get user input   
    	     final EditText input1 = new EditText(this);
       	     input1.setInputType(2);
    	     alert1.setView(input1);  
    	       
    	     alert1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
    	     public void onClick(DialogInterface dialog, int whichButton) {  
    		        String PhoneNumber1 = input1.getText().toString();
    		        android.telephony.SmsManager sm1 = android.telephony.SmsManager.getDefault();
    				sm1.sendTextMessage(PhoneNumber1, null, "Where are you??", null, null);
    	       }  
    	     });  
    	       
    	     alert1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {  
    	       public void onClick(DialogInterface dialog, int whichButton) {  
    	         // Cancelled.  
    	       }  
    	     });  
        }
        
        public void setPermissions(){
        	Intent in = new Intent(this, setPermission.class);
        	startActivity(in);
        }
        
        public class LocListener1 implements LocationListener{

    		public void onLocationChanged(Location location) {
    			lat = location.getLatitude();
    			lng = location.getLongitude();
    			alt = location.getAltitude();
    			float speed = location.getSpeed();
    			
    			//SmsManager sm = SmsManager.getDefault();
    			//sm.sendTextMessage("5558", null, "Latitude = "+lat+" Longitude = "+lng+" Altitude = "+alt+" Speed = "+speed, null, null);
    			addressString = null;
    			Geocoder gc = new Geocoder(getBaseContext(), Locale.getDefault());
    		    try {
    		      List<Address> addresses = gc.getFromLocation(lat, lng, 1);
    		      StringBuilder sb = new StringBuilder();
    		      Log.d("Geocoder","Inside try ");
    		      Log.d("Geocoder","Addr size: "+addresses.size());
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
    		    } catch (IOException e) {
    		    	Log.d("Geocoder","IOexception");
    		    }
    		    
    			Log.d("DEEPAK", "Deepak: Address = "+addressString);
    			android.telephony.SmsManager sm1 = android.telephony.SmsManager.getDefault();
    			sm1.sendTextMessage(PhoneNumber, null, "Lat:"+lat+"Lng:"+lng+"Addr:"+ addressString, null, null);
    			
    			locationManager.removeUpdates(loclst);
    			
    		}

    		public void onProviderDisabled(String provider) {
    			
    			
    		}

    		public void onProviderEnabled(String provider) {
    			
    			
    		}

    		public void onStatusChanged(String provider, int status, Bundle extras) {
    			
    			
    		}
        	
        }

}