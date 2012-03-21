package net.nologin.meep.pingly.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.core.AlarmTriggeredProbeReceiver;
import net.nologin.meep.pingly.view.PinglyCustomPref;

import java.util.Calendar;
import java.util.TimeZone;

public class ScheduleListActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_details);
    }

    public void doIt(View v) {

        PinglyCustomPref prefView = (PinglyCustomPref)v;
        
        switch(v.getId()){
            
            case R.id.scheduled_probe_enabled:
                prefView.setPrefSummary("ONE WAS CLICKED");
                break;

            case R.id.scheduled_probe_enabled2:
                prefView.setPrefSummary("TWO WAS CLICKED");
                break;

        }
        
    }

//    private void setRecurringAlarm(Context context) {
//
//        Calendar updateTime = Calendar.getInstance();
//        updateTime.setTimeZone(TimeZone.getTimeZone("GMT"));
//        updateTime.set(Calendar.HOUR_OF_DAY, 11);
//        updateTime.set(Calendar.MINUTE, 45);
//
//        Intent probleReceiver = new Intent(context, AlarmTriggeredProbeReceiver.class);
//
//        PendingIntent recurringDownload = PendingIntent.getBroadcast(context,0, probleReceiver, PendingIntent.FLAG_CANCEL_CURRENT);
//
//        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//        alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, recurringDownload);
//    }
}