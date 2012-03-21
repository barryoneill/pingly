package net.nologin.meep.pingly.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.model.DayOfWeek;
import net.nologin.meep.pingly.model.SchedulerRepetitionUnit;
import net.nologin.meep.pingly.view.PinglyBasePrefView;

public class ScheduleDetailActivity extends BasePinglyActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_detail);


    }

    public void configureRepetition(View v) {

        Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.schedule_detail_dialog_repetition);
        dialog.setTitle("Configure Repetition");

        Spinner repetitionUnit = (Spinner)dialog.findViewById(R.id.scheduler_repetition_unit);
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,
                                                            android.R.layout.simple_spinner_item,
                                                            SchedulerRepetitionUnit.toSpinnerValueArray(this));
        repetitionUnit.setAdapter(spinnerArrayAdapter);

        repetitionUnit.setSelection(SchedulerRepetitionUnit.Minutes.id);

        dialog.show();

    }

    public void configureActiveDays(View v) {

        PinglyBasePrefView prefView = (PinglyBasePrefView)v;

        
    }

    public void configureActiveHours(View v) {

        PinglyBasePrefView prefView = (PinglyBasePrefView)v;



    }
    

}