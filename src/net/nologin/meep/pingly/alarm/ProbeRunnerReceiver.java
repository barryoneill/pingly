package net.nologin.meep.pingly.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import net.nologin.meep.pingly.service.ProbeRunnerScheduleService;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

// http://mobile.tutsplus.com/tutorials/android/android-fundamentals-scheduling-recurring-tasks/
// http://developer.android.com/guide/topics/ui/notifiers/notifications.html

/**
 * Called by the alarm manager to handle the task
 */
public class ProbeRunnerReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {

        Log.e(LOG_TAG, "ProbeRunnerReceiver: " + intent.toString());

        Intent repackage = new Intent(context, ProbeRunnerScheduleService.class);
        repackage.putExtras(intent.getExtras());

        context.startService(repackage);

    }

}

