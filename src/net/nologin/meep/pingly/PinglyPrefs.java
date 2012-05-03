package net.nologin.meep.pingly;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

public class PinglyPrefs {

	public static final int PROBE_RUN_HISTORY_SIZE_DEFAULT = 20;
	public static final int PROBE_RUN_HISTORY_SIZE_MIN = 1;
	public static final int PROBE_RUN_HISTORY_SIZE_MAX = 200;

	private PinglyPrefs () {}

	private static SharedPreferences getPrefs(Context ctx) {
		return PreferenceManager.getDefaultSharedPreferences(ctx);
	}

	public static Uri getNotificationSound(Context ctx){
		String soundPrefKey = ctx.getString(R.string.prefs_key_NOTIFICATION_SOUND);
		String notifPref = getPrefs(ctx).getString(soundPrefKey, "DEFAULT_SOUND");
		Log.d(LOG_TAG, "Returning notification sound: " + notifPref);
		return Uri.parse(notifPref);
	}

	public static boolean areVibrationsAllowed(Context ctx) {
		String vibrationPrefKey = ctx.getString(R.string.prefs_key_NOTIFICATION_ALLOW_VIBRATE);
		return getPrefs(ctx).getBoolean(vibrationPrefKey,false);
	}

	public static int getProbeHistorySize(Context ctx) {

		// EditTextPreference stores the value as a string
		String histSizeKey = ctx.getString(R.string.prefs_key_PROBERUN_HIST_SIZE);
		String valStr = getPrefs(ctx).getString(histSizeKey, String.valueOf(PROBE_RUN_HISTORY_SIZE_DEFAULT));
		try {
			return Integer.parseInt(valStr);
		}
		catch(NumberFormatException e){
			return PROBE_RUN_HISTORY_SIZE_DEFAULT;
		}

	}

	public static boolean isFirstRunComplete(Context ctx) {

		String frcKey = ctx.getString(R.string.prefs_key_FIRST_RUN_COMPLETE);
		return getPrefs(ctx).getBoolean(frcKey,false);

	}

	public static void setFirstRunComplete(Context ctx) {

		String frcKey = ctx.getString(R.string.prefs_key_FIRST_RUN_COMPLETE);
		SharedPreferences.Editor editor = getPrefs(ctx).edit();
		editor.putBoolean(frcKey, true);
		editor.commit();

	}



}
