package net.nologin.meep.pingly.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.Toast;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.activity.*;
import net.nologin.meep.pingly.model.ProbeRun;
import net.nologin.meep.pingly.model.ScheduleEntry;
import net.nologin.meep.pingly.model.probe.Probe;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

public class PinglyUtils {

	private static final String INTENT_EXTRA_PROBE_ID = "net.nologin.meep.pingly.intent.extra_probe_id";
	private static final String INTENT_EXTRA_SCHEDULE_ID = "net.nologin.meep.pingly.intent.extra_schedule_id";
	private static final String INTENT_EXTRA_PROBE_RUN_ID = "net.nologin.meep.pingly.intent.extra_probe_run_id";


	private PinglyUtils() {} // static methods

	public static String getPinglyVersionName(Context ctx) {

		try {
			PackageInfo info = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(),0);
			return info.versionName;

		} catch (PackageManager.NameNotFoundException e) {
			Log.e(LOG_TAG,"Error getting version number! " + e.getMessage(), e);
			return "";
		}

	}

	/**
	 * Convenience method for showing a toast message
	 * @param ctx The context
	 * @param msgResId The message resource ID.
	 * @param msgParams If not empty, then the resource pointed to by msgResId will be assumed to be a format
	 *                     string with placeholders for each of these params
	 */
	public static void showToast(Context ctx, int msgResId, Object... msgParams){

		Toast toast;

		if(msgParams.length < 1){
			toast = Toast.makeText(ctx,msgResId,Toast.LENGTH_SHORT);
		}
		else{
			String fmt = ctx.getString(msgResId);

			String msg = String.format(fmt,msgParams);
			toast = Toast.makeText(ctx,msg,Toast.LENGTH_LONG);
		}

		toast.show();

	}

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


	/* --------- centralised intent extra set/getters ------------ */

	public static long getIntentExtraProbeId(Intent intent) {
		return getIntentExtraIdInternal(intent, INTENT_EXTRA_PROBE_ID);
	}

	public static long getIntentExtraScheduleEntryId(Intent intent) {
		return getIntentExtraIdInternal(intent, INTENT_EXTRA_SCHEDULE_ID);
	}

	public static long getIntentExtraProbeRunId(Intent intent) {
		return getIntentExtraIdInternal(intent, INTENT_EXTRA_PROBE_RUN_ID);
	}

	private static long getIntentExtraIdInternal(Intent intent, String key) {
		Bundle b = intent.getExtras();
		if(b != null && b.containsKey(key)){
			return b.getLong(key, -1);
		}
		return -1;
	}

	public static void setIntentExtraProbeId(Intent intent, long id) {
		setIntentExtraInternal(intent, INTENT_EXTRA_PROBE_ID, id);
	}

	public static void setIntentExtraScheduleEntryId(Intent intent, long id) {
		setIntentExtraInternal(intent, INTENT_EXTRA_SCHEDULE_ID, id);
	}

	public static void setIntentExtraProbeRunId(Intent intent, long id) {
		setIntentExtraInternal(intent, INTENT_EXTRA_PROBE_RUN_ID, id);
	}


	private static void setIntentExtraInternal(Intent intent, String constant, long id){
		if(id > 0){
			Log.d(LOG_TAG, "Adding '" + constant + "' param: " + id);
			Bundle b = new Bundle();
			b.putLong(constant, id);
			intent.putExtras(b);
		}
	}

	/* ---------------- activity-starting convenience methods ------------------ */

	public static void startActivityMainDash(Context ctx) {
		startActivityInternal(ctx, PinglyDashActivity.class, true);
	}

	public static void startActivityProbeList(Context ctx) {
		startActivityInternal(ctx,ProbeListActivity.class, true);
	}

	public static void startActivityScheduleList(Context ctx) {
		startActivityInternal(ctx, ScheduleListActivity.class, true);
	}

	public static void startActivitySettings(Context ctx) {
		startActivityInternal(ctx, SettingsActivity.class, true);
	}

	public static void startActivityProbeDetail(Context ctx, Probe probe) {
		startActivityInternal(ctx, ProbeDetailActivity.class, true, probe, null, null);
	}

	public static void startActivityProbeRunHistory(Context ctx, Probe probe) {
		startActivityInternal(ctx, ProbeRunHistoryActivity.class, true, probe, null, null);
	}

	public static void startActivityProbeRunner(Context ctx, Probe probe) {
		startActivityInternal(ctx, ProbeRunnerActivity.class, true, probe, null, null);
	}

	public static void startActivityScheduleEntryDetail(Context ctx, Probe probe) {
		startActivityInternal(ctx, ScheduleDetailActivity.class, true, probe, null, null);
	}

	public static void startActivityScheduleEntryDetail(Context ctx, ScheduleEntry entry) {
		startActivityInternal(ctx, ScheduleDetailActivity.class, true, null, entry, null);
	}

	private static void startActivityInternal(Context ctx, Class activityClass,boolean clearTop) {
		startActivityInternal(ctx, activityClass, clearTop, null, null, null);
	}

	private static void startActivityInternal(Context ctx, Class activityClass,boolean clearTop,
											  Probe probeParam,
											  ScheduleEntry scheduleParam,
											  ProbeRun probeRunParam) {

		Log.d(LOG_TAG, "Starting activity: " + activityClass.getName());

		Intent intent = new Intent(ctx,activityClass);

		if(clearTop){
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		}

		if(probeParam != null && probeParam.id > 0){
			setIntentExtraProbeId(intent, probeParam.id);
		}
		if(scheduleParam != null && scheduleParam.id > 0){
			setIntentExtraScheduleEntryId(intent, scheduleParam.id);
		}
		if(probeRunParam != null && probeRunParam.id > 0) {
			setIntentExtraProbeRunId(intent, probeRunParam.id);
		}

		ctx.startActivity(intent);

	}


	/* ------------------------------------------------------------------------------- */

}
