package net.nologin.meep.pingly.activity;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.model.PinglyTask;
import net.nologin.meep.pingly.model.TaskRunResult;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

// https://github.com/commonsguy/cw-android/blob/master/Rotation/RotationAsync/src/com/commonsware/android/rotation/async/RotationAsync.java

public class TaskRunnerActivity extends BasePinglyActivity {

	private TextView taskName;
	private Button runAgainBut;
	private Button stopTaskBut;
	private TextView taskLogOutput;
	private ScrollView taskLogScroller;
	
	private PinglyTask currentTask;

	private AsyncTaskRunner asyncTask;
	
	@Override
	protected void onCreate(Bundle state) {

		super.onCreate(state);
		setContentView(R.layout.task_run_log);

		// parameter must be present
		currentTask = loadTaskParamIfPresent();

		Log.d(LOG_TAG, "Running task " + currentTask);

		// load refs
		taskName = (TextView) findViewById(R.id.text_newTask_name);
		runAgainBut = (Button) findViewById(R.id.but_taskRunner_runAgain);
		stopTaskBut = (Button) findViewById(R.id.but_taskRunner_cancel);
		taskLogOutput = (TextView) findViewById(R.id.task_log_output);	
		taskLogScroller = (ScrollView) findViewById(R.id.task_log_scroller);
		
		runAgainBut.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {				
				clearAndStartTask();				
			}
		});
		
		stopTaskBut.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				if(asyncTask != null && !asyncTask.isCancelled()){
					asyncTask.cancel(true);
				}
			}
		});
		
		// init view
		taskName.setText(currentTask.name);
	
//		// after activity restart (screen rotate)
//		if(state != null && state.containsKey("taskLogOutput")){
//			taskLogOutput.setText(state.getString("taskLogOutput"));
//			// hack to push to bottom _after_ scrollview resize
//			taskLogScroller.post(new Runnable() {
//		         public void run() {
//		             ((ScrollView) findViewById(R.id.task_log_scroller)).fullScroll(View.FOCUS_DOWN);
//		         }
//		 });
//		}
		
		if(asyncTask == null || asyncTask.getStatus() == AsyncTask.Status.FINISHED){			
			clearAndStartTask();
		}
		
	}
	
//	@Override
//	public void onSaveInstanceState(Bundle state) {
//	
//		super.onSaveInstanceState(state);
//		state.putString("taskLogOutput", taskLogOutput.getText().toString());
//	}
//	
	private void clearAndStartTask() {
		
		taskLogOutput.setText("");

		asyncTask = new AsyncTaskRunner();
		asyncTask.execute(currentTask);
	}
	
	
	
	private void appendLogLine(String txt){
				
		taskLogOutput.append(txt + "\n");		
		// taskLogScroller.smoothScrollTo(0, taskLogOutput.getBottom());
		taskLogScroller.fullScroll(ScrollView.FOCUS_DOWN);
	}
	
	
	
	private class AsyncTaskRunner extends AsyncTask<PinglyTask, String, TaskRunResult> {

		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			runAgainBut.setEnabled(false);
			stopTaskBut.setEnabled(true);
			
			appendLogLine("Starting task..");
			
		}
		
		@Override
		protected TaskRunResult doInBackground(PinglyTask... params) {

			publishProgress("background started");			
			
			try {
				for(int i=0;i<100;i++){
					Thread.sleep(100);
					publishProgress("At: " + i);
				}
			} catch (InterruptedException e) {
				publishProgress("Interrupted");
				e.printStackTrace();
			}
			
			publishProgress("background finished");
			
			return null;
		}
	
		@Override
		protected void onProgressUpdate(String... values) {
		
			super.onProgressUpdate(values);
			
			for(String val : values){
				appendLogLine(val);
			}
			
		}
		

		@Override
		protected void onCancelled() {

			super.onCancelled();
			
			appendLogLine("Cancelled");
			
			runAgainBut.setEnabled(true);
			stopTaskBut.setEnabled(false);
		}
		
		@Override
		protected void onPostExecute(TaskRunResult result) {
		
			appendLogLine("Post Execute");
			
			runAgainBut.setEnabled(true);
			stopTaskBut.setEnabled(false);
			
		}
	}

}
