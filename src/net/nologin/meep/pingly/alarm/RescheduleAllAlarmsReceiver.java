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

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

/**
 * This class is registered in AndroidManifest.xml to be triggered on various events, such as a
 * reboot (BOOT_COMPLETED), an upgrade (PACKAGE_REPLACED), or a timezone change (TIMEZONE_CHANGED)
 * <br/>
 * All active entries in the database are scheduled with the AlarmManager
 *
 */
public class RescheduleAllAlarmsReceiver extends BroadcastReceiver {

    public void onReceive(Context ctx, Intent intent) {

        Log.d(LOG_TAG, getClass().getSimpleName() + " recieved broadcast with intent: " + intent.toString());

        AlarmScheduler.setAlarmsForAllScheduledItems(ctx);

    }

}
