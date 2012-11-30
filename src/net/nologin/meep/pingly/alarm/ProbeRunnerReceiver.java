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
package net.nologin.meep.pingly.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import net.nologin.meep.pingly.service.ProbeRunnerScheduleService;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

/**
 * Registered in the AndroidManifest, this broadcast reciever repackages the intents fired by the
 * AlarmManager (configured by AlarmScheduler) so that they can be processed by the
 * ProbeRunnerScheduleService.  Based on strategies suggested by the following tutorials:
 *
 * http://mobile.tutsplus.com/tutorials/android/android-fundamentals-scheduling-recurring-tasks/
 * http://developer.android.com/guide/topics/ui/notifiers/notifications.html
 *
 */
public class ProbeRunnerReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {

        Log.e(LOG_TAG, "ProbeRunnerReceiver: " + intent.toString());

        Intent repackage = new Intent(context, ProbeRunnerScheduleService.class);
        repackage.putExtras(intent.getExtras());

        context.startService(repackage);

    }

}

