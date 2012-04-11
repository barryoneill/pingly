package net.nologin.meep.pingly.service.runner;

import android.util.Log;
import net.nologin.meep.pingly.PinglyConstants;
import net.nologin.meep.pingly.model.Probe;
import net.nologin.meep.pingly.util.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URI;

public class ProbeRunner {

	// make the returns in run() more readable
	private final boolean RUN_SUCCESS = true;
	private final boolean RUN_FAILED = false;

	private Probe probe;
	private ProbeUpdateListener updateListener;
	private boolean cancelRequested;

	public ProbeRunner(Probe probe){
		this.probe = probe;
		this.updateListener = null;
	}

	public void setUpdateListener(ProbeUpdateListener listener){
		this.updateListener = listener;
	}

	public void cancel() {
		Log.d(PinglyConstants.LOG_TAG,"Proberunner (Probe " + probe.id + ") marked as cancelRequested.");
		cancelRequested = true;
	}

	private boolean cancelRequested(){
		return cancelRequested;
	}

	public boolean run() {

		if (StringUtils.isBlank(probe.url)) {
			publishUpdateLine("No URL specified");
			return RUN_FAILED;
		}

		// sanity check
		if(cancelRequested()){
			return RUN_FAILED;
		}

		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet();
		try {


			publishUpdateLine("Sleeping for 2");
			Thread.sleep(2000);

			publishUpdateLine("HTTP req to: " + probe.url);

			request.setURI(new URI(probe.url));
			HttpResponse response = client.execute(request);


			// execute can take some time, check that the asynctask hasn't been cancelRequested in the meantime
			if(cancelRequested()){
				return RUN_FAILED;
			}

			publishUpdateLine("========== response start ==========");
			StatusLine status = response.getStatusLine();
			if(status != null){
				if(status.getProtocolVersion() != null){
					publishUpdateLine("Protocol Version: " + status.getProtocolVersion().toString());
				}
				if(status.getReasonPhrase() != null){
					publishUpdateLine("Reason Phrase: " + status.getReasonPhrase());
				}
				publishUpdateLine("Status Code: " + status.getStatusCode());
			}
			publishUpdateLine(" ");
			publishUpdateLine("- Headers: ");
			for(Header hdr : response.getAllHeaders()){
				publishUpdateLine(hdr.getName() + ": " + hdr.getValue());
			}
			publishUpdateLine("========== response end ==========");


		}
		catch (Exception e){
			publishUpdateLine("Error: " + e.getClass().getSimpleName() + ", " + e.getMessage());
			return RUN_FAILED;
		}


		return RUN_SUCCESS;
	}

	public void publishUpdate(String data){
		if(updateListener!=null){
			updateListener.onUpdate(data);
		}
	}

	private void publishUpdateLine(String data) {
		publishUpdate(data + System.getProperty("line.separator"));

	}

	public interface ProbeUpdateListener {

		public void onUpdate(String newOutput);

	}



}




