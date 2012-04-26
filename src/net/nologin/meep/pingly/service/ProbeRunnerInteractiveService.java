package net.nologin.meep.pingly.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import net.nologin.meep.pingly.db.ProbeDAO;
import net.nologin.meep.pingly.db.ProbeRunDAO;
import net.nologin.meep.pingly.model.ProbeRun;
import net.nologin.meep.pingly.service.runner.ProbeRunner;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

public class ProbeRunnerInteractiveService extends Service {

	public static final String ACTION_UPDATE = "net.nologin.meep.pingly.service.ProbeRunnerInteractiveService.ACTION_UPDATE";
	public static final String EXTRA_PROBE_RUN_ID = "net.nologin.meep.pingly.service.ProbeRunnerInteractiveService.EXTRA_PROBE_RUN_ID";

	ProbeRunnerThread runThread;
	ProbeDAO probeDAO;
	ProbeRunDAO probeRunDAO;

	public void onCreate() {
		super.onCreate();
		Log.d(LOG_TAG, "ProbeRunnerInteractiveService created");

		probeDAO = new ProbeDAO(this);
		probeRunDAO = new ProbeRunDAO(this);
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

		if (probeDAO != null) {
			probeDAO.close();
		}
		if (probeRunDAO != null) {
			probeRunDAO.close();
		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		long probeRunId = intent.getLongExtra(EXTRA_PROBE_RUN_ID, 0);
		ProbeRun probeRun = probeRunDAO.findProbeRunById(probeRunId);
		if(probeRun == null){
			Log.e(LOG_TAG,"No probe run info for " + probeRunId + " was found, ignoring service call");
			return START_NOT_STICKY;


		}

		// signal any previously running task to requestCancel
		if(runThread != null){
			Log.e(LOG_TAG,"Previous thread still running, calling interrupt");
			runThread.interrupt();
		}

		runThread = new ProbeRunnerThread(probeRun);
		runThread.start();

		return START_NOT_STICKY;

	}



	private class ProbeRunnerThread extends Thread {

		private ProbeRun probeRun;

		public ProbeRunnerThread(ProbeRun probeRun){
			this.probeRun = probeRun;
		}

		@Override
		public void run() {


			final ProbeRunner runner = ProbeRunner.getInstance(probeRun);

			runner.setUpdateListener(new ProbeRunner.ProbeUpdateListener() {
				@Override
				public void onUpdate(String newOutput) {

					if(isInterrupted()){
						Log.d(LOG_TAG,"Thread for probe run " + probeRun.id
								+ " was interrupted, most likely due to a new probe run");

						runner.requestCancel();
					}

					probeRunDAO.saveProbeRun(probeRun);
					updateActivity(probeRun);

				}
			});

			// blocking call, use update listener above to notify cancel request
			runner.run();

			// runner finished, but don't send an update if the thread was interrupted
			if(!isInterrupted()){
				updateActivity(probeRun);
			}


		}

	}

	private void updateActivity(ProbeRun probeRun) {

		probeRunDAO.saveProbeRun(probeRun);

		Intent updateIntent = new Intent(ACTION_UPDATE);
		updateIntent.putExtra(EXTRA_PROBE_RUN_ID, probeRun.id);
		sendBroadcast(updateIntent);

	}


}