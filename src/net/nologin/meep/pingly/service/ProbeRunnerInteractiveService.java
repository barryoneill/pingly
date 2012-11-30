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
package net.nologin.meep.pingly.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import net.nologin.meep.pingly.PinglyApplication;
import net.nologin.meep.pingly.db.PinglyDataHelper;
import net.nologin.meep.pingly.db.ProbeDAO;
import net.nologin.meep.pingly.db.ProbeRunDAO;
import net.nologin.meep.pingly.model.ProbeRun;
import net.nologin.meep.pingly.service.runner.ProbeRunner;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

/**
 * This service is used by the ProbeRunnerActivity to run probes asynchronously in the background.
 * The service accepts intents from the activity, and starts the appropriate proberun.  When the
 * probe runner provides updates, this service fires off broadcast intents which the activity
 * uses to display the current state of the run to the user.
 */
public class ProbeRunnerInteractiveService extends Service {

	public static final String ACTION_UPDATE = "net.nologin.meep.pingly.service.ProbeRunnerInteractiveService.ACTION_UPDATE";
	public static final String EXTRA_PROBE_RUN_ID = "net.nologin.meep.pingly.service.ProbeRunnerInteractiveService.EXTRA_PROBE_RUN_ID";

	ProbeRunnerThread runThread;
	ProbeDAO probeDAO;
	ProbeRunDAO probeRunDAO;

	public void onCreate() {
		super.onCreate();
		Log.d(LOG_TAG, "ProbeRunnerInteractiveService created");

        PinglyApplication app = (PinglyApplication)getApplication();
        PinglyDataHelper dh = app.getPinglyDataHelper();

        probeDAO = new ProbeDAO(dh);
        probeRunDAO = new ProbeRunDAO(dh);

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

					updateActivity(probeRun,false);

				}
			});

			// blocking call, use update listener above to notify cancel request
			runner.run(ProbeRunnerInteractiveService.this);

			// runner finished, but don't send an update if the thread was interrupted
			if(!isInterrupted()){
				updateActivity(probeRun,true);
			}


		}

	}

	private void updateActivity(ProbeRun probeRun, boolean finished) {

		probeRunDAO.saveProbeRun(probeRun,finished);

		Intent updateIntent = new Intent(ACTION_UPDATE);
		updateIntent.putExtra(EXTRA_PROBE_RUN_ID, probeRun.id);
		sendBroadcast(updateIntent);

	}


}