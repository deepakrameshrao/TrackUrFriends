package com.deepak;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ZoomControls;
import com.google.android.maps.*;

public class ShareUrLocMap extends Activity {
	
	private MapController mapController;
	private MapView mapView;
	private GeoPoint p = new GeoPoint(12985730,77597305);
	private LocationManager locationManager;
	public GeoUpdateHandler g = new GeoUpdateHandler();
	public static double lat1 = 0;
	public static double lng1 = 0;
	public List<String> proxregion = new ArrayList<String>();
    int i = 0;
	String proxname;
    AlertDialog.Builder alert;
	Editable t1;
	
	MapOverlay positionOverlay;
	int mRadius = 5;
	boolean flag = true;
	
	ProgressDialog pd;
	
	protected static final String PROXIMITY_ALERT = new String("android.intent.action.PROXIMITY_ALERT");
	
	protected final IntentFilter intentfilter = new IntentFilter(PROXIMITY_ALERT);
	
	//Intent intent = new Intent(); 
	
	BroadcastReceiver bc = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			int i1 = proxregion.size();
			Log.d("DEEPAK", "IN onRecieve of brdcast rcvr");
			Log.d("DEEPAK","i1 = "+i1);
			while(i1>0)
			{
			   Log.d("DEEPAK","i1 = "+i1+"Action comp = "+proxregion.get(i1-1)+"Action rcvd ="+intent.getAction());
	    	   if(intent.getAction().equals("android.intent.action."+proxregion.get(i1-1)))
	    	   {
	              Log.d("DEEPAK","IN action "+intent.getAction());
	              Bundle b = intent.getExtras();
	            
	        	  if(b.getBoolean(LocationManager.KEY_PROXIMITY_ENTERING)){     	
	           	      Log.d("Proximity","Entering proximity "+proxregion.get(i1-1));
	        	      Toast.makeText(context, "Entered Proximity "+proxregion.get(i1-1),Toast.LENGTH_LONG).show();
	        	   }
	        	   else
	        	   {
	        		  Log.d("Proximity","Leaving proximity "+proxregion.get(i1-1));
	        	      Toast.makeText(context, "Leaving Proximity "+proxregion.get(i1-1),Toast.LENGTH_LONG).show();
	        	   }
	            
	          }
	    	   i1--;
			}
			
		}
	};
	
	 class MapOverlay extends com.google.android.maps.Overlay
	    {
		    
	        public boolean draw(Canvas canvas, MapView mapView, 
	        boolean shadow, long when) 
	        {    
	            //---translate the GeoPoint to screen pixels---
	           
	            Point screenPts = new Point();
	            mapView.getProjection().toPixels(p, screenPts);
	 
	            //---add the marker---
	            Bitmap bmp = BitmapFactory.decodeResource(
	                getResources(), R.drawable.doticon);            
	            canvas.drawBitmap(bmp, screenPts.x, screenPts.y-50, null);
	           
	           /* else
            {
	            Point screenPts = new Point();
	            mapView.getProjection().toPixels(p, screenPts);
	            RectF oval = new RectF(screenPts.x - mRadius, screenPts.y - mRadius,
	                                   screenPts.x + mRadius, screenPts.y + mRadius);
	            // Setup the paint
	            Paint paint = new Paint();
	            paint.setARGB(250, 255, 0, 0);
	            paint.setAntiAlias(true);
	            paint.setFakeBoldText(true);
	            Paint backPaint = new Paint();
	            backPaint.setARGB(175, 50, 50, 50);
	            backPaint.setAntiAlias(true);
	            RectF backRect = new RectF(screenPts.x + 2 + mRadius,
	                                       screenPts.y - 3*mRadius,
	                                       screenPts.x + 65, screenPts.y + mRadius);
	            // Draw the marker
	            canvas.drawOval(oval, paint);
	            canvas.drawRoundRect(backRect, 5, 5, backPaint);
               
	            
            }*/
	            
	            super.draw(canvas, mapView, shadow);  
	            return true;
	        }
	        
	        /*public boolean onTouchEvent(MotionEvent event, MapView mapView) 
	        {   
	        	
	        	p = mapView.getProjection().fromPixels(
		                    (int) event.getX(),
		                    (int) event.getY());
	        	lat1 = p.getLatitudeE6()/1E6;
	        	lng1 = p.getLongitudeE6()/1E6;
	        	//drawontouch();
		       /* Toast.makeText(getBaseContext(), 
                        p.getLatitudeE6() / 1E6 + "," + 
                        p.getLongitudeE6() /1E6 , 
                        Toast.LENGTH_SHORT).show();*                          
	            return false;
	        }
	        */ 
	        
	    } 	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState); 
        registerReceiver(bc, intentfilter);
      
        setContentView(R.layout.main2);
        
        
     // create a map view
		/*RelativeLayout linearLayout = (RelativeLayout) findViewById(R.id.mainlayout);
		mapView = (MapView) findViewById(R.id.mapview);
		ZoomControls mZoom = (ZoomControls) mapView.getZoomControls();
		linearLayout.addView(mZoom);
		mapController = mapView.getController();
		mapController.setZoom(16);
		int lat = (int) (getIntent().getDoubleExtra("Latitude", 0.00) * 1E6);
		int lng = (int) (getIntent().getDoubleExtra("Longitude", 0.00) * 1E6);
		Log.d("MapActivity","Deepak: lat n lng ="+lat+" "+lng);
		GeoPoint point = new GeoPoint(lat, lng);
		p =  new GeoPoint(lat, lng);
		mapController.setCenter(point);
		
		MapOverlay mapOverlay = new MapOverlay();
        List<Overlay> listOfOverlays = mapView.getOverlays();
        listOfOverlays.clear();
        listOfOverlays.add(mapOverlay); 
        
        mapView.invalidate();
		mapController.setCenter(point);
	*/
        //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
			//	0, g);	
       
	
    }

	/*@Override
	protected boolean isRouteDisplayed() {
		
		return false;
	}*/
	
	/* Creates the menu items */
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}



	
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    }
	    return false;
	}
	
	public void drawontouch()
	{
		 // Add the MyPositionOverlay
		//MapView mapView = (MapView) findViewById(R.id.mapview);
		 positionOverlay = new MapOverlay();
		 List<Overlay> overlays = mapView.getOverlays();
		 overlays.add(positionOverlay);
		 mapView.invalidate();

	}

	
