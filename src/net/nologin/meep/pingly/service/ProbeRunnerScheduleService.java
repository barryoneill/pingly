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
import net.nologin.meep.pingly.db.ProbeDAO;
import net.nologin.meep.pingly.db.ProbeRunDAO;
import net.nologin.meep.pingly.db.ScheduleDAO;
import net.nologin.meep.pingly.model.ProbeRun;
import net.nologin.meep.pingly.model.ScheduleEntry;
import net.nologin.meep.pingly.service.runner.ProbeRunner;
import net.nologin.meep.pingly.util.PinglyUtils;

import java.util.Date;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

public class ProbeRunnerScheduleService extends IntentService {

    public static final String PARAM_SCHEDULE_ENTRY_ID = "net.nologin.meep.pingly.service.ProbeRunnerService_schedule_entry_id";

	private ScheduleDAO scheduleDAO;
	private ProbeDAO probeDAO;
	private ProbeRunDAO probeRunDAO;

    // important to have a no-paramter constructor for alarmmanager
    public ProbeRunnerScheduleService() {
        super("Probe Runner Service");
    }

	@Override
	public void onCreate(){

		super.onCreate();
		scheduleDAO = new ScheduleDAO(this);
		probeDAO = new ProbeDAO(this);
		probeRunDAO = new ProbeRunDAO(this);
	}

	public void onDestroy() {
		super.onDestroy();
		scheduleDAO.close();
		probeDAO.close();
		probeRunDAO.close();
	}

    // called asynchronously by android
    @Override
    protected void onHandleIntent(Intent intent) {

		Log.d(LOG_TAG, "ProbeRunnerScheduleService, handling: " + intent.toString());

		// load all the required data, lots of checking
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

		// ready, running the probe
		final StringBuffer buf = new StringBuffer();
		final ProbeRunner runner = ProbeRunner.getInstance(entry.probe);
		runner.setUpdateListener(new ProbeRunner.ProbeUpdateListener() {
			@Override
			public void onUpdate(String newOutput) {
				buf.append(newOutput);
			}
		});
		boolean runSuccessful = runner.run();

		// save the log
		ProbeRun probeRun = new ProbeRun(entry.probe, entry);
		if(runSuccessful){
			probeRun.setFinishedWithSuccess();
		}
		else{
			probeRun.setFinishedWithFailure();
		}
		probeRunDAO.saveProbeRun(probeRun);

		// TODO, check scheduler hasn't been disabled/ entry disabled/ entry deleted since

		Log.i(LOG_TAG, "Probe Run on " + entry + " successful:" + runSuccessful);
        showAppNotification(this, entry.probe.id, buf.toString());


    }

    void showAppNotification(Context ctx, long id, String msg) {


        NotificationManager mNotificationManager = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        int icon = R.drawable.pingly_notification;
        CharSequence tickerText = "Pingly Alarm";
        long when = System.currentTimeMillis();

        Notification notification = new Notification(icon, tickerText, when);
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        notification.defaults |= Notification.FLAG_AUTO_CANCEL;

		notification.sound = PinglyUtils.getSelectedNotificationSound(ctx);

        Context appContext = ctx.getApplicationContext();
		CharSequence contentTitle = "ID:" + id;
        CharSequence contentText = msg + " (time: " + new Date().toLocaleString() + ")";

		Intent notificationIntent = new Intent(ctx, PinglyDashActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,notificationIntent, 0);

        notification.setLatestEventInfo(appContext, contentTitle, contentText, contentIntent);

        mNotificationManager.notify((int)id, notification);


        // AlarmScheduler.testIntentService(ctx);
    }




}
