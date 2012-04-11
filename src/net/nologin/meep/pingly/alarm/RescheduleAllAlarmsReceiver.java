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