public void getAddress(){
	     pd = ProgressDialog.show(ShareUrLocMap.this, "Working..", "Searching your address", true, false); //Show a progress dialog
	     String addressString = "No address found";
	     
	     Geocoder gc = new Geocoder(this, Locale.getDefault());
		    try {
		      List<Address> addresses = gc.getFromLocation(lat1, lng1, 1);
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
		    pd.dismiss();
		    Toast.makeText(getBaseContext(),"Address is :"+addressString+" lat: "+lat1+" lng: "+lng1,Toast.LENGTH_LONG).show();
}

public void showalert()
{
	alert = new AlertDialog.Builder(this);  
    
    alert.setTitle("Proximity Alert");  
    alert.setMessage("Enter the name for this proximity");    
       
     // Set an EditText view to get user input   
     final EditText input = new EditText(this);  
     alert.setView(input);  
       
     alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
     public void onClick(DialogInterface dialog, int whichButton) {  
        
      t1 = input.getText();
      proxname = t1.toString();
       
       Log.d("DEEPAK","PROXNAME = "+proxname);
	    String  proxregion1 = new String("android.intent.action."+proxname); 
	      Log.d("DEEPAK","PROXREGNAME = "+proxregion1);
	      
	      unregisterReceiver(bc);
	      intentfilter.addAction(proxregion1);
	      registerReceiver(bc, intentfilter);
	      
	       Intent intent = new Intent(proxregion1); 
	    
	    proxregion.add(proxname);
	    i++;
	    
 	PendingIntent proximityIntent = PendingIntent.getBroadcast(getBaseContext(),0, intent, 0);   
 	locationManager.addProximityAlert(lat1, lng1, 20, -1, proximityIntent);
 	 Toast.makeText(getBaseContext(), "Added proximity with name "+proxname,Toast.LENGTH_LONG).show();
       
       }  
     });  
       
     alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {  
       public void onClick(DialogInterface dialog, int whichButton) {  
         // Cancelled.  
       }  
     });  
}
	
	
	public class GeoUpdateHandler implements LocationListener {

		public void onLocationChanged(Location location) {
			final String TAG = "DEEPAK - MAP";
			//lat1 = location.getLatitude();
			//lng1 = location.getLongitude();
			int lat = (int) (location.getLatitude() * 1E6);
			int lng = (int) (location.getLongitude() * 1E6);
			p = new GeoPoint(lat, lng);
			mapController.setCenter(p);
		
			MapOverlay mapOverlay = new MapOverlay();
	        List<Overlay> listOfOverlays = mapView.getOverlays();
	        listOfOverlays.clear();
	        listOfOverlays.add(mapOverlay);        
	 
	        mapView.invalidate();
			
            Log.d(TAG,"Latitude = " + lat);
			Log.d(TAG,"Longtitude = " + lng);			
//			setContentView(mapView);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}
	
	protected void onStop(){
		super.onStop();
		//unregisterReceiver(bc);
	}
  
	protected void onDestroy(){
		super.onDestroy();
		unregisterReceiver(bc);
	}
}
