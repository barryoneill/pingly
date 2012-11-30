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
package net.nologin.meep.pingly.activity;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import net.nologin.meep.pingly.PinglyPrefs;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.util.ui.NumberRangeTextWatcher;

public class SettingsActivity extends PreferenceActivity {


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.main_preferences);

		// let's force a range
		String histSizeKey = this.getString(R.string.prefs_key_PROBERUN_HIST_SIZE);
		EditTextPreference runHistSize = (EditTextPreference) findPreference(histSizeKey);

		String hintFmt = getString(R.string.prefs_proberun_histsize_hint);
		String hint = String.format(hintFmt,
					PinglyPrefs.PROBE_RUN_HISTORY_SIZE_MIN, PinglyPrefs.PROBE_RUN_HISTORY_SIZE_MAX);

		runHistSize.setDefaultValue(PinglyPrefs.PROBE_RUN_HISTORY_SIZE_DEFAULT);
		runHistSize.getEditText().setHint(hint);
		runHistSize.getEditText().addTextChangedListener(
				new NumberRangeTextWatcher(
						PinglyPrefs.PROBE_RUN_HISTORY_SIZE_MIN,
						PinglyPrefs.PROBE_RUN_HISTORY_SIZE_MAX
				));

	}



}