package net.nologin.meep.pingly.core;

import android.app.IntentService;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG_PROTO;

public class ProbeRunnerInteractiveService extends IntentService {

	public static final String ACTION_UPDATE = "net.nologin.meep.pingly.core.ProbeRunnerInteractiveService.ACTION_UPDATE";
	public static final String ACTION_FINISHED = "net.nologin.meep.pingly.core.ProbeRunnerInteractiveService.ACTION_FINISHED";

	public static final String EXTRA_PROBERUN_ID = "net.nologin.meep.pingly.core.ProbeRunnerInteractiveService.EXTRA_DATA_RUN_ID";
	public static final String EXTRA_DATA_LOGTEST = "net.nologin.meep.pingly.core.ProbeRunnerInteractiveService.EXTRA_DATA_LOGTEST";

	public ProbeRunnerInteractiveService() {
		super("ProbeRunnerInteractiveService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		// callbackIntent = (PendingIntent) intent.getParcelableExtra(EXTRA_PROBERUN_ID);
		long probeRunId = intent.getLongExtra(EXTRA_PROBERUN_ID,0);

		Log.e(LOG_TAG_PROTO, "handling onHandleIntent - probe run " + probeRunId);

		for(int i=1;i<4;i++){

			Log.e(LOG_TAG_PROTO, "sending update broadcast #" + i + " to " + ACTION_UPDATE);

			Intent updateIntent = new Intent(ACTION_UPDATE);
			updateIntent.putExtra(EXTRA_DATA_LOGTEST, "service update #" + i);
			updateIntent.putExtra(EXTRA_PROBERUN_ID, probeRunId);
			sendBroadcast(updateIntent);

			Log.e(LOG_TAG_PROTO,"sleeping for a second");
			SystemClock.sleep(1000);
		}


		Log.e(LOG_TAG_PROTO,"finished sleeping, doing callback");

		Intent doneIntent = new Intent(ACTION_FINISHED);
		doneIntent.putExtra(EXTRA_PROBERUN_ID, probeRunId);
		sendBroadcast(doneIntent);


	}




}