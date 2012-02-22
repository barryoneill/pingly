package net.nologin.meep.pingly.activity;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.StringUtils;
import net.nologin.meep.pingly.model.PinglyTask;
import net.nologin.meep.pingly.model.TaskRunResult;
import net.nologin.meep.pingly.util.PinglyUtils;

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
				if (asyncTask != null && !asyncTask.isCancelled()) {
					asyncTask.cancel(true);
				}
			}
		});

		// init view
		taskName.setText(currentTask.name);

		// // after activity restart (screen rotate)
		// if(state != null && state.containsKey("taskLogOutput")){
		// taskLogOutput.setText(state.getString("taskLogOutput"));
		// // hack to push to bottom _after_ scrollview resize
		// taskLogScroller.post(new Runnable() {
		// public void run() {
		// ((ScrollView)
		// findViewById(R.id.task_log_scroller)).fullScroll(View.FOCUS_DOWN);
		// }
		// });
		// }

		if (asyncTask == null
				|| asyncTask.getStatus() == AsyncTask.Status.FINISHED) {
			clearAndStartTask();
		}

	}

	// @Override
	// public void onSaveInstanceState(Bundle state) {
	//
	// super.onSaveInstanceState(state);
	// state.putString("taskLogOutput", taskLogOutput.getText().toString());
	// }
	//
	private void clearAndStartTask() {

		taskLogOutput.setText("");

		if(!PinglyUtils.activeNetConnectionPresent(this)){
			Log.d(LOG_TAG, "No net connection, not running task");
			appendLogLine("================================");
			appendLogLine("     Network not available      ");  
			appendLogLine(" Enable data/wifi and try again ");
			appendLogLine("================================");
			return;
		}
		
		asyncTask = new AsyncTaskRunner();
		asyncTask.execute(currentTask);
	}

	private void appendLogLine(String txt) {

		taskLogOutput.append(txt + "\n");
		// taskLogScroller.smoothScrollTo(0, taskLogOutput.getBottom());
		taskLogScroller.fullScroll(ScrollView.FOCUS_DOWN);
	}

	private class AsyncTaskRunner extends
			AsyncTask<PinglyTask, String, TaskRunResult> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			runAgainBut.setEnabled(false);
			stopTaskBut.setEnabled(true);

			appendLogLine("Starting async task..");

		}

		@Override
		protected TaskRunResult doInBackground(PinglyTask... params) {

			PinglyTask t = params[0];

			publishProgress("Processing: " + t);

			if (StringUtils.isBlank(t.url)) {
				publishProgress("No URL specified, nothing to check.");
				return null;
			}

			// sanity check
			if(isCancelled()){
				publishProgress("Cancelled, request will not be made");
				return null;
			}
			
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			try {
				publishProgress("HTTP req to: " + t.url);
				request.setURI(new URI(t.url));
				HttpResponse response = client.execute(request);
				
				// execute can take some time, check that the task hasn't been cancelled in the meantime
				if(isCancelled()){
					publishProgress("Req success, but task cancelled in the meantime.");		
					return null;
				}
				
				publishProgress("========== response start ==========");				
				StatusLine status = response.getStatusLine();
				if(status != null){
					if(status.getProtocolVersion() != null){
						publishProgress("Protocol Version: " + status.getProtocolVersion().toString());
					}
					if(status.getReasonPhrase() != null){
						publishProgress("Reason Phrase: " + status.getReasonPhrase());
					}
					publishProgress("Status Code: " + status.getStatusCode());					
				}
				publishProgress(" ");
				publishProgress("- Headers: ");
				for(Header hdr : response.getAllHeaders()){
					publishProgress(hdr.getName()  + ": " + hdr.getValue());
				}
				publishProgress("========== response end ==========");
						
				
			} catch (Exception e) {
				publishProgress(e);
			}
			
			return null;
		}

		private void publishProgress(Exception e) {
			publishProgress("========== exception ==========");
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			publishProgress(sw.getBuffer().toString());
			publishProgress("========== exception ==========");
		}

		@Override
		protected void onProgressUpdate(String... values) {

			super.onProgressUpdate(values);

			for (String val : values) {
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

			appendLogLine("Async task finished.");

			runAgainBut.setEnabled(true);
			stopTaskBut.setEnabled(false);

		}
	}

}
