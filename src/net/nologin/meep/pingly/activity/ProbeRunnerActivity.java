package net.nologin.meep.pingly.activity;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.*;
import android.provider.Settings;
import net.nologin.meep.pingly.core.ProbeRunnerInteractiveService;
import net.nologin.meep.pingly.model.Probe;

import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.util.PinglyUtils;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.UUID;

import static net.nologin.meep.pingly.core.ProbeRunnerInteractiveService.EXTRA_PROBERUN_ID;
import static net.nologin.meep.pingly.core.ProbeRunnerInteractiveService.ACTION_UPDATE;
import static net.nologin.meep.pingly.core.ProbeRunnerInteractiveService.ACTION_FINISHED;

public class ProbeRunnerActivity extends BasePinglyActivity {

	static final int DIALOG_SERVICE_WAIT_ID = 0;
	static final int DIALOG_NO_DATACONN_ID = 1;

	static final String BUNDLE_CURRENTRUNNER_ID = "bundle_currentRunnerID";

	private TextView probeName;
	private View probeInfoContainer;
	private Button runAgainBut;
	private Button editProbeBut;
	private TextView probeLogOutput;
	private ScrollView probeLogScroller;

	private Probe currentProbe;

	private ProbeRunnerCallBackReceiver callbackReceiver = null;
	private Intent serviceCallIntent = null;

	@Override
	protected void onCreate(Bundle state) {

		super.onCreate(state);
		setContentView(R.layout.probe_runner);

		// parameter must be present
		currentProbe = loadProbeParamIfPresent();

		Log.d(LOG_TAG, "Running probe " + currentProbe);

		// load refs
		probeInfoContainer = findViewById(R.id.probeInfoContainer);
		probeName = (TextView) findViewById(R.id.text_probe_name);
		runAgainBut = (Button) findViewById(R.id.but_probeRun_runAgain);
		editProbeBut = (Button) findViewById(R.id.but_probeRun_edit);
		probeLogOutput = (TextView) findViewById(R.id.probe_log_output);
		probeLogScroller = (ScrollView) findViewById(R.id.probe_log_scroller);

		runAgainBut.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				clearAndStartProbe();
			}
		});
		editProbeBut.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				goToProbeDetails(currentProbe.id);
			}
		});

		decorateProbeStatus(ProbeRunnerStatus.Inactive);
		callbackReceiver = new ProbeRunnerCallBackReceiver();

	}

	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putLong(BUNDLE_CURRENTRUNNER_ID, callbackReceiver.getRunnerId());
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState.containsKey(BUNDLE_CURRENTRUNNER_ID)) {
			long oldId = savedInstanceState.getLong(BUNDLE_CURRENTRUNNER_ID);
			Log.d(LOG_TAG,"onRestoreInstanceState, setting callbackreceiver back to runner ID " + oldId);
			callbackReceiver.setRunnerId(oldId);
		}
	}

	@Override
	public void onResume() {

		super.onResume();

		// register the receiver for updates
		IntentFilter filter = new IntentFilter(ACTION_UPDATE);
		filter.addAction(ACTION_FINISHED);

		Log.e(LOG_TAG,
				" --------------  Registering Receiver");
		registerReceiver(callbackReceiver, filter);
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(callbackReceiver);
	}

	// what a name
	private class ProbeRunnerCallBackReceiver extends BroadcastReceiver {

		private long runnerId;

		public void setRunnerId(long runnerId) {
			this.runnerId = runnerId;
		}

		public long setNewRunnerId() {
			runnerId = System.currentTimeMillis(); // TODO: this sufficient?
			return runnerId;
		}

		public long getRunnerId() {
			return runnerId;
		}

		@Override
		public void onReceive(Context context, Intent intent) {

			if (ACTION_UPDATE.equals(intent.getAction())) {

				if (!checkRunnerId(intent)) {
					return;
				}

				Log.d(LOG_TAG, " Broadcast Receiver matched " + ACTION_UPDATE);

				// Updates the ProgressDialog
				String value = intent.getStringExtra(ProbeRunnerInteractiveService.EXTRA_DATA_LOGTEST);
				appendLogLine("Data: " + value);

			}

			if (ACTION_FINISHED.equals(intent.getAction())) {

				if (!checkRunnerId(intent)) {
					return;
				}

				Log.d(LOG_TAG, " Broadcast Receiver matched " + ACTION_UPDATE);
				appendLogLine("Service finished processing");

				// and we're done with the dialog
				removeDialog(DIALOG_SERVICE_WAIT_ID);

				decorateProbeStatus(ProbeRunnerStatus.Success);

			}
		}

		boolean checkRunnerId(Intent intent) {

			long probeRunId = intent.getLongExtra(EXTRA_PROBERUN_ID, 0);

			if (this.runnerId == probeRunId) {
				return true;
			} else {
				Log.w(LOG_TAG, "Matched broadcasted action " + intent.getAction()
						+ ", but probe runner id " + probeRunId + " did not match " + this.runnerId
						+ ", perhaps this is a broadcast from an older runner instant - ignoring");
				return false;
			}

		}
	}


	private void clearAndStartProbe() {

		probeLogOutput.setText("");

		if (!PinglyUtils.activeNetConnectionPresent(this)) {
			appendLogLine("Probe run aborted, no data connection present."); // TODO: i18n
			decorateProbeStatus(ProbeRunnerStatus.Failed);
			showDialog(DIALOG_NO_DATACONN_ID);
			return;
		}

		// and we're off
		decorateProbeStatus(ProbeRunnerStatus.Running);
		showDialog(DIALOG_SERVICE_WAIT_ID);

		// -----------------------------------------------------------------------------
		serviceCallIntent = new Intent(this, ProbeRunnerInteractiveService.class);
		serviceCallIntent.putExtra(ProbeRunnerInteractiveService.EXTRA_PROBERUN_ID, callbackReceiver.setNewRunnerId());
		startService(serviceCallIntent);


		// -----------------------------------------------------------------------------

	}

	private void decorateProbeStatus(ProbeRunnerStatus status) {
		probeInfoContainer.setBackgroundResource(status.colorResId);
		probeName.setText(status.formatName(this, currentProbe.name));
	}

	private void appendLogLine(String txt) {

		probeLogOutput.append(txt + "\n");
		// probeLogScroller.smoothScrollTo(0, probeLogOutput.getBottom());
		probeLogScroller.fullScroll(ScrollView.FOCUS_DOWN);
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
						// TODO: inform service to end processing?
						appendLogLine("Probe was cancelled by the user.");
						decorateProbeStatus(ProbeRunnerStatus.Failed);
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

	// bleh
	enum ProbeRunnerStatus {

		Inactive(R.color.probe_runner_status_inactive, R.string.probe_runner_status_inactive),
		Running(R.color.probe_runner_status_running, R.string.probe_runner_status_running),
		Success(R.color.probe_runner_status_success, R.string.probe_runner_status_success),
		Failed(R.color.probe_runner_status_failed, R.string.probe_runner_status_failed);

		public final int colorResId;
		public final int nameFormatterResId;

		ProbeRunnerStatus(int colorResId, int nameFormatterResId) {
			this.colorResId = colorResId;
			this.nameFormatterResId = nameFormatterResId;
		}

		public String formatName(Context ctx, String probeName) {
			String fmt = ctx.getString(nameFormatterResId);
			return String.format(fmt, probeName);
		}
	}

}

