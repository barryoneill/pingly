package net.nologin.meep.pingly.activity;

import android.app.AlertDialog;
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
import net.nologin.meep.pingly.adapter.ScheduleListCursorAdapter;
import net.nologin.meep.pingly.alarm.AlarmScheduler;
import net.nologin.meep.pingly.model.ScheduleEntry;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

public class ScheduleListActivity extends BasePinglyActivity {

    private ScheduleListCursorAdapter listAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_list);

        Cursor schedCursor = scheduleDAO.findAllScheduledItems();
        listAdapter = new ScheduleListCursorAdapter(this,schedCursor);
        ListView lv = (ListView) findViewById(R.id.scheduleList);
        lv.setAdapter(listAdapter);
        registerForContextMenu(lv);

        View empty = findViewById(R.id.emptyListElem);
        lv.setEmptyView(empty);


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.scheduleList) {

            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

            ScheduleEntry target = scheduleDAO.findById(info.id);
            menu.setHeaderTitle("Probe: '" + target.probe + "'");

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

            case R.id.schedule_list_contextmenu_activetoggle:
                Log.d("PINGLY", "Toggling : " + entry);

                // TODO: implement!

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
                                listAdapter.changeCursor(scheduleDAO.findAllScheduledItems());
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


}