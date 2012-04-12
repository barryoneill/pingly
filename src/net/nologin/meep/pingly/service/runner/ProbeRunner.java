package net.nologin.meep.pingly.service.runner;

import android.util.Log;
import net.nologin.meep.pingly.PinglyConstants;
import net.nologin.meep.pingly.model.probe.HTTPResponseProbe;
import net.nologin.meep.pingly.model.probe.PingProbe;
import net.nologin.meep.pingly.model.probe.Probe;
import net.nologin.meep.pingly.model.probe.SocketConnectionProbe;

public abstract class ProbeRunner {

	// make the returns in run() more readable
	protected final boolean RUN_SUCCESS = true;
	protected final boolean RUN_FAILED = false;

	private Probe probe;
	private ProbeUpdateListener updateListener;
	private boolean cancelRequested;

	public static ProbeRunner getInstance(Probe p){

		if(p instanceof PingProbe) {
			return new PingProbeRunner(p);
		}
		if(p instanceof SocketConnectionProbe){
			return new SocketConnectionProbeRunner(p);
		}
		if(p instanceof HTTPResponseProbe){
			return new HTTPResponseProbeRunner(p);
		}
		throw new IllegalArgumentException("No runner implementation for " + p);
	}

	public ProbeRunner(Probe probe){
		this.probe = probe;
		this.updateListener = null;
	}

	public void setUpdateListener(ProbeUpdateListener listener){
		this.updateListener = listener;
	}

	public Probe getProbe(){
		return probe;
	}

	public void cancel() {
		Log.d(PinglyConstants.LOG_TAG,"Proberunner (Probe " + probe.id + ") marked as checkCancelled.");
		cancelRequested = true;
	}

	protected void checkCancelled() throws ProbeRunCancelledException {
		if(cancelRequested){
			throw new ProbeRunCancelledException();
		}
	}

	public boolean run(){

		// no exception from a runner should go higher than this point
		try {
			return doRun();
		}
		catch(ProbeRunCancelledException e){
			publishUpdate("Probe run cancelled");
			return this.RUN_FAILED;
		}
		catch(Exception e){
			publishUpdate("Internal Error (" + e.getClass().getName() + ":" + e.getMessage());
			return this.RUN_FAILED;
		}

	}

	protected abstract boolean doRun() throws ProbeRunCancelledException;

	protected void publishUpdate(String data){
		if(updateListener!=null){
			updateListener.onUpdate(data + System.getProperty("line.separator"));
		}
	}

	protected class ProbeRunCancelledException extends Exception { }

	public interface ProbeUpdateListener {

		public void onUpdate(String newOutput);

	}



}




