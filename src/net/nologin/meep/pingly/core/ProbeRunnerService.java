package net.nologin.meep.pingly.core;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.activity.PinglyDashActivity;
import net.nologin.meep.pingly.db.ScheduleDAO;
import net.nologin.meep.pingly.model.ScheduleEntry;

import java.util.Date;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

public class ProbeRunnerService extends IntentService {

    public static final String PARAM_SCHEDULE_ENTRY_ID = "net.nologin.meep.pingly.core.ProbeRunnerService_entry_id";

	private ScheduleDAO scheduleDAO;

    // important to have a no-paramter constructor for alarmmanager
    public ProbeRunnerService() {
        super("Probe Runner Service");
    }

	@Override
	public void onCreate(){

		super.onCreate();
		scheduleDAO = new ScheduleDAO(this);
	}

	public void onDestroy() {
		super.onDestroy();
		scheduleDAO.close();
	}

    // called asynchronously by android
    @Override
    protected void onHandleIntent(Intent intent) {

		Log.d(LOG_TAG, "ProbeRunnerService, handling: " + intent.toString());

		long entryId = intent.getExtras().getLong(PARAM_SCHEDULE_ENTRY_ID);
		if(entryId < 0){
			Log.e(LOG_TAG,"Alarm triggered, but invalid entry ID supplied, ignoring alarm!");
			return;
		}

		ScheduleEntry entry = scheduleDAO.findById(entryId);
		if(entry == null){
			Log.e(LOG_TAG, "Entry for ID " + entryId + " was not found, nothing to do!");
			return;
		}

		int alarmCount = intent.getExtras().getInt(Intent.EXTRA_ALARM_COUNT);

		Log.i(LOG_TAG, "ProbeRunner called on scheduled entry : " + entry + " (alarm count: " + alarmCount + ")");
        showAppNotification(this, entry, alarmCount);


    }

    void showAppNotification(Context ctx, ScheduleEntry entry, int alarmCount) {


        NotificationManager mNotificationManager = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        int icon = R.drawable.pingly_notification;
        CharSequence tickerText = "Pingly Alarm";
        long when = System.currentTimeMillis();

        Notification notification = new Notification(icon, tickerText, when);
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        notification.defaults |= Notification.FLAG_AUTO_CANCEL;

        Context appContext = ctx.getApplicationContext();
        CharSequence contentTitle = "Latest Entry ID " + entry.id;
        CharSequence contentText = "On Probe '" + entry.probe + "', alarm count " + alarmCount;
		contentText = new Date().toLocaleString();
        Intent notificationIntent = new Intent(ctx, PinglyDashActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,notificationIntent, 0);

        notification.setLatestEventInfo(appContext, contentTitle, contentText,
                contentIntent);

        int HELLO_ID = 1;
        mNotificationManager.notify(HELLO_ID, notification);


        // AlarmScheduler.testIntentService(ctx);
    }


}