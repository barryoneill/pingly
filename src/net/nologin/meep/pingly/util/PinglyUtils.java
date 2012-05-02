package net.nologin.meep.pingly.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.activity.BasePinglyActivity;
import net.nologin.meep.pingly.activity.ProbeDetailActivity;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class PinglyUtils {

	private PinglyUtils() {}; // static methods

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
            Log.e(LOG_TAG,"No string resource defined for key '" + name +"'");
            return "n/a";
        }
        return context.getString(resId);
    }

    // TODO: doc
    public static String loadStringForPlural(Context context, String name, int count){

        int resId = context.getResources().getIdentifier(name, "plurals", context.getPackageName());
        // avoiding "android.content.res.Resources$NotFoundException: String resource ID #0x0"
        // perhaps that's what _should_ happen..  Best not to kill the app cause of laziness I guess.
        if(resId <= 0){
            Log.e(LOG_TAG,"No plural resource defined for key '" + name +"'");
            return "n/a";
        }
        return context.getResources().getQuantityString(resId,count,count);
    }

    public static String[] enumToStringValuesArray(Context ctx, Class<?> c, String valueMethod){

        List<String> result = new ArrayList<String>();
        try {
            Method mVal = c.getMethod(valueMethod);

            for(Object enumElem : c.getEnumConstants()){
                String resVal = (String)mVal.invoke(enumElem);
                result.add(loadStringForName(ctx,resVal));
            }
        }
        catch(Exception e){
            Log.e(LOG_TAG,"Error parsing " + c,e);
        }

        return result.toArray(new String[result.size()]);
    }


	public static AlertDialog.Builder getAlertDialogBuilder(Context ctx) {
		return new AlertDialog.Builder(getPinglyDialogContext(ctx));
	}

	public static Context getPinglyDialogContext(Context ctx) {
		return new ContextThemeWrapper(ctx, R.style.PinglyDialogTheme);
	}


	// TODO: move all similar stuff here
	public static void startActivityProbeDetail(Context ctx, long probeId) {

		Log.d(LOG_TAG, "Starting activity: " + ProbeDetailActivity.class.getName());

		Intent intent = new Intent(ctx,ProbeDetailActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		BasePinglyActivity.setIntentExtraProbe(intent, probeId);

		ctx.startActivity(intent);

	}

}
