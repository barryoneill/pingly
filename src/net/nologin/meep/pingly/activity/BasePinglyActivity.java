package net.nologin.meep.pingly.activity;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;
import net.nologin.meep.pingly.model.PinglyTask;
import net.nologin.meep.pingly.model.PinglyTaskDataHelper;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public abstract class BasePinglyActivity extends Activity {
	
	public static final String PARAMETER_TASK_ID = "param_task";

	protected PinglyTaskDataHelper taskDataHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		taskDataHelper = new PinglyTaskDataHelper(this);
	}	
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    if (taskDataHelper != null) {
	        taskDataHelper.close();
	    }
	}
	
	public PinglyTask loadTaskParamIfPresent() {

		PinglyTask result = null;
		
		// populate fields if param set	
		Bundle b = getIntent().getExtras();		
		if(b != null && b.containsKey(PARAMETER_TASK_ID)){
			Long taskId = b.getLong(PARAMETER_TASK_ID, -1);
			if(taskId >= 0){
				Log.d(LOG_TAG, "Will be loading task ID " + taskId);
								
				result = taskDataHelper.findTaskById(taskId);
				Log.d(LOG_TAG, "Got task: " + result);	
			}
		}
		
		if(result == null){
			Log.e(LOG_TAG, "No task found");
		}
		
		return result;
	}
	
	// available to all actions (TODO: name? goTO?)
	public void createNewTask(View v) {

		goToTaskDetails(-1);
	}

	public void goToTaskRunner(long taskId) {
		
		Log.d(LOG_TAG, "Starting activity: " + TaskRunnerActivity.class.getName());
		
		Intent intent = new Intent(getApplicationContext(),
				TaskRunnerActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);			
		
		addTaskIdParam(intent, taskId);
		
		startActivity(intent);

	}
	
	
	public void goToTaskDetails(long taskId) {
		
		Log.d(LOG_TAG, "Starting activity: " + TaskDetailsActivity.class.getName());
		
		Intent intent = new Intent(getApplicationContext(),
				TaskDetailsActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);			
		
		addTaskIdParam(intent, taskId);
		
		startActivity(intent);

	}
	
	private void addTaskIdParam(Intent intent, long taskId){
		// add parameter if valid
		if(taskId > 0){
			Log.d(LOG_TAG, "Adding task id param: " + taskId);
			Bundle b = new Bundle();
			b.putLong(TaskDetailsActivity.PARAMETER_TASK_ID, taskId);
			intent.putExtras(b);	
		}
	}
	
	public void goToTaskList(View v) {

		if (this instanceof TaskListActivity) {
			Log.d(LOG_TAG, "Already at task list, ignoring request");
		}
		else{
			Log.d(LOG_TAG, "Going to task list");
			Intent intent = new Intent(getApplicationContext(),
					TaskListActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);			
			startActivity(intent);
		}		
	}
	
	public void goHome(View v) {

		if (this instanceof PinglyDashActivity) {
			Log.d(LOG_TAG, "Already at home, ignoring 'home' request");
		}
		else{
			Log.d(LOG_TAG, "Going to dashboard (home)");
			Intent intent = new Intent(getApplicationContext(),
					PinglyDashActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}		
	}

}
