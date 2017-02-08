package com.deepak;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.provider.Contacts.Phones;
import android.provider.ContactsContract.Contacts;
import android.provider.Settings.System;
import android.telephony.PhoneStateListener;
import android.telephony.gsm.GsmCellLocation;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController.AnimationParameters;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class MapsActivity extends MapActivity {
	private MapController mapController;
	private MapView mapView;
	private GeoPoint p = new GeoPoint(12985730, 77597305);
	private LocationManager locationManager;
	public GeoUpdateHandler g = new GeoUpdateHandler();
	public LocListener2 loclst = new LocListener2();
	public double lat1 = 0;
	public double lng1 = 0;
	public List<String> proxregion = new ArrayList<String>();
	int i = 0;
	String proxname;
	AlertDialog.Builder alert;
	Editable t1;
	String PhoneNumber;

	AlertDialog.Builder alert11;
	AlertDialog.Builder alert1;

	MapOverlay positionOverlay;
	int mRadius = 5;
	boolean flag = true;

	ProgressDialog pd;

	List<Overlay> listOfOverlays;
	
	protected static final String PROXIMITY_ALERT = new String(
			"android.intent.action.PROXIMITY_ALERT");

	protected final IntentFilter intentfilter = new IntentFilter(
			PROXIMITY_ALERT);

	static EditText input1;
	// Intent intent = new Intent();
	
	//private static final String NUM_PROJECTION[] = {Contacts.Phones.NUMBER, Contacts.Phones.NAME};

	BroadcastReceiver bc = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			int i1 = proxregion.size();
			Log.d("DEEPAK", "IN onRecieve of brdcast rcvr");
			Log.d("DEEPAK", "i1 = " + i1);
			
			NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			Notification ni = new Notification();
			
			while (i1 > 0) {
				Log.d("DEEPAK", "i1 = " + i1 + "Action comp = "
						+ proxregion.get(i1 - 1) + "Action rcvd ="
						+ intent.getAction());
				if (intent.getAction().equals(
						"android.intent.action." + proxregion.get(i1 - 1))) {
					Log.d("DEEPAK", "IN action " + intent.getAction());
					Bundle b = intent.getExtras();

					if (b.getBoolean(LocationManager.KEY_PROXIMITY_ENTERING)) {
						Log.d("Proximity", "Entering proximity "
								+ proxregion.get(i1 - 1));
						Toast.makeText(context,
								"Entered Proximity " + proxregion.get(i1 - 1),
								Toast.LENGTH_LONG).show();
						ni.setLatestEventInfo(getApplicationContext(), "Entered Proximity " + proxregion.get(i1 - 1), "Entered Proximity " + proxregion.get(i1 - 1), null);
						ni.flags = Notification.FLAG_AUTO_CANCEL;
						nm.notify(0,ni);
						
					} else {
						Log.d("Proximity", "Leaving proximity "
								+ proxregion.get(i1 - 1));
						Toast.makeText(context,
								"Leaving Proximity " + proxregion.get(i1 - 1),
								Toast.LENGTH_LONG).show();
						ni.setLatestEventInfo(getApplicationContext(), "Leaving Proximity " + proxregion.get(i1 - 1), "Leaving Proximity " + proxregion.get(i1 - 1), null);
						ni.flags = Notification.FLAG_AUTO_CANCEL;
						nm.notify(0,ni);
					}

				}
				i1--;
			}

		}
	};

	class MyLocOverlay extends com.google.android.maps.MyLocationOverlay {

		public MyLocOverlay(Context context, MapView mapView) {
			super(context, mapView);
			// TODO Auto-generated constructor stub
		}

	}

	class MapOverlay extends com.google.android.maps.MyLocationOverlay {

		public MapOverlay(Context context, MapView mapView) {
			super(context, mapView);
		}

		
		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
				long when) {
			// ---translate the GeoPoint to screen pixels---
			Log.d("Deepak", "Deepak: ondraw");
			super.draw(canvas, mapView, shadow);
			Point screenPts1 = new Point();
			mapView.getProjection().toPixels(p, screenPts1);

			
			//Deepak for varun
			int latspan = mapView.getLatitudeSpan();
			int lngspan = mapView.getLongitudeSpan();
			//Toast.makeText(MapsActivity.this, "In draw Lat: "+latspan+" lng:"+lngspan, Toast.LENGTH_LONG).show();
			
			// ---add the marker---
			Bitmap bmp = BitmapFactory.decodeResource(getResources(),
					R.drawable.pin);
			canvas.drawBitmap(bmp, screenPts1.x, screenPts1.y, null);

			/* else*/ 
			{
				Point screenPts = new Point();
				mapView.getProjection().toPixels(p, screenPts);
				RectF oval = new RectF(screenPts.x - mRadius, screenPts.y
						- mRadius, screenPts.x + mRadius, screenPts.y + mRadius);

				// Setup the paint
				Paint paint = new Paint();
				paint.setARGB(250, 255, 0, 0);
				paint.setColor(Color.RED);
				paint.setAntiAlias(true);
				// paint.setColor(Color.BLUE);
				paint.setTextSize((float) 15.50);

				Paint backPaint = new Paint();
				// backPaint.setARGB(175, 255, 255, 255);
				backPaint.setAntiAlias(true);
				backPaint.setColor(Color.WHITE);
				RectF backRect = new RectF(screenPts.x + 4 * mRadius,
						screenPts.y - 3 * mRadius, screenPts.x + 65,
						screenPts.y + mRadius);
				// Draw the marker
				// canvas.drawOval(oval, paint);

				//canvas.drawRoundRect(backRect, 5, 5, backPaint);
				//canvas.drawText("Hello", screenPts.x + 5 * mRadius,
					//	screenPts.y, paint);

			}

			return false;
		}

		@Override
		public boolean onTouchEvent(MotionEvent event, MapView mapView) {
			Log.d("Deepak", "Deepak: ontouch. XXX: "+event.getX()+" YYY: "+event.getY());
			//To adjust the pin on the screen perfectly to the clicked point
			p = mapView.getProjection().fromPixels((int) event.getX()-8,
					(int) event.getY()-10);
			lat1 = p.getLatitudeE6() / 1E6;
			lng1 = p.getLongitudeE6() / 1E6;
			super.onTouchEvent(event, mapView);
			return false;
		}

		@Override
		public boolean onTap(GeoPoint point, MapView map) {
			Log.d("Deepak", "Deepak: ontap");

			return super.onTap(point, map);
		}
		
	
	}
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		registerReceiver(bc, intentfilter);
		
		Intent startshareSerivce = new Intent(this, ShareService.class);
		startService(startshareSerivce);
		
		
		setContentView(R.layout.maplayout);

		Button find_friends = (Button) findViewById(R.id.MapButton01);
		find_friends.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				findfriends();

			}
		});

		Button Prox = (Button) findViewById(R.id.MapButton02);
		
		Prox.setAnimation(AnimationUtils.makeInAnimation(this, true));
		Prox.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				showalertProx();
			}
		});
		
		
		final Button Share = (Button) findViewById(R.id.MapButton03);
		Share.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				showalert();
			}
		});

		// create a map view
		RelativeLayout linearLayout = (RelativeLayout) findViewById(R.id.maplayout);
		mapView = (MapView) findViewById(R.id.map);
		
		ZoomControls mZoom = (ZoomControls) mapView.getZoomControls();
		linearLayout.addView(mZoom);
		mapController = mapView.getController();
		int latspan = mapView.getLatitudeSpan();
		int lngspan = mapView.getLongitudeSpan();
		Toast.makeText(this, "Lat: "+latspan+" lng:"+lngspan, Toast.LENGTH_LONG).show();
		mapController.setZoom(17);
		mapController.animateTo(p);
		MapOverlay mapOverlay = new MapOverlay(getBaseContext(), mapView);
		listOfOverlays = mapView.getOverlays();
		listOfOverlays.clear();
		listOfOverlays.add(mapOverlay);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, g);
	}

	@Override
	protected boolean isRouteDisplayed() {
		
		return true;
	}
	

