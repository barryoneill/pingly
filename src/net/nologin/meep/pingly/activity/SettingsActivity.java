package net.nologin.meep.pingly.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import net.nologin.meep.pingly.R;

public class SettingsActivity extends PreferenceActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.main_preferences);

    }


}