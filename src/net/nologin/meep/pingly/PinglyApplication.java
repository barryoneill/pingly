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
package net.nologin.meep.pingly;

import android.app.Application;
import android.util.Log;
import net.nologin.meep.pingly.db.PinglyDataHelper;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

/**
 * Custom application object instance.  Not normally needed except that it's used to maintain the
 * datahelper singleton (Better than having an unnecessary static instance)
 */
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