/*	@Override
	protected void onPause() {
		super.onPause();
		if (locationManager != null)
			locationManager.removeUpdates(loclst);
		
	}

	/* Creates the menu items */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case (R.id.share_location):
			showalert();
			break;

		case (R.id.find_friends):
			findfriends();
			break;

		case (R.id.settings):
			setPermissions();
			break;

		case (R.id.quit):
			if (locationManager != null)
				locationManager.removeUpdates(loclst);
			Log.d("DEEPAK", "Closing Session, updates removed");
			finish();
			Log.d("DEEPAK", "On finish called");
			return true;
		}
		return false;
	}

	public void showalert() {
		alert11 = new AlertDialog.Builder(this);

		alert11.setTitle("Details");
		alert11.setMessage("Enter the number");

		SearchLayout(alert11);

		alert11.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				PhoneNumber = input1.getText().toString();

				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 5000, 0, loclst);
			}
		});

		alert11.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Cancelled.
					}
				});

		alert11.show();
		
	}

	public void findfriends() {
		alert1 = new AlertDialog.Builder(this);

		alert1.setTitle("Details");
		alert1.setMessage("Enter the number");
		
		SearchLayout(alert1);
		//input1.setInputType(2);
		//alert1.setView(input1);
		
		
		
		alert1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String PhoneNumber1 = input1.getText().toString();
				android.telephony.SmsManager sm1 = android.telephony.SmsManager
						.getDefault();
				sm1.sendTextMessage(PhoneNumber1, null, "Where are you??",
						null, null);
			}
		});
		
		/*alert1.setNeutralButton("Look-up", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				Intent contact_intent = new Intent(Intent.ACTION_GET_CONTENT);
				contact_intent.setType(Contacts.CONTENT_ITEM_TYPE);
				MapsActivity.this.startActivityForResult(contact_intent, 0);
				
			}
		});*/
		
		
		
	
		
		alert1.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Cancelled.
					}
				});
		
		alert1.show();
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		AlertDialog tmp = alert1.create();
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
        	Log.d("MapsActivity", "Deepak: onActivityResult: contact picker result not OK.");
            return;
        }
        Log.d("MapsActivity", "Deepak: data is "+data.getData());
        Cursor cursor = getContentResolver().query(data.getData(),
                null, null, null, null);
        if ((cursor == null) || (!cursor.moveToFirst())) {
        	Log.d("MapsActivity", "Deepak: onActivityResult: bad contact data, no results found.");
            return;
        }
        
        String number = new String();
        switch (requestCode) {
            case 0:
            	String[] colnames = cursor.getColumnNames();
            	for (String s :colnames){
            		Log.d("Deepak", "Deepak: col = "+s);
            	}
            	//int numberColId = cursor.getColumnIndex(android.provider.Contacts.Phones.NUMBER);
                number = cursor.getString(7);
                Log.d("MapsActivity", "Deepak: The selected number is: " + number);
                break;
            default:
                // TODO: may need exception here.
        }
        input1.setText(number);
	}
	
	
	
	public void setPermissions() {
		Intent in = new Intent(this, setPermission.class);
		startActivity(in);
	}

	public void drawontouch() {
		// Add the MyPositionOverlay
		// MapView mapView = (MapView) findViewById(R.id.mapview);
		positionOverlay = new MapOverlay(getBaseContext(), mapView);
		List<Overlay> overlays = mapView.getOverlays();
		overlays.add(positionOverlay);
		mapView.invalidate();

	}

	public void getAddress() {
		pd = ProgressDialog.show(MapsActivity.this, "Working..",
				"Searching your address", true, false); // Show a progress
														// dialog
		String addressString = "No address found";

		Geocoder gc = new Geocoder(this, Locale.getDefault());
		try {
			List<Address> addresses = gc.getFromLocation(lat1, lng1, 1);
			StringBuilder sb = new StringBuilder();
			Log.d("Geocoder", "Inside try ");
			Log.d("Geocoder", "Addr size: " + addresses.size());
			if (addresses.size() > 0) {
				Log.d("Geocoder", "Inside if loop");
				Address address = addresses.get(0);
				for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
					sb.append(address.getAddressLine(i)).append("\n");
				sb.append(address.getLocality()).append("\n");
				sb.append(address.getPostalCode()).append("\n");
				sb.append(address.getCountryName());
			}
			addressString = sb.toString();
		} catch (IOException e) {
			Log.d("Geocoder", "IOexception");
		}
		pd.dismiss();
		Toast.makeText(
				getBaseContext(),
				"Address is :" + addressString + " lat: " + lat1 + " lng: "
						+ lng1, Toast.LENGTH_LONG).show();
	}

	public void showalertProx() {
		alert = new AlertDialog.Builder(this);

		alert.setTitle("Proximity Alert");
		alert.setMessage("Enter the name for this location");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				t1 = input.getText();
				proxname = t1.toString();

				Log.d("DEEPAK", "PROXNAME = " + proxname);
				String proxregion1 = new String("android.intent.action."
						+ proxname);
				Log.d("DEEPAK", "PROXREGNAME = " + proxregion1);

				unregisterReceiver(bc);
				intentfilter.addAction(proxregion1);
				registerReceiver(bc, intentfilter);

				Intent intent = new Intent(proxregion1);

				proxregion.add(proxname);
				i++;

				PendingIntent proximityIntent = PendingIntent.getBroadcast(
						getBaseContext(), 0, intent, 0);
				locationManager.addProximityAlert(lat1, lng1, 20, -1,
						proximityIntent);
				Toast.makeText(getBaseContext(),
						"Added proximity with name " + proxname+" LAT: "+lat1+" LNG: "+lng1,
						Toast.LENGTH_LONG).show();
				mapView.invalidate();
				

			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Cancelled.
						mapView.invalidate();
					}
				});

		alert.show();

	}
	
	public void SearchLayout(AlertDialog.Builder alert){
		LinearLayout search_layout = (LinearLayout) getLayoutInflater().inflate(R.layout.search_contacts, null);
		alert.setView(search_layout);
		// Set an EditText view to get user input
		input1 = (EditText) search_layout.findViewById(R.id.search_contacttext);
		Button search = (Button) search_layout.findViewById(R.id.search_contactbutton);
		search.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent contact_intent = new Intent(Intent.ACTION_GET_CONTENT);
				contact_intent.setType(Phones.CONTENT_ITEM_TYPE);
				
				MapsActivity.this.startActivityForResult(contact_intent, 0);
			}
		});
	}

	public class GeoUpdateHandler implements LocationListener {

		public void onLocationChanged(Location location) {
			final String TAG = "DEEPAK - MAP";
			// lat1 = location.getLatitude();
			// lng1 = location.getLongitude();
			int lat = (int) (location.getLatitude() * 1E6);
			int lng = (int) (location.getLongitude() * 1E6);
			p = new GeoPoint(lat, lng);
			mapController.setCenter(p);
			MapOverlay mapOverlay = new MapOverlay(getBaseContext(), mapView);
			List<Overlay> listOfOverlays = mapView.getOverlays();
			//listOfOverlays.clear();
			listOfOverlays.add(mapOverlay);

			mapView.invalidate();

			Log.d(TAG, "Latitude = " + lat);
			Log.d(TAG, "Longtitude = " + lng);
			// setContentView(mapView);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	protected void onStop() {
		super.onStop();
		// unregisterReceiver(bc);
	}

	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(bc);
	}
	
	public class LocListener2 implements LocationListener{

		public void onLocationChanged(Location location) {
		 double lat = location.getLatitude();
		double	lng = location.getLongitude();
		double	alt = location.getAltitude();
			float speed = location.getSpeed();
			String addressString = "Edward road, bangalore";
			//SmsManager sm = SmsManager.getDefault();
			//sm.sendTextMessage("5558", null, "Latitude = "+lat+" Longitude = "+lng+" Altitude = "+alt+" Speed = "+speed, null, null);
		/*	addressString = null;
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
		    */
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
