package net.nologin.meep.pingly.core;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.activity.PinglyDashActivity;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

public class AlarmTriggeredProbeReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {

        Log.e(LOG_TAG, "Wahey! : " + intent.toString());

        doNotificationTest(context);

        // start the download
//        Intent downloader = new Intent(context, TutListDownloaderService.class);
//        downloader.setData(Uri
//                .parse("http://feeds.feedburner.com/MobileTuts?format=xml"));
//        context.startService(downloader);

    }

    // http://developer.android.com/guide/topics/ui/notifiers/notifications.html
    private void doNotificationTest(Context ctx) {

        NotificationManager mNotificationManager = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        int icon = R.drawable.pingly_notification;
        CharSequence tickerText = "PinglyStart!";
        long when = System.currentTimeMillis();

        Notification notification = new Notification(icon, tickerText, when);
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        notification.defaults |= Notification.FLAG_AUTO_CANCEL;

        Context appContext = ctx.getApplicationContext();
        CharSequence contentTitle = "Pingly Was Started";
        CharSequence contentText = "AlarmTriggeredProbeReceiver.onReceive was called!";
        Intent notificationIntent = new Intent(ctx, PinglyDashActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,notificationIntent, 0);

        notification.setLatestEventInfo(appContext, contentTitle, contentText,
                contentIntent);

        int HELLO_ID = 1;

        mNotificationManager.notify(HELLO_ID, notification);

    }
}
