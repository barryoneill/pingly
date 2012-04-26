package net.nologin.meep.pingly.activity;

import android.util.Log;
import net.nologin.meep.pingly.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

public class PinglyDashActivity extends BasePinglyActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_dashboard);

	}

	public void dashButtonClicked(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.but_dash_showProbes:
			goToProbeList(v);
			break;
		case R.id.but_dash_newProbe:
			createNewProbe(v);
			break;
		case R.id.but_dash_schedule:

            //doNotificationTest();
            Log.d(LOG_TAG, "Going to scheduler");
            Intent slIndent = new Intent(getApplicationContext(),
                    ScheduleListActivity.class);
            slIndent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(slIndent);

			break;
		case R.id.but_dash_settings:

            Log.d(LOG_TAG, "Going to settings");
            Intent scIntent = new Intent(getApplicationContext(),
                    SettingsActivity.class);
            scIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(scIntent);


			break;
		default:
			break;
		}
	}

	// http://developer.android.com/guide/topics/ui/notifiers/notifications.html
	private void doNotificationTest() {

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		int icon = R.drawable.pingly_notification;
		CharSequence tickerText = "TickerTxt";
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.defaults |= Notification.FLAG_AUTO_CANCEL;

		Context context = getApplicationContext();
		CharSequence contentTitle = "Notification Title";
		CharSequence contentText = "This is a long text I want to see what happens when really long text is passed in as the content to a notification.";
		Intent notificationIntent = new Intent(this, PinglyDashActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);

		int HELLO_ID = 1;

		mNotificationManager.notify(HELLO_ID, notification);

	}


//	private void showDialog(String msg) {
//		AlertDialog.Builder builder = new AlertDialog.Builder(
//				PinglyDashActivity.this);
//
//		builder.setMessage(msg)
//				.setCancelable(false)
//				.setPositiveButton("*Robotic Cough*",
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog, int id) {
//								dialog.requestCancel();
//							}
//						});
//		AlertDialog alert = builder.create();
//		alert.show();
//	}

}