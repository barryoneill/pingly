package net.nologin.meep.pingly.activity;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;

import net.nologin.meep.pingly.model.Probe;
import net.nologin.meep.pingly.util.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.model.ProbeResult;
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

public class ProbeRunActivity extends BasePinglyActivity {

	private TextView probeName;
    private TextView probeNamePrefix;
    private View probeInfoContainer;
	private Button runAgainBut;
	private Button stopProbeBut;
	private TextView probeLogOutput;
	private ScrollView probeLogScroller;
    
	private Probe currentProbe;

	private AsyncTaskRunner asyncTask;

	@Override
	protected void onCreate(Bundle state) {

		super.onCreate(state);
		setContentView(R.layout.probe_run_log);

		// parameter must be present
		currentProbe = loadProbeParamIfPresent();

		Log.d(LOG_TAG, "Running probe " + currentProbe);

		// load refs
        probeInfoContainer = findViewById(R.id.probeInfoContainer);
		probeName = (TextView) findViewById(R.id.text_probe_name);
        probeNamePrefix = (TextView) findViewById(R.id.text_probe_namePrefix);
		runAgainBut = (Button) findViewById(R.id.but_probeRun_runAgain);
		stopProbeBut = (Button) findViewById(R.id.but_probeRun_cancel);
		probeLogOutput = (TextView) findViewById(R.id.probe_log_output);
		probeLogScroller = (ScrollView) findViewById(R.id.probe_log_scroller);
        
		runAgainBut.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				clearAndStartProbe();
			}
		});

        stopProbeBut.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (asyncTask != null && !asyncTask.isCancelled()) {
                    asyncTask.cancel(true);
                }
            }
        });

		// init view
        probeName.setText(currentProbe.name);



		// // after activity restart (screen rotate)
		// if(state != null && state.containsKey("probeLogOutput")){
		// probeLogOutput.setText(state.getString("probeLogOutput"));
		// // hack to push to bottom _after_ scrollview resize
		// probeLogScroller.post(new Runnable() {
		// public void run() {
		// ((ScrollView)
		// findViewById(R.id.task_log_scroller)).fullScroll(View.FOCUS_DOWN);
		// }
		// });
		// }

		if (asyncTask == null
				|| asyncTask.getStatus() == AsyncTask.Status.FINISHED) {
			clearAndStartProbe();
		}

	}



	// @Override
	// public void onSaveInstanceState(Bundle state) {
	//
	// super.onSaveInstanceState(state);
	// state.putString("probeLogOutput", probeLogOutput.getText().toString());
	// }
	//
	private void clearAndStartProbe() {

		probeLogOutput.setText("");

		if(!PinglyUtils.activeNetConnectionPresent(this)){
			Log.d(LOG_TAG, "No net connection, not running probe");
			appendLogLine("================================");
			appendLogLine("     Network not available      ");  
			appendLogLine(" Enable data/wifi and try again ");
			appendLogLine("================================");
			return;
        }

        probeInfoContainer.setBackgroundResource(R.color.probe_status_running);

		asyncTask = new AsyncTaskRunner();
		asyncTask.execute(currentProbe);
	}

	private void appendLogLine(String txt) {

		probeLogOutput.append(txt + "\n");
		// probeLogScroller.smoothScrollTo(0, probeLogOutput.getBottom());
        probeLogScroller.fullScroll(ScrollView.FOCUS_DOWN);
	}

	private class AsyncTaskRunner extends
			AsyncTask<Probe, String, ProbeResult> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			runAgainBut.setEnabled(false);
			stopProbeBut.setEnabled(true);

			appendLogLine("Starting async task..");

		}

		@Override
		protected ProbeResult doInBackground(Probe... params) {

			Probe t = params[0];

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

				// execute can take some time, check that the asynctask hasn't been cancelled in the meantime
				if(isCancelled()){
					publishProgress("Req success, but probe cancelled in the meantime.");
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

            probeInfoContainer.setBackgroundResource(R.color.probe_status_inactive);

            runAgainBut.setEnabled(true);
			stopProbeBut.setEnabled(false);
		}

		@Override
		protected void onPostExecute(ProbeResult result) {

			appendLogLine("Async task finished.");

            // TODO: status failed?
            probeInfoContainer.setBackgroundResource(R.color.probe_status_success);

            runAgainBut.setEnabled(true);
			stopProbeBut.setEnabled(false);

		}
	}

}
