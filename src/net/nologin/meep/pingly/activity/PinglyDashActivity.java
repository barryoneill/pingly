package net.nologin.meep.pingly.activity;

import android.util.Log;
import net.nologin.meep.pingly.R;
import android.os.Bundle;
import android.view.View;
import net.nologin.meep.pingly.util.PinglyUtils;

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

				Log.d(LOG_TAG, "Dashboard selection - probe list");
				PinglyUtils.startActivityProbeList(this);
				break;

			case R.id.but_dash_newProbe:

				Log.d(LOG_TAG, "Dashboard selection - new probe");
				PinglyUtils.startActivityProbeDetail(this);
				break;

			case R.id.but_dash_schedule:

				Log.d(LOG_TAG, "Dashboard selection - schedule");
				PinglyUtils.startActivityScheduleList(this);
				break;

			case R.id.but_dash_settings:

				Log.d(LOG_TAG, "Dashboard selection - settings");
				PinglyUtils.startActivitySettings(this);
				break;

			default:
				break;
		}
	}


}