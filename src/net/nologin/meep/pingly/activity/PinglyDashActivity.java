package net.nologin.meep.pingly.activity;

import android.util.Log;
import net.nologin.meep.pingly.R;
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


}