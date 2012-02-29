package net.nologin.meep.pingly.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class PinglyUtils {

	public static boolean activeNetConnectionPresent(Context ctx) {
		
	    ConnectivityManager cm = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo ni = cm.getActiveNetworkInfo();
	    return ni != null && ni.isConnected();
	    
	}
	
	public static String loadStringForName(Context context, String name){

        int resId = context.getResources().getIdentifier(name, "string", context.getPackageName());
        // avoiding "android.content.res.Resources$NotFoundException: String resource ID #0x0"
        // perhaps that's what _should_ happen..  Best not to kill the app cause of laziness I guess.
        if(resId <= 0){
            Log.e("Pingly","No string resource defined for key '" + name +"'");
            return "n/a";
        }
        return context.getString(resId);
    }
}
