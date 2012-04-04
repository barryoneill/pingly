package net.nologin.meep.pingly.core;


import android.app.Activity;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import net.nologin.meep.pingly.PinglyConstants;

public class ProbeRunnerInteractiveService extends IntentService {

	protected PendingIntent callbackIntent = null;

	public static final String EXTRA_CALLBACK_INTENT = "CallbackIntent";

	public static final String FILTER_UPDATE_DATA = "net.nologin.meep.pingly.core.ProbeRunnerInteractiveService.FILTER_UPDATE_DATA";

	public static final String EXTRA_DATA_LOGTEST = "net.nologin.meep.pingly.core.ProbeRunnerInteractiveService.EXTRA_DATA_LOGTEST";

	public ProbeRunnerInteractiveService() {
		super("ProbeRunnerInteractiveService");
	}



	@Override
	protected void onHandleIntent(Intent intent) {

		callbackIntent = (PendingIntent) intent
				.getParcelableExtra(EXTRA_CALLBACK_INTENT);

		if (callbackIntent == null) {
			throw new RuntimeException("There is no pending intent!");
		}

		Log.e(PinglyConstants.LOG_TAG,
				" --------------  handling onHandleIntent, sleeping for 2s");
		SystemClock.sleep(2000);

		Log.e(PinglyConstants.LOG_TAG,
				" --------------  sending broadcast to " + FILTER_UPDATE_DATA);
		Intent updateIntent = new Intent(FILTER_UPDATE_DATA);
		updateIntent.putExtra(EXTRA_DATA_LOGTEST, "wokka wokka");
		sendBroadcast(updateIntent);

		Log.e(PinglyConstants.LOG_TAG,
				" --------------  handling onHandleIntent, sleeping for another 2s");
		SystemClock.sleep(2000);


		Log.e(PinglyConstants.LOG_TAG,
				" --------------  finished sleeping, doing callback");


		// If you finished, use one of the two methods to send the result or an error
		Intent i = new Intent();
		// BLAH i.putExtra(PROGRESS_DATA_RESULT, null);// blah

		try {

			callbackIntent.send(this, Activity.RESULT_OK, i);
		} catch (PendingIntent.CanceledException e) {
			Log.e(PinglyConstants.LOG_TAG,
					"There is something wrong with the pending intent", e);
		}

		//failed(exception, optionalMessage);
	}




}