package net.nologin.meep.pingly.core;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import net.nologin.meep.pingly.db.ScheduleDAO;
import net.nologin.meep.pingly.model.ScheduleEntry;
import net.nologin.meep.pingly.model.ScheduleRepeatType;

import java.util.Calendar;
import java.util.Date;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

/*
 * Notes about using the 'unused' request code to differentiate intents
 * http://stackoverflow.com/questions/7496603/how-to-create-different-pendingintent-so-filterequals-return-false
 * http://code.google.com/p/android/issues/detail?id=7780
 * http://code.google.com/p/android/issues/detail?id=863
 *
 */

public class AlarmScheduler {

    public static void setAlarmsForAllScheduledItems(Context ctx) {

        Log.i(LOG_TAG, "Rescheduling all active schedule items");

        ScheduleDAO scheduleDAO = new ScheduleDAO(ctx);

        Intent intent = new Intent(ctx, ProbeRunnerService.class);
        // intent.putExtra(ProbeRunnerService.PARAM_SCHEDULE_ENTRY_ID, strInputMsg);

        scheduleDAO.close();

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

    public static void cancelAlarm(Context ctx, ScheduleEntry entry) {

        /* A PendingIntent which matches that used to set the alarm must be passed to AlarmManager.cancel().
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
        intent.putExtra(ProbeRunnerService.PARAM_SCHEDULE_ENTRY_ID, entry.id);

		Log.d(LOG_TAG, "Building intent with extra : " + intent.getExtras().get(ProbeRunnerService.PARAM_SCHEDULE_ENTRY_ID));

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
