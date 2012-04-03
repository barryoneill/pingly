package net.nologin.meep.pingly.core;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import net.nologin.meep.pingly.PinglyConstants;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.activity.PinglyDashActivity;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

// http://mobile.tutsplus.com/tutorials/android/android-fundamentals-scheduling-recurring-tasks/
// http://developer.android.com/guide/topics/ui/notifiers/notifications.html

/**
 * Called by the alarm manager to handle the task
 */
public class ProbeRunnerReceiver extends BroadcastReceiver {

    public static final String ACTION_RESP = "net.nologin.meep.Pingly.intent.action.MESSAGE_PROCESSED";

    public void onReceive(Context context, Intent intent) {

        Log.e(LOG_TAG, "ProbeRunnerReceiver: " + intent.toString());

        Intent repackage = new Intent(context, ProbeRunnerService.class);
        repackage.putExtras(intent.getExtras());

        context.startService(repackage);

    }

}

