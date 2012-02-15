package net.nologin.meep.pingly.activity;

import net.nologin.meep.pingly.PinglyConstants;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public abstract class BasePinglyActivity extends Activity {

	// available to all actions (TODO: name? goTO?)
	public void createNewTask(View v) {

		goToTaskDetails(-1);
	}

	public void goToTaskDetails(long taskId) {
		
		Log.d(PinglyConstants.LOG_TAG, "Starting activity: "
				+ TaskDetailsActivity.class.getName());
		
		Intent intent = new Intent(getApplicationContext(),
				TaskDetailsActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);			
		
		// add parameter if valid
		if(taskId >= 0){
			Log.d(PinglyConstants.LOG_TAG, "Adding task id param: " + taskId);
			Bundle b = new Bundle();
			b.putLong(TaskDetailsActivity.PARAMETER_TASK_ID, taskId);
			intent.putExtras(b);	
		}
		
		startActivity(intent);

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
