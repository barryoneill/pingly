package net.nologin.meep.pingly.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class PinglyUtils {

	public static boolean activeNetConnectionPresent(Context ctx) {
		
	    ConnectivityManager cm = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo ni = cm.getActiveNetworkInfo();
	    return ni != null && ni.isConnected();
	    
	}
	
	
}
