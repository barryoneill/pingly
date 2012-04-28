package net.nologin.meep.pingly.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import net.nologin.meep.pingly.PinglyConstants;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.adapter.ProbeRunHistoryCursorAdapter;
import net.nologin.meep.pingly.model.ProbeRun;
import net.nologin.meep.pingly.model.ProbeRunStatus;
import net.nologin.meep.pingly.model.probe.Probe;
import net.nologin.meep.pingly.util.PinglyUtils;
import net.nologin.meep.pingly.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;


public class ProbeRunHistoryActivity extends BasePinglyActivity {

	static final int DIALOG_PROBE_RUN_LOG = 1;

	ProbeRunHistoryCursorAdapter listAdapter;
	Probe currentProbe;
	ProbeRun probeRunForLogDialog; // for showing chosen log info

	@Override
	protected void onCreate(Bundle state) {

		super.onCreate(state);
		setContentView(R.layout.probe_run_history_list);

		currentProbe = getIntentExtraProbe();

		// if a probe run param is supplied, that overrides the probe param
		probeRunForLogDialog = getIntentExtraProbeRun();
		if(probeRunForLogDialog != null){
			currentProbe = probeRunForLogDialog.probe;
		}

		Long probeId = currentProbe == null ? null : currentProbe.id;

		Cursor runHistoryCursor = probeRunDAO.queryForProbeRunHistoryCursorAdapter(probeId);
		listAdapter = new ProbeRunHistoryCursorAdapter(this, runHistoryCursor);
		ListView lv = (ListView) findViewById(R.id.probeRunList);
		lv.setAdapter(listAdapter);
		registerForContextMenu(lv);

		View empty = findViewById(R.id.emptyListElem);
		lv.setEmptyView(empty);

		startManagingCursor(runHistoryCursor);

		((TextView) findViewById(R.id.probe_name)).setText(currentProbe.name);
		((TextView) findViewById(R.id.probe_summary)).setText(currentProbe.desc);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int itemPos, long itemId) {
				Log.d(LOG_TAG, "Got id " + itemId);

				probeRunForLogDialog = probeRunDAO.findProbeRunById(itemId);
				showDialog(DIALOG_PROBE_RUN_LOG);

			}
		});

		findViewById(R.id.probeHistory_runNowBut).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				goToProbeRunner(currentProbe.id);
			}
		});

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// so we know what probe run log to display after sleep/rotation etc
		if(probeRunForLogDialog != null){
			savedInstanceState.putLong(STATE_PROBERUN_ID, probeRunForLogDialog.id);
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
			probeRunForLogDialog = probeRunDAO.findProbeRunById(runId);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
									ContextMenu.ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);
//
//		if (v.getId() == R.id.probeRunList) {
//
//			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
//
//			ProbeRun target = probeRunDAO.findProbeRunById(info.id);
//			menu.setHeaderTitle("Context Menu");
//
//			MenuInflater inflater1 = getMenuInflater();
//			inflater1.inflate(R.menu.probe_list_context, menu);
//
//		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();

//		final Probe probe = probeDAO.findProbeById(info.id);
//
//		switch (item.getItemId()) {
//
//			case R.id.probe_list_contextmenu_run:
//				Log.d("PINGLY", "Running probe: " + probe);
//
//				goToProbeRunner(probe.id);
//
//				return true;
//
//			default:
//				Log.d("PINGLY", "Unhandled Item ID " + item.getItemId());
//				super.onContextItemSelected(item);
//
//		}
//
		return true;
	}

	// TODO: i18n!
	protected Dialog onCreateDialog(int id) {

		Dialog dialog;
		AlertDialog.Builder builder;

		switch (id) {
			case DIALOG_PROBE_RUN_LOG:
				String title = "Probe Log";

				View logView = View.inflate(PinglyUtils.getPinglyDialogContext(ProbeRunHistoryActivity.this),
						R.layout.probe_run_history_log, null);

				builder = PinglyUtils.getAlertDialogBuilder(this);
				builder.setTitle(title)
						.setView(logView)
						.setCancelable(true)
						.setNegativeButton("Close", null);
				dialog = builder.create();

				break;

			default:
				Log.e(LOG_TAG, "Unknown dialog ID " + id);
				dialog = null;
		}
		return dialog;
	}


	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {

		switch (id) {
			case DIALOG_PROBE_RUN_LOG:

				AlertDialog ad = (AlertDialog)dialog;

				Probe probe = probeRunForLogDialog.probe;
				ProbeRunStatus status = probeRunForLogDialog.status;

				ad.setTitle("Run Log For:\n" + probe.name);

				TextView txtStatusSummary = (TextView)ad.findViewById(R.id.probeRun_log_status_summary);
				TextView txtTimeStarted = (TextView)ad.findViewById(R.id.probeRun_log_time_started);
				TextView txtLog = (TextView)ad.findViewById(R.id.probeRun_log_logText);

				String summary = status.getKey() + ": " + probeRunForLogDialog.runSummary;
				txtStatusSummary.setText(summary);
				txtStatusSummary.setBackgroundResource(status.colorResId);

				DateFormat df = new SimpleDateFormat(PinglyConstants.FMT_DATE_AND_TIME_SUMMARY_SHORT);
				txtTimeStarted.setText("Started: " + df.format(probeRunForLogDialog.startTime));

				if(StringUtils.isBlank(probeRunForLogDialog.logText)){
					txtLog.setText(" -- No Log Data -- ");
				}
				else {
					txtLog.setText(probeRunForLogDialog.logText);
				}


			break;

		default:
			Log.e(LOG_TAG, "Unknown dialog ID " + id);

		}
	}
}

