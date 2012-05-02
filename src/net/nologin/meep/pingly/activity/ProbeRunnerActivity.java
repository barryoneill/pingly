package net.nologin.meep.pingly.activity;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.*;
import android.provider.Settings;
import net.nologin.meep.pingly.model.ProbeRun;
import net.nologin.meep.pingly.model.ProbeRunStatus;
import net.nologin.meep.pingly.service.ProbeRunnerInteractiveService;
import net.nologin.meep.pingly.model.probe.Probe;

import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.util.PinglyUtils;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ScrollView;
import android.widget.TextView;

import static net.nologin.meep.pingly.service.ProbeRunnerInteractiveService.EXTRA_PROBE_RUN_ID;
import static net.nologin.meep.pingly.service.ProbeRunnerInteractiveService.ACTION_UPDATE;

public class ProbeRunnerActivity extends BasePinglyActivity {

	static final int DIALOG_SERVICE_WAIT_ID = 0;
	static final int DIALOG_NO_DATACONN_ID = 1;

	private TextView probeName;
	private View probeInfoContainer;
	private TextView probeLogOutput;
	private ScrollView probeLogScroller;

	// current probe chosen from the probe list, loaded from activity params bundle
	private Probe selectedProbe;

	// an identifier given to each 'run' of the probe, created on run, persisted from onRestoreInstanceState
	private ProbeRun currentRun;

	// listens for broadcasts from the update server which inform of updates
	private ProbeRunCallbackReceiver callbackReceiver = null;


