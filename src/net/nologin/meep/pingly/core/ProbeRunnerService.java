package net.nologin.meep.pingly.core;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.activity.PinglyDashActivity;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

public class ProbeRunnerService extends IntentService {

    public static final String PARAM_SCHEDULE_ENTRY_ID = "schedule_entry_id";
    public static final String PARAM_OUT_MSG = "omsg";

    // important to have a no-paramter constructor for alarmmanager
    public ProbeRunnerService() {
        super("Probe Runner Service");
    }

    // called asynchronously by android
    @Override
    protected void onHandleIntent(Intent intent) {

		Log.e(LOG_TAG, "ProbeRunnerService, handling: " + intent.toString());

        String msg = intent.getStringExtra(PARAM_SCHEDULE_ENTRY_ID);

        showAppNotification(this,msg);


    }

    void showAppNotification(Context ctx, String msg) {


        NotificationManager mNotificationManager = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        int icon = R.drawable.pingly_notification;
        CharSequence tickerText = "Pingly Alarm";
        long when = System.currentTimeMillis();

        Notification notification = new Notification(icon, tickerText, when);
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        notification.defaults |= Notification.FLAG_AUTO_CANCEL;

        Context appContext = ctx.getApplicationContext();
        CharSequence contentTitle = "Scheduled I was";
        CharSequence contentText = "Message was '" + msg + "'";
        Intent notificationIntent = new Intent(ctx, PinglyDashActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,notificationIntent, 0);

        //noinspection deprecation
        notification.setLatestEventInfo(appContext, contentTitle, contentText,
                contentIntent);

        int HELLO_ID = 1;
        mNotificationManager.notify(HELLO_ID, notification);


        // AlarmScheduler.testIntentService(ctx);
    }


}
