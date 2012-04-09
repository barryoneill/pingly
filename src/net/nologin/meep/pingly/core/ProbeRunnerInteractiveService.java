package net.nologin.meep.pingly.core;

import android.app.IntentService;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import net.nologin.meep.pingly.model.InteractiveProbeRunInfo;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

public class ProbeRunnerInteractiveService extends IntentService {

	public static final String ACTION_UPDATE = "net.nologin.meep.pingly.core.ProbeRunnerInteractiveService.ACTION_UPDATE";

	public static final String EXTRA_PROBERUN_ID = "net.nologin.meep.pingly.core.ProbeRunnerInteractiveService.EXTRA_DATA_RUN_ID";

	public ProbeRunnerInteractiveService() {
		super("ProbeRunnerInteractiveService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		long probeRunId = intent.getLongExtra(EXTRA_PROBERUN_ID,0);

		InteractiveProbeRunInfo runInfo = ((PinglyApplication)getApplication()).getProbeRunInfo(probeRunId, true);
		runInfo.status = InteractiveProbeRunInfo.RunStatus.Running;

		Log.e(LOG_TAG, "handling onHandleIntent - probe run " + probeRunId);

		for(int i=1;i<4;i++){

			if(runInfo.isFinished()){
				Log.d(LOG_TAG, "Caught finished flag");
				return;
			}

			Log.d(LOG_TAG, "sending update broadcast #" + i + " to " + ACTION_UPDATE);

			runInfo.writeLogLine("This is loop iteration " + i);
			broadcastUpdate(probeRunId);

			SystemClock.sleep(1000);
		}

		if(runInfo.isFinished()){
			Log.d(LOG_TAG, "Caught finished flag");
			return;
		}

		Log.e(LOG_TAG,"finished sleeping, doing callback");
		runInfo.writeLogLine("and I'm done");
		runInfo.setFinishedWithSuccess();

		broadcastUpdate(probeRunId);

	}

	private void broadcastUpdate(long probeRunId){

		Intent updateIntent = new Intent(ACTION_UPDATE);
		updateIntent.putExtra(EXTRA_PROBERUN_ID, probeRunId);
		sendBroadcast(updateIntent);

	}


}