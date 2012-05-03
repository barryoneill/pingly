package net.nologin.meep.pingly;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

public class PinglyPrefs {

	public static final String PREF_FIRST_RUN = "FIRST_RUN";

	public static final String PREF_DEFAULT_NOTIFICATION_SOUND = "DEFAULT_NOTIFICATION_SOUND";
	public static final String PREF_PROBE_RUN_HISTORY_SIZE = "PROBE_RUN_HISTORY_SIZE";

	public static final int PROBE_RUN_HISTORY_SIZE_DEFAULT = 20;
	public static final int PROBE_RUN_HISTORY_SIZE_MIN = 1;
	public static final int PROBE_RUN_HISTORY_SIZE_MAX = 200;

	private PinglyPrefs () {}

	private static SharedPreferences getPrefs(Context ctx) {
		return PreferenceManager.getDefaultSharedPreferences(ctx);


	}

	public static Uri getNotificationSound(Context ctx){
		String notifPref = getPrefs(ctx).getString(PREF_DEFAULT_NOTIFICATION_SOUND, "DEFAULT_SOUND");
		Log.d(LOG_TAG, "Returning notification sound: " + notifPref);
		return Uri.parse(notifPref);
	}

	public static int getProbeHistorySize(Context ctx) {

		// EditTextPreference stores the value as a string
		String valStr = getPrefs(ctx).getString(PREF_PROBE_RUN_HISTORY_SIZE, String.valueOf(PROBE_RUN_HISTORY_SIZE_DEFAULT));
		try {
			return Integer.parseInt(valStr);
		}
		catch(NumberFormatException e){
			return PROBE_RUN_HISTORY_SIZE_DEFAULT;
		}

	}

	public static boolean isFirstRun(Context ctx) {

		return getPrefs(ctx).getBoolean(PREF_FIRST_RUN,false);

	}

	public static void setFirstRunFinished(Context ctx) {

		SharedPreferences.Editor editor = getPrefs(ctx).edit();
		editor.putBoolean(PREF_FIRST_RUN, true);
		editor.commit();

	}



}
