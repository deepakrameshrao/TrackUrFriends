package com.deepak;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

public class ShareLoc extends Activity {
		
	AlertDialog.Builder alert;
	String PhoneNumber;
	LocationManager locationManager;
	String addressString;
	double lat;
	double lng;
	double alt;
	LocListener1 loclst = new LocListener1();
	 ShareService s = new ShareService();
	boolean flag=false;
	 @Override
     public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         shareLoc();
         if(flag == true){
        	 s.SendLocation(PhoneNumber);
        	 flag = false;
         }
	 }
	 
	 public void shareLoc(){
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
	 	    	 /*locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	 			 locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, loclst);	   
	 	     	 */
	 	    	 flag = true;
	 	     }
 	     });  
 	       
 	     alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {  
 	       public void onClick(DialogInterface dialog, int whichButton) {  
 	         // Cancelled.  
 	       }  
 	     });  
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
 		    
 		   /* Button b1 = new Button(getBaseContext());
 			b1.setText("Latt = " + lat);
 			
 			Button b2 = new Button(getBaseContext());
 			b2.setText("Longitude = " + lng);
 			
 			Button b3 = new Button(getBaseContext());
 			b3.setText("Address = " + addressString);
 			
 			LinearLayout l = new android.widget.LinearLayout(getBaseContext());
 			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,android.widget.LinearLayout.LayoutParams.WRAP_CONTENT); 
 			
 			l.addView(b1,lp);
 			l.addView(b2,lp);
 			l.addView(b3,lp);
 			
 			l.setOrientation(1);
 			setContentView(l,lp);
 			*/
 			Log.d("DEEPAK", "Address = "+addressString);
 			android.telephony.SmsManager sm1 = android.telephony.SmsManager.getDefault();
 			sm1.sendTextMessage(PhoneNumber, null, "Hi.. i'm @ Latitude = "+lat+" Longitude = "+lng+" Address = "+ addressString, null, null);
 			
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
