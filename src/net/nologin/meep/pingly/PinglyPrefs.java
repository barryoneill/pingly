/*
 *    Pingly - A simple app for checking for signs of life in hosts/services.
 *    Copyright 2012 Barry O'Neill
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package net.nologin.meep.pingly;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

/**
 * Utility class for easy access/setting of app preferences.
 */
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
