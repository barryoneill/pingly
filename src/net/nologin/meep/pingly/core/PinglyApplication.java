package net.nologin.meep.pingly.core;

import android.app.Application;
import android.util.Log;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

public class PinglyApplication extends Application {



    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG,"Pingly Application OnCreate!");
    }

}
