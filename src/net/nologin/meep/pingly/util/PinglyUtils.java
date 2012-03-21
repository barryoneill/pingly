package net.nologin.meep.pingly.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import net.nologin.meep.pingly.PinglyConstants;
import net.nologin.meep.pingly.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

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


    public static String[] enumToResourceValueArray(Class c, String resourceNameMethod, Context ctx){

        List<String> result = new ArrayList<String>();
        try {
            Method m = c.getMethod(resourceNameMethod);
            for(Object enumElem : c.getEnumConstants()){
                String resName = (String)m.invoke(enumElem);
                result.add(loadStringForName(ctx,resName));
            }
        }
        catch(Exception e){
            Log.e(PinglyConstants.LOG_TAG,"Error parsing " + c,e);
        }
            
        return result.toArray(new String[result.size()]);
    }

}
