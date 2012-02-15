package net.nologin.meep.pingly.activity;

import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.adapter.PinglyCursorTaskAdapter;
import net.nologin.meep.pingly.model.PinglyTaskDataHelper;
import net.nologin.meep.pingly.model.PinglyTask;

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
import android.widget.Toast;

public class TaskListActivity extends BasePinglyActivity {

	private PinglyTaskDataHelper data;
	private PinglyCursorTaskAdapter listAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_list);

		data = new PinglyTaskDataHelper(this);

		// init_dummy_list();

		Cursor allTasksCursor = data.findAllTasks();
		listAdapter = new PinglyCursorTaskAdapter(this,allTasksCursor);
		ListView lv = (ListView) findViewById(R.id.taskList);
		lv.setAdapter(listAdapter);
		registerForContextMenu(lv);

		View empty = findViewById(R.id.emptyListElem);	    
	    lv.setEmptyView(empty);
		
//	    int[] colors = {0, 0xFF007700, 0}; //green
//	    lv.setDivider(new GradientDrawable(Orientation.RIGHT_LEFT, colors));
//	    lv.setDividerHeight(1);

		// any activity 
		startManagingCursor(allTasksCursor);
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (data != null) {
			data.close();
		}
	}

	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);

		if (v.getId() == R.id.taskList) {

			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

			PinglyTask target = data.findTaskById(info.id);
			menu.setHeaderTitle("Task: '" + target.name + "'");

			MenuInflater inflater1 = getMenuInflater();
			inflater1.inflate(R.menu.task_list_context, menu);

			
			
			// menu.setHeaderTitle(Countries[info.position]);
			// String[] menuItems =
			// getResources().getStringArray(R.array.taskList_context_menu);
			// for (int i = 0; i < menuItems.length; i++) {
			// menu.add(Menu.NONE, i, i, menuItems[i]);
			// }
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();

		final PinglyTask target = data.findTaskById(info.id);
		
		switch (item.getItemId()) {

			case R.id.task_list_contextmenu_edit:
				Log.d("PINGLY", "Would be editing item: " + target);
				
				goToTaskDetails(target.id);
				
				return true;


			case R.id.task_list_contextmenu_delete:				
				Log.d("PINGLY", "Deleting item: " + target);
				
				AlertDialog dialog = new AlertDialog.Builder(this)
						.setMessage("Are you sure you want to delete item '" + target.name + "'?")
				       .setCancelable(false)
				       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				        	   data.deleteTask(target);
								/* since we stay where we are (no activity state change), the startManagingCursor() registration in onCreate()
								 * won't know to refresh the cursor/adapter.  We requery all tasks and pass the new cursor to the adapter. */
								listAdapter.changeCursor(data.findAllTasks());								
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
