package net.nologin.meep.pingly.service.runner;

import android.content.Context;
import android.util.Log;
import net.nologin.meep.pingly.PinglyConstants;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.model.ProbeRun;
import net.nologin.meep.pingly.model.probe.HTTPResponseProbe;
import net.nologin.meep.pingly.util.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

public class HTTPResponseProbeRunner extends ProbeRunner {

	public HTTPResponseProbeRunner(ProbeRun probeRun){
		super(probeRun);
	}

	public void doRun(Context ctx) throws ProbeRunCancelledException {

		HTTPResponseProbe httpProbe = (HTTPResponseProbe)getProbe(); // if everything is configured properly

		if (StringUtils.isBlank(httpProbe.url)) {
			notifyFinishedWithFailure(ctx.getString(R.string.probe_run_HTTP_RESP_err_no_url));
			return;
		}

		checkCancelled();

		try {

			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();

			// 'Starting request to: url'
			notifyUpdate(ctx.getString(R.string.probe_run_HTTP_RESP_reqstart, httpProbe.url));

			request.setURI(new URI(httpProbe.url));
			HttpResponse response = client.execute(request);

			// execute can take some time, check that the asynctask hasn't been checkCancelled in the meantime
			checkCancelled();

			StatusLine sl = response.getStatusLine();
			String statusCode = sl != null ? Integer.toString(sl.getStatusCode()) : "";

			// 'Response Received (Status: 200)'
			notifyFinishedWithSuccess(ctx.getString(R.string.probe_run_HTTP_RESP_successmsg, statusCode));

		}
		catch (URISyntaxException e){
			// "Error parsing URL '${url}': ${exceptionmsg}"
			String msg = ctx.getString(R.string.probe_run_HTTP_RESP_err_invalid_url, httpProbe.url, e.getMessage());
			notifyFinishedWithFailure(msg);
		}
		catch (UnknownHostException e) {
			// "Unknown Host: '${exceptionmsg}'"
			String msg = ctx.getString(R.string.probe_run_HTTP_RESP_err_unknownhost, httpProbe.url);
			notifyFinishedWithFailure(msg);
		}
		catch(HttpHostConnectException e) {
			// "Couldn't Connect: ${exceptionmsg}" (exception message is descriptive)
			String msg = ctx.getString(R.string.probe_run_HTTP_RESP_err_hostconnect, e.getMessage());
			notifyFinishedWithFailure(msg);
		}
		catch (IOException e ) {
			Log.w(PinglyConstants.LOG_TAG, e);
			String msg = ctx.getString(R.string.probe_run_general_err_io, e.getMessage());
			notifyFinishedWithFailure(msg);
		}

	}

	@Override
	protected boolean requiresActiveNetConnection() {
		return true;
	}


}
