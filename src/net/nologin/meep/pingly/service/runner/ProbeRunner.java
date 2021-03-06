/*
 *    Pingly - A simple app for checking for signs of life in hosts/services.
 *    Copyright 2012 Barry O'Neill
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package net.nologin.meep.pingly.service.runner;

import android.content.Context;
import android.util.Log;
import net.nologin.meep.pingly.PinglyConstants;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.model.ProbeRun;
import net.nologin.meep.pingly.model.ProbeRunStatus;
import net.nologin.meep.pingly.model.probe.HTTPResponseProbe;
import net.nologin.meep.pingly.model.probe.PingProbe;
import net.nologin.meep.pingly.model.probe.Probe;
import net.nologin.meep.pingly.model.probe.SocketConnectionProbe;
import net.nologin.meep.pingly.util.PinglyUtils;

import java.util.Date;

/**
 * Base class for all ProbeRunner implementations, provides run framework with success/failure handling routines.
 * This will be called by the scheduled and interactive services to actually perform the probe runs and record
 * their outcomes.
 */
public abstract class ProbeRunner {

	private ProbeRun probeRun;
	private ProbeUpdateListener updateListener;
	private boolean cancelRequested;

	private static final String newLine = System.getProperty("line.separator");

	public static ProbeRunner getInstance(ProbeRun probeRun){

		if(probeRun.probe == null){
			throw new IllegalArgumentException("A probe must be specified");
		}

		Probe p = probeRun.probe;

        /* This isn't very 'pretty', but there are only 3 probe types at the moment.  Should there be
         * more, then perhaps some sort of lookup/config should be implemented.  It'll do as-is for now. */
		if(p instanceof PingProbe) {
			return new PingProbeRunner(probeRun);
		}
		if(p instanceof SocketConnectionProbe){
			return new SocketConnectionProbeRunner(probeRun);
		}
		if(p instanceof HTTPResponseProbe){
			return new HTTPResponseProbeRunner(probeRun);
		}
		throw new IllegalArgumentException("No runner implementation for " + p);
	}

	public ProbeRunner(ProbeRun probeRun){
		this.probeRun = probeRun;
		this.updateListener = null;
	}

	public void setUpdateListener(ProbeUpdateListener listener){
		this.updateListener = listener;
	}

	public Probe getProbe(){
		return probeRun.probe;
	}


	public void run(Context ctx){

		probeRun.status = ProbeRunStatus.Running;

		// no exception from a runner should go higher than this point
		try {

			if(requiresActiveNetConnection() && !PinglyUtils.activeNetConnectionPresent(ctx)){
				notifyFinishedWithFailure(ctx.getString(R.string.probe_run_core_err_nodataconn));
				return;
			}

			doRun(ctx);

			// in an implementation doesn't call notifyFinishedWithSuccess, status will still
			// be 'running' - will just assume success.  :o)
			if(probeRun.status == ProbeRunStatus.Running){
				Log.w(PinglyConstants.LOG_TAG,"Probe runner impl " + getClass().getSimpleName()
						+ " didn't call notifyFinishedWithSuccess, assuming success");
				notifyFinishedWithSuccess("Probe appears to have finished successfully.");
			}
		}
		catch(ProbeRunCancelledException e){
			notifyFinishedWithFailure(ctx.getString(R.string.probe_run_core_err_cancelled));
		}
		catch(Exception e){
			// anything that reaches here probably indicates a case we should handle explicitly
			Log.e(PinglyConstants.LOG_TAG, "Unhandled error",e);
			notifyFinishedWithFailure(ctx.getString(R.string.probe_run_core_err_internalerr,
						e.getClass().getName(),e.getMessage()));
		}

	}

	protected abstract void doRun(Context ctx) throws ProbeRunCancelledException;

	protected abstract boolean requiresActiveNetConnection();

	protected void notifyFinishedWithFailure(String failureSummary)  {
		probeRun.runSummary = failureSummary;
		probeRun.status = ProbeRunStatus.Failed;
		probeRun.endTime = new Date();

		probeRun.appendLogLine(failureSummary);

		if(updateListener!=null){
			updateListener.onUpdate(newLine + failureSummary + newLine);
		}
	}

	protected void notifyFinishedWithSuccess(String successSummary)  {
		probeRun.runSummary = successSummary;
		probeRun.status = ProbeRunStatus.Success;
		probeRun.endTime = new Date();

		probeRun.appendLogLine(successSummary);

		if(updateListener!=null){
			updateListener.onUpdate(newLine + successSummary + newLine);
		}
	}

	protected void notifyUpdate(String data) throws ProbeRunCancelledException {

		checkCancelled();

		probeRun.appendLogLine(data);

		if(updateListener!=null){
			updateListener.onUpdate(data);
		}

		checkCancelled();
	}

	public void requestCancel() {
		Log.d(PinglyConstants.LOG_TAG,"Cancel requested for Proberunner "+ probeRun + ".");
		probeRun.runSummary = "Canceled";
		probeRun.status = ProbeRunStatus.Failed;
		probeRun.endTime = new Date();

		cancelRequested = true;
	}

	protected void checkCancelled() throws ProbeRunCancelledException {
		if(cancelRequested){
			throw new ProbeRunCancelledException();
		}
	}


	protected class ProbeRunCancelledException extends Exception { }

	public interface ProbeUpdateListener {
		public void onUpdate(String newOutput);
	}



}




