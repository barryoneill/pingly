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


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import net.nologin.meep.pingly.PinglyApplication;
import net.nologin.meep.pingly.db.PinglyDataHelper;
import net.nologin.meep.pingly.db.ScheduleDAO;
import net.nologin.meep.pingly.model.ScheduleEntry;
import net.nologin.meep.pingly.model.ScheduleRepeatType;
import net.nologin.meep.pingly.service.ProbeRunnerScheduleService;

import java.util.Date;
import java.util.List;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

/**
 * Utility class providing methods to schedule and cancel alarms which run
 * probes which are scheduled.
 *
 * Notes about using the 'unused' request code to differentiate intents
 * http://stackoverflow.com/questions/7496603/how-to-create-different-pendingintent-so-filterequals-return-false
 * http://code.google.com/p/android/issues/detail?id=7780
 * http://code.google.com/p/android/issues/detail?id=863
 *
 */
public class AlarmScheduler {

    public static void setAlarmsForAllScheduledItems(Context ctx) {

        Log.i(LOG_TAG, "Rescheduling all active schedule items");

        /* I'm not sure of the best way to get access to the application
           instance from the alarms receiver (RescheduleAllAlarmsReceiver),
           so given I only have the context object, this appears to work..
         */
        PinglyApplication app = (PinglyApplication)ctx.getApplicationContext();
        PinglyDataHelper dh = app.getPinglyDataHelper();

        ScheduleDAO scheduleDAO = new ScheduleDAO(dh);

		List<ScheduleEntry> entries = scheduleDAO.findActiveEntriesForReschedule();
		for(ScheduleEntry entry : entries){
			Log.d(LOG_TAG, "Back into the schedule goes: " + entry);
			setAlarm(ctx,entry);
		}


    }

    public static void setAlarm(Context ctx, ScheduleEntry entry) {

        if (!entry.active) {
            Log.w(LOG_TAG, "Called to set alarm for " + entry + ", but item is disabled - ignoring");
            return;
        }

        // TODO: wakelock handling
        AlarmManager am = getAlarmManager(ctx);

        PendingIntent pi = buildAlarmIntent(ctx,entry);

        if(ScheduleRepeatType.OnceOff.equals(entry.repeatType)){

            Date now = new Date();
            if(now.after(entry.startTime)){
                Log.w(LOG_TAG, "Once-Off entry " + entry + " start time in the past, ignoring");
                return;
            }

			Log.w(LOG_TAG, "Setting once-off alarm for " + entry + " at " + entry.startTime.toLocaleString());
            am.set(AlarmManager.RTC_WAKEUP,entry.startTime.getTime(),pi);

        }
        else {

            long interval = entry.repeatType.getAsMillis(entry.repeatValue);
			Log.w(LOG_TAG, "Setting repeating alarm for " + entry + " at " + entry.startTime.toLocaleString()
			 					+  ", repeating every " + interval + " milliseconds");
            am.setRepeating(AlarmManager.RTC,entry.startTime.getTime(), interval, pi);
        }

    }

	public static void cancelAlarms(Context ctx, List<ScheduleEntry> entries){

		for(ScheduleEntry entry : entries){
			cancelAlarm(ctx, entry);
		}

	}

    public static void cancelAlarm(Context ctx, ScheduleEntry entry) {

        /* A PendingIntent which matches that used to set the alarm must be passed to AlarmManager.requestCancel().
         * PendingItents are equal if pi.filterEquals(Intent) matches.  According to the javadoc, that means:
         * "if their action, data, type, class, and categories are the same. This does not compare any extra data
         * included in the intents." */

		Log.w(LOG_TAG, "Cancelling alarm for " + entry);

        PendingIntent pi = buildAlarmIntent(ctx,entry);
        getAlarmManager(ctx).cancel(pi);


     }

    private static PendingIntent buildAlarmIntent(Context ctx, ScheduleEntry entry) {

        Intent intent = new Intent(ctx, ProbeRunnerReceiver.class);

        int requestCode = getIntentReqCode(ctx, entry);
        intent.putExtra(ProbeRunnerScheduleService.PARAM_SCHEDULE_ENTRY_ID, entry.id);

		Log.d(LOG_TAG, "Building intent with extra : " + intent.getExtras().get(ProbeRunnerScheduleService.PARAM_SCHEDULE_ENTRY_ID));

        return PendingIntent.getBroadcast(ctx, requestCode, intent, 0);

    }

    private static int getIntentReqCode(Context ctx, ScheduleEntry entry){

        // TODO: doc
        if(entry.id < Integer.MIN_VALUE ||  entry.id > Integer.MAX_VALUE){
            throw new IllegalArgumentException("System Error SCHED_REQ_TOKEN_TOO_LARGE");
        }
        return (int) entry.id;
    }

    private static AlarmManager getAlarmManager(Context ctx){
        return (AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE);
    }

}
