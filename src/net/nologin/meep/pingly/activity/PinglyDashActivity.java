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
import android.util.Log;
import android.view.View;
import net.nologin.meep.pingly.PinglyPrefs;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.util.PinglyUtils;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

public class PinglyDashActivity extends BasePinglyActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_dashboard);

		firstRunCheck();

	}

	// registered onClick handler on all the dashboard buttons
	public void dashButtonClicked(View v) {
		int id = v.getId();
		switch (id) {

			case R.id.but_dash_showProbes:

				Log.d(LOG_TAG, "Dashboard selection - probe list");
				PinglyUtils.startActivityProbeList(this);
				break;

			case R.id.but_dash_newProbe:

				Log.d(LOG_TAG, "Dashboard selection - new probe");
				PinglyUtils.startActivityProbeDetail(this,null);
				break;

			case R.id.but_dash_schedule:

				Log.d(LOG_TAG, "Dashboard selection - schedule");
				PinglyUtils.startActivityScheduleList(this);
				break;

			case R.id.but_dash_settings:

				Log.d(LOG_TAG, "Dashboard selection - settings");
				PinglyUtils.startActivitySettings(this);
				break;

			default:
				break;
		}
	}


	public void firstRunCheck() {

		if(PinglyPrefs.isFirstRunComplete(this)){
			return;
		}

		Log.i(LOG_TAG, "First run of Pingly, performing setup");

		// generate some sample probes
		probeDAO.generateFirstRunItems();

		// TODO: perhaps a 'welcome to pingly' message

		// mark first run check as finished
		PinglyPrefs.setFirstRunComplete(this);

	}

}