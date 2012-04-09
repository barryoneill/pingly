package net.nologin.meep.pingly.core;

import android.app.Application;
import android.util.Log;
import net.nologin.meep.pingly.model.InteractiveProbeRunInfo;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

public class PinglyApplication extends Application {

	public InteractiveProbeRunInfo probeRunLog;

	public void onCreate() {
		super.onCreate();
		Log.d(LOG_TAG,"Pingly Application OnCreate!");

		probeRunLog = new InteractiveProbeRunInfo(-1); // dummy
	}

	// TODO: doc
	public InteractiveProbeRunInfo getProbeRunInfo(Long probeRunId, boolean forNew){

		// new probe run, replace with new log object and return
		if(forNew){
			probeRunLog = new InteractiveProbeRunInfo(probeRunId);
			return probeRunLog;
		}

		// existing probe run, same id, return what we have
		if(probeRunId == probeRunLog.probeRunId){
			return probeRunLog;
		}

		// older service run, just give a dummy for it to write to
		return new InteractiveProbeRunInfo(probeRunId);
	}



}
