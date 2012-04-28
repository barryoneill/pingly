package net.nologin.meep.pingly.activity;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

import android.widget.EditText;
import net.nologin.meep.pingly.db.ProbeDAO;
import net.nologin.meep.pingly.db.ProbeRunDAO;
import net.nologin.meep.pingly.db.ScheduleDAO;
import net.nologin.meep.pingly.model.ProbeRun;
import net.nologin.meep.pingly.model.ScheduleEntry;
import net.nologin.meep.pingly.model.probe.Probe;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public abstract class BasePinglyActivity extends Activity {
	
	private static final String EXTRA_PROBE_ID = "net.nologin.meep.pingly.intent.extra_probe_id";
	private static final String EXTRA_SCHEDULE_ID = "net.nologin.meep.pingly.intent.extra_schedule_id";
	private static final String EXTRA_PROBE_RUN_ID = "net.nologin.meep.pingly.intent.extra_probe_run_id";

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

		long id = 0;
		Bundle b = getIntent().getExtras();
		if(b != null && b.containsKey(EXTRA_PROBE_ID)){
			id = b.getLong(EXTRA_PROBE_ID, -1);
		}
		return id >= 0 ? probeDAO.findProbeById(id) : null;
	}

	public ScheduleEntry getIntentExtraScheduleEntry() {

		long id = 0;
		Bundle b = getIntent().getExtras();
		if(b != null && b.containsKey(EXTRA_SCHEDULE_ID)){
			id = b.getLong(EXTRA_SCHEDULE_ID, -1);
		}
		return id >= 0 ? scheduleDAO.findById(id) : null;
	}

	public ProbeRun getIntentExtraProbeRun() {

		long id = 0;
		Bundle b = getIntent().getExtras();
		if(b != null && b.containsKey(EXTRA_PROBE_RUN_ID)){
			id = b.getLong(EXTRA_PROBE_RUN_ID, -1);
		}
		return id >= 0 ? probeRunDAO.findProbeRunById(id) : null;
	}

	public static void setIntentExtraProbe(Intent intent, long probeId) {
		addIdExtraInternal(intent, EXTRA_PROBE_ID, probeId);
	}

	public static void setIntentExtraScheduleEntry(Intent intent, long entryId) {
		addIdExtraInternal(intent, EXTRA_SCHEDULE_ID, entryId);
	}

	public static void setIntentExtraProbeRun(Intent intent, long probeRunId) {
		addIdExtraInternal(intent, EXTRA_PROBE_RUN_ID, probeRunId);
	}

	private static void addIdExtraInternal(Intent intent, String constant, long id){
		if(id > 0){
			Log.d(LOG_TAG, "Adding '" + constant + "' param: " + id);
			Bundle b = new Bundle();
			b.putLong(constant, id);
			intent.putExtras(b);
		}
	}


	// available to all actions (TODO: name? goTO?)
	public void createNewProbe(View v) {

		goToProbeDetailsForNew();
	}

	public void goToProbeRunner(long probeId) {
		
		Log.d(LOG_TAG, "Starting activity: " + ProbeRunnerActivity.class.getName());
		
		Intent intent = new Intent(getApplicationContext(),
				ProbeRunnerActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);			
		
		setIntentExtraProbe(intent, probeId);
		
		startActivity(intent);

	}

	public void goToProbeDetailsForNew() {
		goToProbeDetails(-1);
	}

	public void goToProbeDetails(long probeId) {
		
		Log.d(LOG_TAG, "Starting activity: " + ProbeDetailActivity.class.getName());
		
		Intent intent = new Intent(getApplicationContext(),
				ProbeDetailActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);			
		
		setIntentExtraProbe(intent, probeId);
		
		startActivity(intent);

	}


	public void goToProbeRunHistory(long probeId) {

		Log.d(LOG_TAG, "Starting activity: " + ProbeRunHistoryActivity.class.getName());

		Intent intent = new Intent(getApplicationContext(),
				ProbeRunHistoryActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		setIntentExtraProbe(intent, probeId);

		startActivity(intent);

	}


	public void goToProbeScheduling(long probeId) {

        Log.d(LOG_TAG, "Starting activity: " + ScheduleDetailActivity.class.getName());

        Intent intent = new Intent(getApplicationContext(),
                ScheduleDetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        setIntentExtraProbe(intent, probeId);

        startActivity(intent);

    }

	
	public void goToProbeList(View v) {

		if (this instanceof ProbeListActivity) {
			Log.d(LOG_TAG, "Already at probe list, ignoring request");
		}
		else{
			Log.d(LOG_TAG, "Going to probe list");
			Intent intent = new Intent(getApplicationContext(),
					ProbeListActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	}

    public void goToScheduleList(View v) {

        if (this instanceof ScheduleListActivity) {
            Log.d(LOG_TAG, "Already at schedule list, ignoring request");
        }
        else{
            Log.d(LOG_TAG, "Going to probe list");
            Intent intent = new Intent(getApplicationContext(),
                    ScheduleListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

	public void goHome(View v) {

		if (this instanceof PinglyDashActivity) {
			Log.d(LOG_TAG, "Already at home, ignoring 'home' request");
		}
		else{
			Log.d(LOG_TAG, "Going to dashboard (home)");
			Intent intent = new Intent(getApplicationContext(),
					PinglyDashActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}		
	}


	protected EditText findEditText(int id){
		return (EditText)BasePinglyActivity.this.findViewById(id);
	}



}
