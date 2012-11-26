package net.nologin.meep.pingly;

import android.app.Application;
import android.util.Log;
import net.nologin.meep.pingly.db.PinglyDataHelper;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

public class PinglyApplication extends Application {

    // datahelper approach as suggested in the following
    // great posts:
    // http://stackoverflow.com/a/8889012/276183
    // http://stackoverflow.com/a/3901198/276183

    private PinglyDataHelper dataHelper;

	public void onCreate() {
		super.onCreate();

		Log.d(LOG_TAG, "PinglyApplication start");

        // init helper here so we save callers later having to pass the app ctx
        // though it means non-static so we can be sure it was created
        dataHelper = new PinglyDataHelper(this);
        Log.d(LOG_TAG, "PinglyApplication - data helper init complete");


	}

    public PinglyDataHelper getPinglyDataHelper(){
        return dataHelper;
    }

    @Override
    public void onTerminate() {

        super.onTerminate();
        dataHelper.close();
        dataHelper = null;

        Log.d(LOG_TAG, "PinglyApplication terminated. (LONG LIVE PINGLY)");
    }

}
