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

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import net.nologin.meep.pingly.PinglyApplication;
import net.nologin.meep.pingly.db.PinglyDataHelper;
import net.nologin.meep.pingly.db.ProbeDAO;
import net.nologin.meep.pingly.db.ProbeRunDAO;
import net.nologin.meep.pingly.db.ScheduleDAO;
import net.nologin.meep.pingly.model.ProbeRun;
import net.nologin.meep.pingly.model.ScheduleEntry;
import net.nologin.meep.pingly.model.probe.Probe;
import net.nologin.meep.pingly.util.PinglyUtils;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

/**
 * Provides common handlers and routines to all pingly activities
 */
public abstract class BasePinglyActivity extends Activity {
	

	public static final String STATE_PROBERUN_ID = "bundle_currentRunnerID";

	protected ProbeDAO probeDAO;
    protected ScheduleDAO scheduleDAO;
	protected ProbeRunDAO probeRunDAO;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

        PinglyApplication app = (PinglyApplication)getApplication();
        PinglyDataHelper dh = app.getPinglyDataHelper();

		probeDAO = new ProbeDAO(dh);
        scheduleDAO = new ScheduleDAO(dh);
		probeRunDAO = new ProbeRunDAO(dh);
	}	
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	}

	public Probe getIntentExtraProbe() {
		long id = PinglyUtils.getIntentExtraProbeId(getIntent());
		return id >= 0 ? probeDAO.findProbeById(id) : null;
	}

	public ScheduleEntry getIntentExtraScheduleEntry() {
		long id = PinglyUtils.getIntentExtraScheduleEntryId(getIntent());
		return id >= 0 ? scheduleDAO.findById(id) : null;
	}

	public ProbeRun getIntentExtraProbeRun() {
		long id = PinglyUtils.getIntentExtraProbeRunId(getIntent());
		return id >= 0 ? probeRunDAO.findProbeRunById(id) : null;
	}

	public void goToNewProbe(View v) {
		PinglyUtils.startActivityProbeDetail(this,null);
	}

	public void goToProbeList(View v) {

		if (this instanceof ProbeListActivity) {
			Log.d(LOG_TAG, "Already at probe list, ignoring request");
		}
		else{
			Log.d(LOG_TAG, "Going to probe list");
			PinglyUtils.startActivityProbeList(this);
		}
	}

    public void goToScheduleList(View v) {

        if (this instanceof ScheduleListActivity) {
            Log.d(LOG_TAG, "Already at schedule list, ignoring request");
        }
        else{
            Log.d(LOG_TAG, "Going to probe list");
            PinglyUtils.startActivityScheduleList(this);
        }
    }

	public void goToMainDash(View v) {

		if (this instanceof PinglyDashActivity) {
			Log.d(LOG_TAG, "Already at home, ignoring 'home' request");
		}
		else{
			Log.d(LOG_TAG, "Going to dashboard (home)");
			PinglyUtils.startActivityMainDash(this);
		}		
	}


	protected EditText findEditText(int id){
		return (EditText)BasePinglyActivity.this.findViewById(id);
	}



}
