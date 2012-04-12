package net.nologin.meep.pingly.service.runner;

import net.nologin.meep.pingly.model.probe.Probe;
import net.nologin.meep.pingly.util.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URI;

public class HTTPResponseProbeRunner extends ProbeRunner {

	public HTTPResponseProbeRunner(Probe probe){
		super(probe);
	}

	public boolean doRun() throws ProbeRunCancelledException {

		Probe probe = getProbe();

		String url = "";

		if (StringUtils.isBlank(url)) {
			publishUpdate("No URL specified");
			return RUN_FAILED;
		}

		checkCancelled();

		try {

			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();

			publishUpdate("HTTP req to: \n" + url);

			request.setURI(new URI(url));
			HttpResponse response = client.execute(request);

			// execute can take some time, check that the asynctask hasn't been checkCancelled in the meantime
			checkCancelled();

			String successMsg = "Service Responded ";

			StatusLine status = response.getStatusLine();

			if(status!=null){
				successMsg +=  " (HTTP " + status.getStatusCode() + ")";
			}

			publishUpdate(successMsg);
			return RUN_SUCCESS;

		}
		catch (Exception e){
			publishUpdate("Error: " + e.getClass().getSimpleName() + ", " + e.getMessage());
			return RUN_FAILED;
		}

	}


}
