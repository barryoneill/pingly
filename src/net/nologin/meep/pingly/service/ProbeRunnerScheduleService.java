package net.nologin.meep.pingly.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.activity.PinglyDashActivity;
import net.nologin.meep.pingly.db.ScheduleDAO;
import net.nologin.meep.pingly.model.ScheduleEntry;

import java.util.Date;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

public class ProbeRunnerScheduleService extends IntentService {

    public static final String PARAM_SCHEDULE_ENTRY_ID = "net.nologin.meep.pingly.service.ProbeRunnerService_schedule_entry_id";

	private ScheduleDAO scheduleDAO;

    // important to have a no-paramter constructor for alarmmanager
    public ProbeRunnerScheduleService() {
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

		Log.d(LOG_TAG, "ProbeRunnerScheduleService, handling: " + intent.toString());

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

		Log.i(LOG_TAG, "ProbeRunner called on scheduled entry : " + entry);
        showAppNotification(this, entry);


    }

    void showAppNotification(Context ctx, ScheduleEntry entry) {


        NotificationManager mNotificationManager = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        int icon = R.drawable.pingly_notification;
        CharSequence tickerText = "Pingly Alarm";
        long when = System.currentTimeMillis();

        Notification notification = new Notification(icon, tickerText, when);
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        notification.defaults |= Notification.FLAG_AUTO_CANCEL;

        Context appContext = ctx.getApplicationContext();
        CharSequence contentTitle = "Entry ID " + entry;
        CharSequence contentText = "Probe:'" + entry.probe + "', time: " + new Date().toLocaleString();

		Intent notificationIntent = new Intent(ctx, PinglyDashActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,notificationIntent, 0);

        notification.setLatestEventInfo(appContext, contentTitle, contentText,
                contentIntent);

        mNotificationManager.notify((int) entry.id, notification);


        // AlarmScheduler.testIntentService(ctx);
    }


}
