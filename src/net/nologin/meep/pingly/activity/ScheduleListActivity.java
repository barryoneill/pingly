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
import android.widget.Toast;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.adapter.ProbeListCursorAdapter;
import net.nologin.meep.pingly.adapter.ScheduleListCursorAdapter;
import net.nologin.meep.pingly.alarm.AlarmScheduler;
import net.nologin.meep.pingly.db.ProbeDAO;
import net.nologin.meep.pingly.model.ScheduleEntry;
import net.nologin.meep.pingly.util.PinglyUtils;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

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
            menu.setHeaderTitle("Probe: '" + target.probe.name + "'");

            MenuInflater inflater1 = getMenuInflater();
            inflater1.inflate(R.menu.schedule_list_context, menu);

            // update the text depending on status
            MenuItem del = menu.findItem(R.id.schedule_list_contextmenu_activetoggle);
            del.setTitle(target.active ? "Deactivate" : "Activate"); // TODO: i18n

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

				// TODO: implement!
				Toast.makeText(ScheduleListActivity.this,"Edit Schedule Unimplemented!",Toast.LENGTH_SHORT).show();

				return true;

			case R.id.schedule_list_contextmenu_edit_probe:
				Log.d("PINGLY", "Edit Probe : " + entry);

				PinglyUtils.startActivityProbeDetail(this,entry.probe.id);

				return true;

            case R.id.schedule_list_contextmenu_activetoggle:
                Log.d("PINGLY", "Toggling : " + entry);

                // TODO: implement!
				Toast.makeText(ScheduleListActivity.this,"Toggle Unimplemented",Toast.LENGTH_SHORT).show();

                return true;

            case R.id.schedule_list_contextmenu_delete:

                Log.d("PINGLY", "Deleting schedule item: " + entry);

                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setMessage("Are you sure you want to delete schedule '" + entry.id + "'?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

								Log.d(LOG_TAG, "Cancelling alarms for entry " + entry);
								AlarmScheduler.cancelAlarm(ScheduleListActivity.this, entry);

								// TODO: i18n
								Toast.makeText(ScheduleListActivity.this, "Entry removed from scheduler", Toast.LENGTH_SHORT).show();

								scheduleDAO.delete(entry);

                                /* since we stay where we are (no activity state change), the startManagingCursor() registration in onCreate()
                                         * won't know to refresh the cursor/adapter.  We requery all probes and pass the new cursor to the adapter. */
                                listAdapter.changeCursor(scheduleDAO.queryForScheduleListCursorAdapter());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
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

	// TODO: i18n!
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		String title;
		String msg;
		AlertDialog.Builder builder;

		switch (id) {
			case DIALOG_NO_PROBES:
				title = "No Probes Available";
				msg = "No probes are available for scheduling. Please create one first.";

				builder = new AlertDialog.Builder(this);
				builder.setTitle(title)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setMessage(msg)
						.setCancelable(true)
						.setPositiveButton("Create Probe", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								PinglyUtils.startActivityProbeDetail(ScheduleListActivity.this);
							}
						})
						.setNegativeButton("Cancel", null);
				dialog = builder.create();
				break;
			case DIALOG_CHOOSE_PROBE:

				title = "Select Probe";

				final Cursor allProbesCursor = probeDAO.findAllProbes();
				final ProbeListCursorAdapter probeListAdapter = new ProbeListCursorAdapter(this,allProbesCursor);

				builder = new AlertDialog.Builder(this);
				builder.setTitle(title)
						.setIcon(android.R.drawable.ic_dialog_info)
						.setCancelable(true)
						.setNeutralButton("Create New", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								PinglyUtils.startActivityProbeDetail(ScheduleListActivity.this);
							}
						})
						.setNegativeButton("Cancel", null)
						.setAdapter(probeListAdapter, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								long selectedProbeId = ProbeDAO.cursorToProbeId(allProbesCursor);
								PinglyUtils.startActivityScheduleEntryDetail(ScheduleListActivity.this,selectedProbeId);
							}
						});
				dialog = builder.create();

				break;
			default:
				Log.e(LOG_TAG, "Unknown dialog ID " + id);
				dialog = null;
		}
		return dialog;
	}

}