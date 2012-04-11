package net.nologin.meep.pingly;

import android.app.Application;
import android.util.Log;
import net.nologin.meep.pingly.model.InteractiveProbeRunInfo;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

public class PinglyApplication extends Application {

	public InteractiveProbeRunInfo probeRunInfo;

	public void onCreate() {
		super.onCreate();
		Log.d(LOG_TAG,"Pingly Application OnCreate!");

// 		probeRunInfo = new InteractiveProbeRunInfo(-1,-1); // dummy
	}

	public InteractiveProbeRunInfo createProbeRunInfo(long probeRunId, long probeId){

		probeRunInfo = new InteractiveProbeRunInfo(probeRunId,probeId);
		return probeRunInfo;

	}

	// TODO: doc
	public InteractiveProbeRunInfo getProbeRunInfo(Long probeRunId){

		// set, and id matches, return it
		if(probeRunInfo != null && probeRunId == probeRunInfo.probeRunId){
			return probeRunInfo;
		}

		return null;

	}



}
