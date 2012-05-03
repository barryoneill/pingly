package net.nologin.meep.pingly.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import net.nologin.meep.pingly.db.ProbeDAO;
import net.nologin.meep.pingly.db.ProbeRunDAO;
import net.nologin.meep.pingly.db.ScheduleDAO;
import net.nologin.meep.pingly.model.ProbeRun;
import net.nologin.meep.pingly.model.ScheduleEntry;
import net.nologin.meep.pingly.model.probe.Probe;
import net.nologin.meep.pingly.util.PinglyUtils;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

public abstract class BasePinglyActivity extends Activity {
	

	public static final String STATE_PROBERUN_ID = "bundle_currentRunnerID";

	protected ProbeDAO probeDAO;
    protected ScheduleDAO scheduleDAO;
	protected ProbeRunDAO probeRunDAO;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		probeDAO = new ProbeDAO(this);
        scheduleDAO = new ScheduleDAO(this);
		probeRunDAO = new ProbeRunDAO(this);
	}	
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    if (probeDAO != null) {
	        probeDAO.close();
	    }
        if (scheduleDAO != null) {
            scheduleDAO.close();
        }
		if (probeRunDAO != null) {
			probeRunDAO.close();
		}
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
		PinglyUtils.startActivityProbeDetail(this);
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
