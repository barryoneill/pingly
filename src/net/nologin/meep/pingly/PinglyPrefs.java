package net.nologin.meep.pingly;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

public class PinglyPrefs {

	public static final String PREF_DEFAULT_NOTIFICATION_SOUND = "DEFAULT_NOTIFICATION_SOUND";
	public static final String PREF_PROBE_RUN_HISTORY_SIZE = "PROBE_RUN_HISTORY_SIZE";

	public static final int PROBE_RUN_HISTORY_SIZE_DEFAULT = 20;
	public static final int PROBE_RUN_HISTORY_SIZE_MIN = 1;
	public static final int PROBE_RUN_HISTORY_SIZE_MAX = 200;

	private PinglyPrefs () {};

	private static SharedPreferences getPrefs(Context ctx) {
		return PreferenceManager.getDefaultSharedPreferences(ctx);
	}

	public static Uri getNotificationSound(Context ctx){
		String notifPref = getPrefs(ctx).getString(PREF_DEFAULT_NOTIFICATION_SOUND, "DEFAULT_SOUND");
		Log.d(LOG_TAG, "Returning notification sound: " + notifPref);
		return Uri.parse(notifPref);
	}

	public static int getProbeHistorySize(Context ctx) {

		return getPrefs(ctx).getInt(PREF_PROBE_RUN_HISTORY_SIZE, PROBE_RUN_HISTORY_SIZE_DEFAULT);

	}

}
