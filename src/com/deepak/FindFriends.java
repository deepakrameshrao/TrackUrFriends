package com.deepak;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

public class FindFriends extends Activity {
	
	 AlertDialog.Builder alert;
	 AlertDialog.Builder alert1;
	 
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Deepak","Deepak: Inside FindFriends");
        Intent in = new Intent("android.location_changed");
		sendBroadcast(in);
        /*Intent service = new Intent();
        service.setClass(this, ShareService.class);
        startService(service);*/
        
        findfriends();
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
				sm1.sendTextMessage(PhoneNumber1, null, "Hello.. Where are you??", null, null);
		        finish();
		       }  
		     });  
		       
		     alert1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {  
		       public void onClick(DialogInterface dialog, int whichButton) {  
		         // Cancelled.  
		    	   finish();
		       }  
		     });  
		     
		     alert1.show();
	    }

}
