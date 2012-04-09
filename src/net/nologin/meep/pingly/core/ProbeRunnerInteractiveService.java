package net.nologin.meep.pingly.core;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import net.nologin.meep.pingly.model.InteractiveProbeRunInfo;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

public class ProbeRunnerInteractiveService extends Service {

	public static final String ACTION_UPDATE = "net.nologin.meep.pingly.core.ProbeRunnerInteractiveService.ACTION_UPDATE";
	public static final String EXTRA_PROBERUN_ID = "net.nologin.meep.pingly.core.ProbeRunnerInteractiveService.EXTRA_DATA_RUN_ID";

	ProbeRunnerAsyncTask runningTask;

	public void onCreate() {
		super.onCreate();
		Log.d(LOG_TAG, "ProbeRunnerInteractiveService created");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.d(LOG_TAG, "ProbeRunnerInteractiveService started");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(LOG_TAG, "ProbeRunnerInteractiveService destroyed");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		long probeRunId = intent.getLongExtra(EXTRA_PROBERUN_ID, 0);

		InteractiveProbeRunInfo runInfo = ((PinglyApplication) getApplication()).getProbeRunInfo(probeRunId, true);

		if(runningTask != null){
			// cancel the previous task
			runningTask.cancel(true);
		}

		runningTask = new ProbeRunnerAsyncTask(runInfo);
		runningTask.execute();

		return START_NOT_STICKY;

	}



	private class ProbeRunnerAsyncTask extends AsyncTask<Void, Void, Void> {

		private InteractiveProbeRunInfo runInfo;

		public ProbeRunnerAsyncTask(InteractiveProbeRunInfo runInfo){
			this.runInfo = runInfo;
		}

		@Override
		protected Void doInBackground(Void... voids) {

			runInfo.status = InteractiveProbeRunInfo.RunStatus.Running;

			Log.e(LOG_TAG, "handling onHandleIntent - probe run " + runInfo.probeRunId);

			for (int i = 1; i < 4; i++) {

				if(probeRunCancelled(runInfo)){
					return null;
				}

				Log.d(LOG_TAG, "sending update broadcast #" + i + " to " + ACTION_UPDATE);

				runInfo.writeLogLine("This is loop iteration " + i);
				broadcastUpdate(runInfo);

				SystemClock.sleep(1000);
			}

			if (probeRunCancelled(runInfo)) {
				return null;
			}

			Log.d(LOG_TAG, "finished sleeping, doing callback");
			runInfo.writeLogLine("and I'm done");
			runInfo.setFinishedWithSuccess();

			broadcastUpdate(runInfo);

			return null;

		}

		/* TODO: make plenty of checks in running services for cancellations
	     * especially in memory intensive tasks, or those that could potentially
	     * download lots of data (eg, download in blocks and check each time) */
		private boolean probeRunCancelled(InteractiveProbeRunInfo info){

			if(isCancelled()){
				Log.d(LOG_TAG,"Async task for probe run " + info.probeRunId
						+ " was cancelled, most likely due to a new probe run");
				return true;
			}

			if(info.isFinished()){
				Log.d(LOG_TAG, "Probe run " + info.probeRunId + " has been marked as finished, stopping processing");
				return true;
			}

			return false;
		}

	}

	private void broadcastUpdate(InteractiveProbeRunInfo probeRunInfo) {

		Intent updateIntent = new Intent(ACTION_UPDATE);
		updateIntent.putExtra(EXTRA_PROBERUN_ID, probeRunInfo.probeRunId);
		sendBroadcast(updateIntent);

	}

}