package net.nologin.meep.pingly.activity;

import net.nologin.meep.pingly.PinglyConstants;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;

public abstract class BasePinglyActivity extends Activity {

	// available to all actions (TODO: name? goTO?)
	public void createNewTask(View v) {

		Log.d(PinglyConstants.LOG_TAG, "Starting activity: "
				+ TaskDetailsActivity.class.getName());
		startActivity(new Intent(getApplicationContext(), TaskDetailsActivity.class));

	}

	public void goToTaskList(View v) {

		if (this instanceof TaskListActivity) {
			Log.d(PinglyConstants.LOG_TAG, "Already at task list, ignoring request");
		}
		else{
			Log.d(PinglyConstants.LOG_TAG, "Going to task list");
			Intent intent = new Intent(getApplicationContext(),
					TaskListActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);			
			startActivity(intent);
		}		
	}
	
	public void goHome(View v) {

		if (this instanceof PinglyDashActivity) {
			Log.d(PinglyConstants.LOG_TAG, "Already at home, ignoring 'home' request");
		}
		else{
			Log.d(PinglyConstants.LOG_TAG, "Going to dashboard (home)");
			Intent intent = new Intent(getApplicationContext(),
					PinglyDashActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}		
	}

}
