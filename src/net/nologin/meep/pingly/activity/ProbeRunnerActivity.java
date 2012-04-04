package net.nologin.meep.pingly.activity;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
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

import static net.nologin.meep.pingly.core.ProbeRunnerInteractiveService.FILTER_UPDATE_DATA;



public class ProbeRunnerActivity extends BasePinglyActivity {

	static final int DIALOG_SERVICE_WAIT_ID = 0;
	static final int DIALOG_NO_DATA_ID = 1;

	final int SERVICE_REQUEST_CODE = 33333;

	private ProgressDialog progressDialog;

	private TextView probeName;
    private View probeInfoContainer;
	private Button runAgainBut;
	private Button editProbeBut;
	private TextView probeLogOutput;
	private ScrollView probeLogScroller;

	private Probe currentProbe;

	private BroadcastReceiver callbackReceiver;

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

		// -----------------------------------------------------------------------


		// Creates the BroadcastReceiver
		callbackReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent){
				Log.e(LOG_TAG,
						" --------------  Broadcast Receiver onReceive!");

				if(FILTER_UPDATE_DATA.equals(intent.getAction())){
					Log.e(LOG_TAG,
							" --------------  Broadcast Receiver matched " + FILTER_UPDATE_DATA);
					appendLogLine("got update!");

					// Updates the ProgressDialog
					String value = intent.getStringExtra(ProbeRunnerInteractiveService.EXTRA_DATA_LOGTEST);
					appendLogLine("Data: " + value);

				}

			}
		};

		// register the receiver for updates
		IntentFilter filter = new IntentFilter(FILTER_UPDATE_DATA);
		Log.e(LOG_TAG,
				" --------------  Registering Receiver");
		registerReceiver(callbackReceiver, filter);


		decorateProbeStatus(ProbeRunnerStatus.Inactive);
		clearAndStartProbe();



	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		// Compares the requestCode with the requestCode from above
		if (requestCode == SERVICE_REQUEST_CODE) {

			Log.e(LOG_TAG,
					" --------------  onActivityResult!");

			progressDialog.setOnCancelListener(null); // listener in onCreateDialog assumes failure on cancel
			progressDialog.cancel();
			decorateProbeStatus(ProbeRunnerStatus.Success);

		}
	}

	@Override
	protected void onDestroy(){
		unregisterReceiver(callbackReceiver);
		super.onDestroy();
	}

	private void clearAndStartProbe() {

		probeLogOutput.setText("");

		if(!PinglyUtils.activeNetConnectionPresent(this)){
			appendLogLine("Probe run aborted, no data connection present."); // TODO: i18n
			decorateProbeStatus(ProbeRunnerStatus.Failed);
			showDialog(DIALOG_NO_DATA_ID);
			return;
        }

		// and we're off
		decorateProbeStatus(ProbeRunnerStatus.Running);
		showDialog(DIALOG_SERVICE_WAIT_ID);

		// -----------------------------------------------------------------------------
		final Intent runnerServiceIntent = new Intent(this, ProbeRunnerInteractiveService.class);
		PendingIntent callbackIntent = createPendingResult(SERVICE_REQUEST_CODE,null,PendingIntent.FLAG_CANCEL_CURRENT);
		runnerServiceIntent.putExtra(ProbeRunnerInteractiveService.EXTRA_CALLBACK_INTENT, callbackIntent);
		startService(runnerServiceIntent);



		// -----------------------------------------------------------------------------

	}

	private void decorateProbeStatus(ProbeRunnerStatus status){
		probeInfoContainer.setBackgroundResource(status.colorResId);
		probeName.setText(status.formatName(this,currentProbe.name));
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
		switch(id) {
			case DIALOG_SERVICE_WAIT_ID:
				msg = "This may take a few moments depending on your data connection and the probe's destination, please wait..";
				dialog =  ProgressDialog.show(this, "Probe Running", msg, true);
				dialog.setCancelable(true);
				dialog.setOnCancelListener(new DialogInterface.OnCancelListener(){
					@Override
					public void onCancel(DialogInterface dialogInterface) {
						// TODO: inform service to end processing?
						appendLogLine("Probe was cancelled by the user.");
						decorateProbeStatus(ProbeRunnerStatus.Failed);
					}
				});
				progressDialog = (ProgressDialog)dialog; // hold reference
				break;
			case DIALOG_NO_DATA_ID:
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
				Log.e(LOG_TAG,"Unknown dialog ID " + id);
				dialog = null;
		}
		return dialog;
	}

	// bleh
	enum ProbeRunnerStatus {

		Inactive(R.color.probe_runner_status_inactive,R.string.probe_runner_status_inactive),
		Running(R.color.probe_runner_status_running,R.string.probe_runner_status_running),
		Success(R.color.probe_runner_status_success,R.string.probe_runner_status_success),
		Failed(R.color.probe_runner_status_failed,R.string.probe_runner_status_failed);

		public final int colorResId;
		public final int nameFormatterResId;

		ProbeRunnerStatus(int colorResId, int nameFormatterResId){
			this.colorResId = colorResId;
			this.nameFormatterResId = nameFormatterResId;
		}

		public String formatName(Context ctx, String probeName){
			String fmt = ctx.getString(nameFormatterResId);
			return String.format(fmt,probeName);
		}
	}

}

