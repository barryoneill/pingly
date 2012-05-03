package net.nologin.meep.pingly.activity;

import android.content.Context;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.util.AttributeSet;
import net.nologin.meep.pingly.PinglyPrefs;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.util.ui.NumberRangeTextWatcher;

public class SettingsActivity extends PreferenceActivity {


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.main_preferences);

		// let's force a range
		EditTextPreference runHistSize = (EditTextPreference) findPreference("PROBE_RUN_HISTORY_SIZE");

		String hint = "Between " + PinglyPrefs.PROBE_RUN_HISTORY_SIZE_MIN
						+ " and " + PinglyPrefs.PROBE_RUN_HISTORY_SIZE_MAX;

		runHistSize.setDefaultValue(PinglyPrefs.PROBE_RUN_HISTORY_SIZE_DEFAULT);
		runHistSize.getEditText().setHint(hint);
		runHistSize.getEditText().addTextChangedListener(
				new NumberRangeTextWatcher(
						PinglyPrefs.PROBE_RUN_HISTORY_SIZE_MIN,
						PinglyPrefs.PROBE_RUN_HISTORY_SIZE_MAX
				));

	}



}