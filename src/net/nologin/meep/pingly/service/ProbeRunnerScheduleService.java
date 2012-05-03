package net.nologin.meep.pingly.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import net.nologin.meep.pingly.PinglyPrefs;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.activity.ProbeRunHistoryActivity;
import net.nologin.meep.pingly.db.ProbeDAO;
import net.nologin.meep.pingly.db.ProbeRunDAO;
import net.nologin.meep.pingly.db.ScheduleDAO;
import net.nologin.meep.pingly.model.ProbeRun;
import net.nologin.meep.pingly.model.ProbeRunStatus;
import net.nologin.meep.pingly.model.ScheduleEntry;
import net.nologin.meep.pingly.service.runner.ProbeRunner;
import net.nologin.meep.pingly.util.PinglyUtils;


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
	public void onCreate() {

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
		if (entryId < 0) {
			Log.e(LOG_TAG, "Alarm triggered, but invalid entry ID supplied, ignoring alarm!");
			return;
		}
		ScheduleEntry scheduleEntry = scheduleDAO.findById(entryId);
		if (scheduleEntry == null) {
			Log.e(LOG_TAG, "Entry for ID " + entryId + " was not found, nothing to do!");
			return;
		}

		ProbeRun probeRun = new ProbeRun(scheduleEntry.probe, scheduleEntry);

		final ProbeRunner runner = ProbeRunner.getInstance(probeRun);
		runner.run(this);

		probeRun.id = probeRunDAO.saveProbeRun(probeRun,true);

		// TODO, check scheduler hasn't been disabled/ entry disabled/ entry deleted since

		Log.i(LOG_TAG, "Probe Run on " + scheduleEntry + " successful:" + probeRun.status);

		if (doNotification(probeRun)) {
			showNotification(this,probeRun);
		}

	}

	boolean doNotification(ProbeRun probeRun) {

		// possibly add in a global preference to disable notifications
		ScheduleEntry entry = probeRun.scheduleEntry;

		return (entry.notifyOnSuccess && ProbeRunStatus.Success.equals(probeRun.status))
				||
				(entry.notifyOnFailure && ProbeRunStatus.Failed.equals(probeRun.status));

	}


	void showNotification(Context ctx, ProbeRun probeRun) {

		boolean successful = ProbeRunStatus.Success.equals(probeRun.status);

		// green or red icons for status
		int iconRes = successful ? R.drawable.pingly_notification_success : R.drawable.pingly_notification_failure;

		// set in the app settings screen
		Uri soundRes = PinglyPrefs.getNotificationSound(ctx);

		// run should have an end time (if not, use now)
		long probeFinishTime = probeRun.endTime != null ? probeRun.endTime.getTime() : System.currentTimeMillis();

		// Probe 'blah' was successful, etc
		CharSequence tickerText = probeRun.status.formatForProbe(ctx, probeRun.probe.name);

		// setup the notification
		Notification notification = new Notification(iconRes, tickerText, probeFinishTime);
		notification.sound = soundRes;
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.defaults |= Notification.FLAG_AUTO_CANCEL;

		// what it's going to start
		Intent probeRunHistoryIntent = new Intent(ctx, ProbeRunHistoryActivity.class);
		PinglyUtils.setIntentExtraProbeId(probeRunHistoryIntent, probeRun.id);

		// FLAG_UPDATE_CURRENT is required so new values for the extras (probe run id) will take effect
		PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, probeRunHistoryIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		// notification data
		notification.setLatestEventInfo(ctx.getApplicationContext(), tickerText, probeRun.runSummary, contentIntent);

		NotificationManager notifyMan = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

		// TODO: revisit
		notifyMan.notify((int)probeRun.id, notification);



	}



}
