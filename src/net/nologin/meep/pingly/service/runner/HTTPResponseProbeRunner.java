package net.nologin.meep.pingly.service.runner;

import net.nologin.meep.pingly.model.ProbeRun;
import net.nologin.meep.pingly.model.probe.HTTPResponseProbe;
import net.nologin.meep.pingly.util.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class HTTPResponseProbeRunner extends ProbeRunner {

	public HTTPResponseProbeRunner(ProbeRun probeRun){
		super(probeRun);
	}

	public void doRun() throws ProbeRunCancelledException {

		HTTPResponseProbe httpProbe = (HTTPResponseProbe)getProbe(); // if everything is configured properly

		if (StringUtils.isBlank(httpProbe.url)) {
			notifyFinishedWithFailure("No URL specified!");
			return;
		}

		checkCancelled();

		try {

			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();

			notifyUpdate("HTTP req to: \n" + httpProbe.url);

			request.setURI(new URI(httpProbe.url));
			HttpResponse response = client.execute(request);

			// execute can take some time, check that the asynctask hasn't been checkCancelled in the meantime
			checkCancelled();

			String successMsg = "Service Responded ";

			StatusLine status = response.getStatusLine();
			if(status!=null){
				successMsg +=  " (HTTP " + status.getStatusCode() + ")";
			}

			notifyFinishedWithSuccess(successMsg);

		}
		catch (URISyntaxException e){
			notifyFinishedWithFailure("Error parsing URI '" + httpProbe.url + "': " + e.getMessage());

		}
		catch (IOException e ) {
			notifyFinishedWithFailure("IO Error while fetching URL '" + httpProbe.url + "': " + e.getMessage());
		}

	}


}
