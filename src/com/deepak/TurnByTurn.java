package com.deepak;

import android.os.Bundle;

import com.google.android.maps.MapActivity;

import java.io.IOException;
import java.net.*;

import android.net.*;

public class TurnByTurn extends MapActivity {

	@Override
	protected void onCreate(Bundle icicle) {
		// TODO Auto-generated method stub
		super.onCreate(icicle);
		try {
			Socket sock = new Socket("10.50.200.165", 1001);
			SocketAddress addr = new SocketAddress() {
			};
			sock.connect(sock.getRemoteSocketAddress());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		
		
		return true;
	}

	
	
	
}
