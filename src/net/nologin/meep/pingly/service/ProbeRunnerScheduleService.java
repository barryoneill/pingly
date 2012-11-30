/*
 *    Pingly - A simple app for checking for signs of life in hosts/services.
 *    Copyright 2012 Barry O'Neill
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package net.nologin.meep.pingly.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import net.nologin.meep.pingly.PinglyApplication;
import net.nologin.meep.pingly.PinglyPrefs;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.activity.ProbeRunHistoryActivity;
import net.nologin.meep.pingly.db.PinglyDataHelper;
import net.nologin.meep.pingly.db.ProbeRunDAO;
import net.nologin.meep.pingly.db.ScheduleDAO;
import net.nologin.meep.pingly.model.ProbeRun;
import net.nologin.meep.pingly.model.ProbeRunStatus;
import net.nologin.meep.pingly.model.ScheduleEntry;
import net.nologin.meep.pingly.service.runner.ProbeRunner;
import net.nologin.meep.pingly.util.PinglyUtils;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

/**
 * This IntentService is used to run scheduled probes.  When the alarmmanagers sends the appropriate intent,
 * this service will start the appropriate proberunner, and depending on that scheduled entry's config, display
 * an notification to the user depending on the outcome of the probe.
 *
 * (See AlarmManager and ProbeRunnerReceiver for more comments)
 */
public class ProbeRunnerScheduleService extends IntentService {

	public static final String PARAM_SCHEDULE_ENTRY_ID = "net.nologin.meep.pingly.service.ProbeRunnerService_schedule_entry_id";

	private ScheduleDAO scheduleDAO;
	private ProbeRunDAO probeRunDAO;

	// important to have a no-paramter constructor for alarmmanager
	public ProbeRunnerScheduleService() {
		super("Probe Runner Service");
	}

	@Override
	public void onCreate() {

		super.onCreate();

        PinglyApplication app = (PinglyApplication)getApplication();
        PinglyDataHelper dh = app.getPinglyDataHelper();

        scheduleDAO = new ScheduleDAO(dh);
        probeRunDAO = new ProbeRunDAO(dh);
	}

	public void onDestroy() {
		super.onDestroy();
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
		int tickerFmtRes = successful ? R.string.probe_notification_success : R.string.probe_notification_failure;
		CharSequence tickerText = String.format(ctx.getString(tickerFmtRes),probeRun.probe.name);

		// setup the notification
		Notification notification = new Notification(iconRes, tickerText, probeFinishTime);
		notification.sound = soundRes;
		notification.defaults |= Notification.DEFAULT_LIGHTS;

		// vibrate only if the user has enabled it
		if(PinglyPrefs.areVibrationsAllowed(this)){
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		}

		// clear notification when clicked on
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// go directly to the probe run history for this run
		Intent probeRunHistoryIntent = new Intent(ctx, ProbeRunHistoryActivity.class);
		PinglyUtils.setIntentExtraProbeRunId(probeRunHistoryIntent, probeRun.id);

		/*  ProbeRunHistoryActivity has android:launchMode="singleTask" set in the manifest.  In combination with
		 *  FLAG_ACTIVITY_SINGLE_TOP, the activity should move to the root of the stack, being the only instance.
		 *  This has the side-effect that if the user starts pingly from a notification, goes to the activity and
		 *  presses back, they'll exit pingly.  In the android tutorial, a faked back stack is created, which
		 *  we could use to put the dashboard activity in front.
		 *  http://developer.android.com/guide/topics/ui/notifiers/notifications.html
		 *  Unfortunately, Intent.makeRestartActivityTask is only available in API 11, so I'll either
		  * live with this, or find a workaround later on if it becomes an issue.
		  * Also informative:
		  * http://stackoverflow.com/a/5522161/276183
		  */
		probeRunHistoryIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		// FLAG_UPDATE_CURRENT is required so new values for the extras (probe run id) will take effect
		PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, probeRunHistoryIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		// notification data
		notification.setLatestEventInfo(ctx.getApplicationContext(), tickerText, probeRun.runSummary, contentIntent);

		NotificationManager notifyMan = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

		// TODO: revisit
		notifyMan.notify((int)probeRun.id, notification);



	}




}