	@Override
	protected void onCreate(Bundle state) {

		super.onCreate(state);
		setContentView(R.layout.probe_runner);

		// parameter must be present
		selectedProbe = getIntentExtraProbe();

		Log.d(LOG_TAG, "Running probe " + selectedProbe);

		// load refs
		probeInfoContainer = findViewById(R.id.probeInfoContainer);
		probeName = (TextView) findViewById(R.id.text_probe_name);
		probeLogOutput = (TextView) findViewById(R.id.probe_log_output);
		probeLogScroller = (ScrollView) findViewById(R.id.probe_log_scroller);

		// attach onclick events to buttons
		findViewById(R.id.but_probeRun_runAgain).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				clearAndStartProbe();
			}
		});
		findViewById(R.id.but_probeRun_edit).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				goToProbeDetails(selectedProbe.id);
			}
		});
		findViewById(R.id.but_probeRun_history).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				goToProbeRunHistory(selectedProbe.id);
			}
		});

		// create receiver, will be registered until onResume()
		callbackReceiver = new ProbeRunCallbackReceiver();

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// so we know what probe run log to display after sleep/rotation etc
		if(currentRun != null){
			savedInstanceState.putLong(STATE_PROBERUN_ID, currentRun.id);
		}
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		// we were previously running a probe, get the id of that run
		if (savedInstanceState.containsKey(STATE_PROBERUN_ID)) {
			long runId = savedInstanceState.getLong(STATE_PROBERUN_ID);
			Log.d(LOG_TAG, "onRestoreInstanceState, we were processing probe run: " + runId);
			currentRun = probeRunDAO.findProbeRunById(runId);
		}
	}

	@Override
	public void onResume() {

		super.onResume();

		// register the receiver for updates (unregistered in onPause())
		IntentFilter filter = new IntentFilter(ACTION_UPDATE);
		Log.d(LOG_TAG, "Registering Receiver " + this.getClass().getName());
		registerReceiver(callbackReceiver, filter);

		refreshCurrentRunInfo();

		if (currentRun != null && currentRun.isFinished()) {
			removeDialog(DIALOG_SERVICE_WAIT_ID);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		// unregister the receiver (re-registered in onResume())
		unregisterReceiver(callbackReceiver);
	}

	/**
	 * This receiver (registered in onResume, deregistered in onPause) listens
	 * for specific broadcasts from the ProbeRunnerInteractiveService which
	 * tell it that the probe run has more data to display.  This data is currently
	 * held in the Application singleton (perhaps we'll persist the logs in the future)
	 * under a unique ID for this run.
	 *
	 * @see ProbeRunnerInteractiveService
	 */
	private class ProbeRunCallbackReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			// the probe runner service broadcasts using action ACTION_UPDATE
			if (ACTION_UPDATE.equals(intent.getAction())) {

				Log.d(LOG_TAG, " Broadcast Receiver matched " + ACTION_UPDATE);

				/* We also ensure that this broadcast is for the current run.  It's possible
				 * that an older background task in the service hasn't yet detected that it
				 * was 'cancelled', and may emit a broadcasts for the older run. */
 				long callbackRunId = intent.getLongExtra(EXTRA_PROBE_RUN_ID, 0);
				if (currentRun.id != callbackRunId) {
					Log.w(LOG_TAG, "Matched broadcasted action " + intent.getAction()
							+ ", but probe runner id " + callbackRunId + " did not match " + currentRun.id
							+ ", perhaps this is a broadcast from an older runner instant - ignoring");
					return;
				}

				// update the log and status windows with the current info
				refreshCurrentRunInfo();

				// if the run is finished, get rid of the progress dialog
				if (currentRun.isFinished()) {
					removeDialog(DIALOG_SERVICE_WAIT_ID);
				}

			}


		}

	}

	private void clearAndStartProbe() {

		probeLogOutput.setText("");

		// nothing to run if we don't have a data connection
		if (!PinglyUtils.activeNetConnectionPresent(this)) {
			writeToProbeLogWindow("Probe run aborted, no data connection present.", false); // TODO: i18n
			decorateProbeStatus(ProbeRunStatus.Failed);
			showDialog(DIALOG_NO_DATACONN_ID);
			return;
		}

		// show 'please wait' dialog
		showDialog(DIALOG_SERVICE_WAIT_ID);

		// start the service, providing a unique id for the run and the id of the probe itself
		Intent serviceCallIntent = new Intent(this, ProbeRunnerInteractiveService.class);
		currentRun = probeRunDAO.prepareNewProbeRun(selectedProbe, null);

		Log.e(LOG_TAG, " ********************** " + currentRun);

		serviceCallIntent.putExtra(EXTRA_PROBE_RUN_ID, currentRun.id);
		startService(serviceCallIntent);

	}

	// update text/color of the summary box
	private void decorateProbeStatus(ProbeRunStatus status) {
		probeInfoContainer.setBackgroundResource(status.colorResId);
		probeName.setText(status.formatName(this, selectedProbe.name));
	}

	private void writeToProbeLogWindow(String txt, boolean append) {

		if (append) {
			probeLogOutput.append(txt + "\n");
		} else {
			probeLogOutput.setText(txt + "\n");
		}
		// probeLogScroller.smoothScrollTo(0, probeLogOutput.getBottom());
		probeLogScroller.fullScroll(ScrollView.FOCUS_DOWN);
	}

	// update the log window with the current run's status
	private void refreshCurrentRunInfo() {

		if(currentRun == null || currentRun.isNew()){
			writeToProbeLogWindow("", false);
			decorateProbeStatus(ProbeRunStatus.Inactive);
		}
		else {
			// refresh the run info from the db
			currentRun = probeRunDAO.findProbeRunById(currentRun.id);
			writeToProbeLogWindow(currentRun.logText, false);
			decorateProbeStatus(currentRun.status);
		}

	}

	// Note: Rather than creating dialogs directly in the methods above, we use showDialog() with
	// this overridden method - Android will handle the dialog lifecycle for us (eg, it'll
	// persist past screen rotations, etc)
	// TODO: i18n!
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		String title;
		String msg;
		switch (id) {
			case DIALOG_SERVICE_WAIT_ID:
				msg = "This may take a few moments depending on your data connection and the probe's destination, please wait..";
				dialog = ProgressDialog.show(this, "Probe Running", msg, true);
				dialog.setCancelable(true);
				dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialogInterface) {
//						currentRun.status = ProbeRunStatus.Failed; // let the service know to stop processing
//						currentRun.appendLogLine("Cancel requested by user.");
//						fsdf

//						refreshCurrentRunInfo();
//						removeDialog(DIALOG_SERVICE_WAIT_ID);

					}
				});
				break;
			case DIALOG_NO_DATACONN_ID:
				title = "Network Unavailable";
				msg = "Please enable mobile data/wifi and try again.";

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(title)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setMessage(msg)
						.setCancelable(true)
						.setPositiveButton("Wireless Settings", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
							}
						})
						.setNegativeButton("Cancel", null);
				dialog = builder.create();
				break;
			default:
				Log.e(LOG_TAG, "Unknown dialog ID " + id);
				dialog = null;
		}
		return dialog;
	}



}

