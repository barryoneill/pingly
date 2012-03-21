package net.nologin.meep.pingly.core;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

public class ProbeIntentService extends IntentService {

    public ProbeIntentService() {
        super("Probe Intent Service");
    }

    @Override
    public void onStart(Intent intent, int startId) {        
        super.onStart(intent, startId);
        Log.d(LOG_TAG,"ProbeIntentService, startId:" + startId + ", intent:" + intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(LOG_TAG,"ProbeIntentService, handling:" + intent);

    }
}
