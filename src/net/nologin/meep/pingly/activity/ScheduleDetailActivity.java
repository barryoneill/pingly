package net.nologin.meep.pingly.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.model.IdValuePair;
import net.nologin.meep.pingly.model.SchedulerRepetitionUnit;
import net.nologin.meep.pingly.util.PinglyUtils;
import net.nologin.meep.pingly.view.PinglyBasePrefView;

public class ScheduleDetailActivity extends BasePinglyActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_detail);


    }


    public void configureRepetition(View v) {

        ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.PinglyDialogTheme);
        View layout = v.inflate(ctw, R.layout.schedule_detail_dialog_repetition, (ViewGroup) getCurrentFocus());

        // get refs
        final Spinner repUnitSpinner = (Spinner) layout.findViewById(R.id.scheduler_repetition_unit);
        final SeekBar seekBar = (SeekBar) layout.findViewById(R.id.scheduler_repetition_freq);
        final TextView summary = (TextView) layout.findViewById(R.id.schedule_repetition_summary);
        final TextView rangeLower = (TextView) layout.findViewById(R.id.schedule_repetition_freq_lowerN);
        final TextView rangeUpper = (TextView) layout.findViewById(R.id.schedule_repetition_freq_upperN);

        // attach list to spinner
        IdValuePair[] spinnerElems = SchedulerRepetitionUnit.toSpinnerValueArray(this);
        ArrayAdapter<IdValuePair> spinnerArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerElems);
        repUnitSpinner.setAdapter(spinnerArrayAdapter);

        // init state (will trigger setOnItemSelectedListener, do dependent init there)
        repUnitSpinner.setSelection(repUnitSpinner.getFirstVisiblePosition());

        repUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {

                IdValuePair selected = (IdValuePair) adapter.getSelectedItem();
                SchedulerRepetitionUnit unit = SchedulerRepetitionUnit.fromId(selected.id);

                seekBar.setMax(unit.rangeUpperLimit - 1); // can't set min, and starts at 0, so compensate
                rangeLower.setText(String.valueOf(1));
                rangeUpper.setText(String.valueOf(unit.rangeUpperLimit));

                summary.setText(PinglyUtils.loadStringForPlural(ScheduleDetailActivity.this,
                        unit.getResourceNameForSummary(), seekBar.getProgress() + 1));
                                
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // setup seekbar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                IdValuePair selected = (IdValuePair) repUnitSpinner.getSelectedItem();
                SchedulerRepetitionUnit unit = SchedulerRepetitionUnit.fromId(selected.id);
                summary.setText(PinglyUtils.loadStringForPlural(ScheduleDetailActivity.this,
                        unit.getResourceNameForSummary(), progress + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });


        AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        builder.setView(layout);
        builder.setTitle("Configure Frequency");
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //MyActivity.this.finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        builder.create().show();

    }

    public void configureActiveDays(View v) {

        //PinglyBasePrefView prefView = (PinglyBasePrefView) v;


    }

    public void configureActiveHours(View v) {

        //PinglyBasePrefView prefView = (PinglyBasePrefView) v;


    }


}