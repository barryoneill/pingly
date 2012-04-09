package net.nologin.meep.pingly.activity;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

import net.nologin.meep.pingly.core.PinglyApplication;
import net.nologin.meep.pingly.db.ProbeDAO;
import net.nologin.meep.pingly.db.ScheduleDAO;
import net.nologin.meep.pingly.model.Probe;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public abstract class BasePinglyActivity extends Activity {
	
	public static final String PARAMETER_PROBE_ID = "param_probe";

	protected ProbeDAO probeDAO;
    protected ScheduleDAO scheduleDAO;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		probeDAO = new ProbeDAO(this);
        scheduleDAO = new ScheduleDAO(this);
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
	}

	protected PinglyApplication getPinglyApp(){
		return (PinglyApplication)getApplication();
	}

	public Probe loadProbeParamIfPresent() {

		Probe result = null;
		
		// populate fields if param set	
		Bundle b = getIntent().getExtras();		
		if(b != null && b.containsKey(PARAMETER_PROBE_ID)){
			Long probeId = b.getLong(PARAMETER_PROBE_ID, -1);
			if(probeId >= 0){
				Log.d(LOG_TAG, "Will be loading probe ID " + probeId);
								
				result = probeDAO.findProbeById(probeId);
				Log.d(LOG_TAG, "Got probe: " + result);
			}
		}
		
		if(result == null){
			Log.e(LOG_TAG, "No probe found");
		}
		
		return result;
	}
	
	// available to all actions (TODO: name? goTO?)
	public void createNewProbe(View v) {

		goToProbeDetails(-1);
	}

	public void goToProbeRunner(long probeId) {
		
		Log.d(LOG_TAG, "Starting activity: " + ProbeRunnerActivity.class.getName());
		
		Intent intent = new Intent(getApplicationContext(),
				ProbeRunnerActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);			
		
		addProbeIdParam(intent, probeId);
		
		startActivity(intent);

	}

	public void goToProbeDetails(long probeId) {
		
		Log.d(LOG_TAG, "Starting activity: " + ProbeDetailActivity.class.getName());
		
		Intent intent = new Intent(getApplicationContext(),
				ProbeDetailActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);			
		
		addProbeIdParam(intent, probeId);
		
		startActivity(intent);

	}

    public void goToProbeScheduling(long probeId) {

        Log.d(LOG_TAG, "Starting activity: " + ScheduleDetailActivity.class.getName());

        Intent intent = new Intent(getApplicationContext(),
                ScheduleDetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        addProbeIdParam(intent, probeId);

        startActivity(intent);

    }
	
	private void addProbeIdParam(Intent intent, long probeId){
		// add parameter if valid
		if(probeId > 0){
			Log.d(LOG_TAG, "Adding probe id param: " + probeId);
			Bundle b = new Bundle();
			b.putLong(ProbeDetailActivity.PARAMETER_PROBE_ID, probeId);
			intent.putExtras(b);	
		}
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




}
