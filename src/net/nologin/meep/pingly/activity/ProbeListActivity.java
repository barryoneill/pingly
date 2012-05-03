package net.nologin.meep.pingly.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.adapter.ProbeListCursorAdapter;
import net.nologin.meep.pingly.alarm.AlarmScheduler;
import net.nologin.meep.pingly.model.ScheduleEntry;
import net.nologin.meep.pingly.model.probe.Probe;
import net.nologin.meep.pingly.util.PinglyUtils;

import java.util.List;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

public class ProbeListActivity extends BasePinglyActivity {

	private ProbeListCursorAdapter listAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.probe_list);

		Cursor allProbesCursor = probeDAO.findAllProbes();
		listAdapter = new ProbeListCursorAdapter(this, allProbesCursor);
		ListView lv = (ListView) findViewById(R.id.probeList);
		lv.setAdapter(listAdapter);
		registerForContextMenu(lv);

		View empty = findViewById(R.id.emptyListElem);
		lv.setEmptyView(empty);

		startManagingCursor(allProbesCursor);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int itemPos, long itemId) {
				Log.d(LOG_TAG, "Got id " + itemId);
				// onclick used to just start the runner, but I noticed that people then don't
				// realise that there's a long-press menu with other features
				// PinglyUtils.startActivityProbeRunner(ProbeListActivity.this,itemId);

				// onclick == long press, ie, open the context menu
				openContextMenu(view);
			}
		});

	}



	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (probeDAO != null) {
			probeDAO.close();
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
									ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);

		if (v.getId() == R.id.probeList) {

			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

			Probe target = probeDAO.findProbeById(info.id);
			menu.setHeaderTitle(target.name);

			MenuInflater inflater1 = getMenuInflater();
			inflater1.inflate(R.menu.probe_list_context, menu);

		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();

		final Probe probe = probeDAO.findProbeById(info.id);

		switch (item.getItemId()) {

			case R.id.probe_list_contextmenu_run:
				Log.d("PINGLY", "Running probe: " + probe);

				PinglyUtils.startActivityProbeRunner(this,probe.id);

				return true;

			case R.id.probe_list_contextmenu_edit:
				Log.d("PINGLY", "Editing probe: " + probe);

				PinglyUtils.startActivityProbeDetail(this,probe.id);

				return true;

			case R.id.probe_list_contextmenu_run_history:
				Log.d("PINGLY", "Going to run history for probe: " + probe);

				PinglyUtils.startActivityProbeRunHistory(this,probe.id);

				return true;

			case R.id.probe_list_contextmenu_schedule:

				Log.d("PINGLY", "Scheduling probe: " + probe);

				PinglyUtils.startActivityScheduleEntryDetail(this,probe.id);

				return true;

			case R.id.probe_list_contextmenu_delete:
				Log.d("PINGLY", "Deleting probe: " + probe);

				final List<ScheduleEntry> entries = scheduleDAO.findEntriesForProbe(probe.id);
				final String msg = entries.size() > 0
						? "Probe '" + probe.name + "' has scheduler entries, these will be stopped as well.  Continue?"
						: "Are you sure you want to delete probe '" + probe.name + "'?";

				AlertDialog dialog = new AlertDialog.Builder(this)
						.setTitle("Delete Probe")
						.setMessage(msg)
						.setCancelable(false)
						.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								// requestCancel alarms for those entries
								AlarmScheduler.cancelAlarms(ProbeListActivity.this, entries);

								// delete the run history (all items, not just finished)
								probeRunDAO.deleteHistoryForProbe(probe.id,false);

								// delete the entries
								scheduleDAO.deleteForProbe(probe.id);

								// then delete the probe
								probeDAO.deleteProbe(probe);

								/* since we stay where we are (no activity state change), the startManagingCursor()
								 * registration in onCreate() won't know to refresh the cursor/adapter.  We requery
								 * all probes and pass the new cursor to the adapter. */
								listAdapter.changeCursor(probeDAO.findAllProbes());
							}
						})
						.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						}).create();
				dialog.show();
				return true;

			default:
				Log.d("PINGLY", "Unhandled Item ID " + item.getItemId());
				super.onContextItemSelected(item);

		}

		// TextView text = (TextView)findViewById(R.id.footer);
		// text.setText(String.format("Selected %s for item %s", menuItemName,
		// listItemName));
		return true;
	}

}
