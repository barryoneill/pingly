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
package net.nologin.meep.pingly.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.adapter.ProbeListCursorAdapter;
import net.nologin.meep.pingly.adapter.ScheduleListCursorAdapter;
import net.nologin.meep.pingly.alarm.AlarmScheduler;
import net.nologin.meep.pingly.db.ProbeDAO;
import net.nologin.meep.pingly.model.ScheduleEntry;
import net.nologin.meep.pingly.model.probe.Probe;
import net.nologin.meep.pingly.util.PinglyUtils;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

/**
 * Show the list of configured schedules and allow selection of
 * an existing probe for scheduling
 */
public class ScheduleListActivity extends BasePinglyActivity {

	static final int DIALOG_NO_PROBES = 1;
	static final int DIALOG_CHOOSE_PROBE = 2;

    private ScheduleListCursorAdapter listAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_list);

        Cursor schedCursor = scheduleDAO.queryForScheduleListCursorAdapter();
        listAdapter = new ScheduleListCursorAdapter(this,schedCursor);
        ListView lv = (ListView) findViewById(R.id.scheduleList);
        lv.setAdapter(listAdapter);
        registerForContextMenu(lv);

        View empty = findViewById(R.id.emptyListElem);
        lv.setEmptyView(empty);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int itemPos, long itemId) {
				Log.d(LOG_TAG, "Got id " + itemId);

				// onclick == long press, ie, open the context menu
				openContextMenu(view);
			}
		});

    }

	// onClick handler for new schedule
	public void createNewSchedule(View v){

		if(probeDAO.getNumProbes() < 1){
			showDialog(DIALOG_NO_PROBES);
		}
		else {
			showDialog(DIALOG_CHOOSE_PROBE);
		}

	}


	@Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.scheduleList) {

            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

            ScheduleEntry target = scheduleDAO.findById(info.id);
            menu.setHeaderTitle(target.probe.name);

            MenuInflater inflater1 = getMenuInflater();
            inflater1.inflate(R.menu.schedule_list_context, menu);

            // update the text depending on status
            MenuItem del = menu.findItem(R.id.schedule_list_contextmenu_activetoggle);
            del.setTitle(target.active
					? R.string.schedule_list_context_deactivate
					: R.string.schedule_list_context_activate);

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();

        final ScheduleEntry entry = scheduleDAO.findById(info.id);

        switch (item.getItemId()) {

			case R.id.schedule_list_contextmenu_edit_schedule:
				Log.d("PINGLY", "Edit Schedule : " + entry);

				PinglyUtils.startActivityScheduleEntryDetail(this,entry);

				return true;

			case R.id.schedule_list_contextmenu_edit_probe:
				Log.d("PINGLY", "Edit Probe : " + entry);

				PinglyUtils.startActivityProbeDetail(this,entry.probe);

				return true;

            case R.id.schedule_list_contextmenu_activetoggle:
                Log.d("PINGLY", "Toggling : " + entry);

				// mark as enabled/disabled
				entry.active = !entry.active;
				scheduleDAO.saveScheduleEntry(entry);

				// cancel/set alarm depending on new state
				int msgResId;
				if(entry.active){
					msgResId = R.string.toast_schedule_activated;
					AlarmScheduler.setAlarm(this,entry);
				}
				else{
					msgResId = R.string.toast_schedule_deactivated;
					AlarmScheduler.cancelAlarm(this,entry);
				}

				// refresh the list to show the change in status
				refreshScheduleList();

                // give the user feedback
				PinglyUtils.showToast(this,msgResId, entry.probe.name);

                return true;

			case R.id.schedule_list_contextmenu_history:
				Log.d("PINGLY", "Viewing history for : " + entry);

				PinglyUtils.startActivityProbeRunHistory(this,entry.probe);

				return true;

            case R.id.schedule_list_contextmenu_delete:

                Log.d("PINGLY", "Deleting schedule item: " + entry);

				String msg = getString(R.string.dialog_schedule_delete_confirmFmt, entry.probe.name);

                AlertDialog dialog = new AlertDialog.Builder(this)
						.setTitle(R.string.dialog_schedule_delete_title)
                        .setMessage(msg)
                        .setCancelable(false)
                        .setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								Log.d(LOG_TAG, "Cancelling alarms for entry " + entry);
								AlarmScheduler.cancelAlarm(ScheduleListActivity.this, entry);

								scheduleDAO.delete(entry);

								PinglyUtils.showToast(ScheduleListActivity.this,
										R.string.toast_schedule_deleted,
										entry.probe.name);

								refreshScheduleList();
							}
						})
                        .setNegativeButton(R.string.button_no, new DialogInterface.OnClickListener() {
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

        return true;
    }

	private void refreshScheduleList(){

		/* since we stay where we are (no activity state change), the startManagingCursor() registration in onCreate()
                                         * won't know to refresh the cursor/adapter.  We requery all probes and pass the new cursor to the adapter. */
		listAdapter.changeCursor(scheduleDAO.queryForScheduleListCursorAdapter());
	}


	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		AlertDialog.Builder builder;

		switch (id) {
			case DIALOG_NO_PROBES:

				builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.dialog_schedule_noprobes_title)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setMessage(R.string.dialog_schedule_noprobes_message)
						.setCancelable(true)
						.setPositiveButton(R.string.button_create_new_probe, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								PinglyUtils.startActivityProbeDetail(ScheduleListActivity.this,null);
							}
						})
						.setNegativeButton(R.string.button_cancel, null);
				dialog = builder.create();
				break;
			case DIALOG_CHOOSE_PROBE:

				final Cursor allProbesCursor = probeDAO.findAllProbes();
				final ProbeListCursorAdapter probeListAdapter = new ProbeListCursorAdapter(this,allProbesCursor);

				builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.dialog_schedule_chooseprobe_title)
						.setIcon(android.R.drawable.ic_dialog_info)
						.setCancelable(true)
						.setNeutralButton(R.string.button_create_new_probe, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								PinglyUtils.startActivityProbeDetail(ScheduleListActivity.this,null);
							}
						})
						.setNegativeButton(R.string.button_cancel, null)
						.setAdapter(probeListAdapter, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								Probe selected = ProbeDAO.cursorToProbe(allProbesCursor, false);
								PinglyUtils.startActivityScheduleEntryDetail(ScheduleListActivity.this, selected);
							}
						});
				dialog = builder.create();

				// make the listview look like the 'ListView'-styled xml ListViews
				PinglyUtils.styleListView(this,dialog);

				break;
			default:
				Log.e(LOG_TAG, "Unknown dialog ID " + id);
				dialog = null;
		}
		return dialog;
	}




}