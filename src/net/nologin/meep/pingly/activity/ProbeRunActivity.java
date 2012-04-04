package net.nologin.meep.pingly.activity;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
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

public class ProbeRunActivity extends BasePinglyActivity {

	static final int DIALOG_SERVICE_WAIT_ID = 0;
	static final int DIALOG_NO_DATA_ID = 1;

	private TextView probeName;
    private View probeInfoContainer;
	private Button runAgainBut;
	private Button editProbeBut;
	private TextView probeLogOutput;
	private ScrollView probeLogScroller;

	private Probe currentProbe;

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

		clearAndStartProbe();

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

