package net.nologin.meep.pingly.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import net.nologin.meep.pingly.PinglyApplication;
import net.nologin.meep.pingly.db.ProbeDAO;
import net.nologin.meep.pingly.model.InteractiveProbeRunInfo;
import net.nologin.meep.pingly.model.probe.Probe;
import net.nologin.meep.pingly.service.runner.ProbeRunner;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

public class ProbeRunnerInteractiveService extends Service {

	public static final String ACTION_UPDATE = "net.nologin.meep.pingly.service.ProbeRunnerInteractiveService.ACTION_UPDATE";
	public static final String EXTRA_PROBE_RUN_ID = "net.nologin.meep.pingly.service.ProbeRunnerInteractiveService.EXTRA_PROBE_RUN_ID";

	ProbeRunnerAsyncTask runningTask;
	ProbeDAO probeDAO;

	public void onCreate() {
		super.onCreate();
		Log.d(LOG_TAG, "ProbeRunnerInteractiveService created");

		probeDAO = new ProbeDAO(this);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.d(LOG_TAG, "ProbeRunnerInteractiveService started");

		if (probeDAO != null) {
			probeDAO.close();
		}
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

		long probeRunId = intent.getLongExtra(EXTRA_PROBE_RUN_ID, 0);
		InteractiveProbeRunInfo runInfo = ((PinglyApplication) getApplication()).getProbeRunInfo(probeRunId);
		if(runInfo == null){
			Log.e(LOG_TAG,"No probe run info for " + probeRunId + " was found, ignoring service call");
			return START_NOT_STICKY;

		}
		if(runInfo.probeId < 1){
			Log.e(LOG_TAG,"Invalid Probe ID supplied, ignoring service call");
			return START_NOT_STICKY;
		}

		Probe targetProbe = probeDAO.findProbeById(runInfo.probeId);
		if(targetProbe == null){
			Log.e(LOG_TAG,"No probe found for ID " + runInfo.probeId + ", ignoring service call");
			return START_NOT_STICKY;
		}

		// signal any previously running task to cancel
		if(runningTask != null && !runningTask.isCancelled()){
			runningTask.cancel(true);
		}

		runningTask = new ProbeRunnerAsyncTask(runInfo, targetProbe);
		runningTask.execute();

		return START_NOT_STICKY;

	}



	private class ProbeRunnerAsyncTask extends AsyncTask<Void, Void, Void> {

		private InteractiveProbeRunInfo runInfo;
		private Probe probe;

		public ProbeRunnerAsyncTask(InteractiveProbeRunInfo runInfo, Probe probe){
			this.runInfo = runInfo;
			this.probe = probe;
		}

		@Override
		protected Void doInBackground(Void... voids) {

			runInfo.status = InteractiveProbeRunInfo.RunStatus.Running;

			runInfo.writeLogLine("Starting run for:\n" + probe.name);
			runInfo.writeLogLine("");
			broadcastUpdate(runInfo);

			// ------------------------------------------------------------------------------------

			final ProbeRunner runner = ProbeRunner.getInstance(probe);
			runner.setUpdateListener(new ProbeRunner.ProbeUpdateListener() {
				@Override
				public void onUpdate(String newOutput) {
					runInfo.writeLog(newOutput);
					broadcastUpdate(runInfo);
					if (probeRunCancelled(runInfo)) {
						runner.cancel();
					}
				}
			});
			boolean runSuccessful = runner.run();

			runInfo.writeLogLine("");
			if(runSuccessful){
				runInfo.writeLogLine("Probe successful");
				runInfo.setFinishedWithSuccess();
			}
			else{
				runInfo.writeLogLine("Probe failed");
				runInfo.setFinishedWithFailure();
			}
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
				Log.d(LOG_TAG, "ProbeRunnerInteractiveService - Probe run "
						+ info.probeRunId + " has been marked as finished, stopping processing");
				return true;
			}

			return false;
		}

	}

	private void broadcastUpdate(InteractiveProbeRunInfo probeRunInfo) {

		Intent updateIntent = new Intent(ACTION_UPDATE);
		updateIntent.putExtra(EXTRA_PROBE_RUN_ID, probeRunInfo.probeRunId);
		sendBroadcast(updateIntent);

	}


}