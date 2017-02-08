package com.deepak;

import android.app.PendingIntent;
import android.appwidget.*;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

public class LocationWidgetProvider extends AppWidgetProvider {
	
	static AppWidgetManager widgetManager;
	static int widgetid;
	RemoteViews locationView;
	RemoteViews views;
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		widgetManager = appWidgetManager;
		Log.d("Deepak","Deepak: onUpdate. app="+appWidgetManager+"wid"+widgetManager);
		int N = appWidgetIds.length;
		widgetManager = appWidgetManager;
		for(int i=0; i<N; i++){
			views = new RemoteViews(context.getPackageName(), R.layout.main);
            Intent intent = new Intent(context, LocationWidget.class);
            Intent intent1 = new Intent(context, FindFriends.class);
            
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            PendingIntent pendingIntent1 = PendingIntent.getActivity(context, 0, intent1, 0);
            
            views.setOnClickPendingIntent(R.id.Button02 , pendingIntent);
            views.setOnClickPendingIntent(R.id.Button01, pendingIntent1);
            widgetid = appWidgetIds[i];
            
           // RemoteViews view1 = new RemoteViews(context.getPackageName(), R.layout.nestedremoteviews);
            
            //views.addView(R.id.remoteTextView, view1);
            
			appWidgetManager.updateAppWidget(appWidgetIds[i],views);
		}
		
		
		
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		Log.d("Deepak","Deepak: On recv()");
		String action = intent.getAction();
		if(action.equals("android.location_changed")){
			Toast.makeText(context, "wid man "+widgetManager+" "+widgetid, Toast.LENGTH_LONG).show();
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main);
			views.setTextViewText(R.id.Text01, "He is at bangalore");
			widgetManager = AppWidgetManager.getInstance(context);
			widgetManager.updateAppWidget(widgetid, views);
		}
	}
	
}
