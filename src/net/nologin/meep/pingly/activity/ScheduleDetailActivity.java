package net.nologin.meep.pingly.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.model.*;
import net.nologin.meep.pingly.util.PinglyUtils;
import net.nologin.meep.pingly.view.PinglyBooleanPref;
import net.nologin.meep.pingly.view.PinglyExpanderPref;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class ScheduleDetailActivity extends BasePinglyActivity {

    private ScheduleEntry schedule;    

    TextView probeName;
    TextView probeSummary;
    
    PinglyBooleanPref scheduleEnabled;
    PinglyExpanderPref scheduleStartTime;
    PinglyExpanderPref scheduleRepetition;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_detail);

        schedule = new ScheduleEntry();        
        schedule.probe = loadProbeParamIfPresent();
        if(schedule.probe == null){ // should never happen, but is it the correct handling?
            throw new IllegalArgumentException("This activity expects requires a proble ID parameter");
        }

        probeName = (TextView) findViewById(R.id.scheduled_probe_name);
        probeSummary = (TextView) findViewById(R.id.scheduled_probe_summary);
        scheduleEnabled = (PinglyBooleanPref) findViewById(R.id.scheduled_probe_enabled);
        scheduleStartTime = (PinglyExpanderPref) findViewById(R.id.scheduled_probe_start_time);
        scheduleRepetition = (PinglyExpanderPref) findViewById(R.id.scheduled_probe_repetition);

        
        // TODO: i18n!
        probeName.setText("Probe: " + schedule.probe.name);
        probeSummary.setText(schedule.probe.desc);
        scheduleEnabled.setChecked(schedule.active);


    }



    public void configureStartTime(View v) {

        View layout = inflateScheduleDialogLayout(R.layout.schedule_detail_dialog_starttime);

        final View futureControlsGrp = layout.findViewById(R.id.schedule_starttime_futureControlsGrp);
        final RadioGroup radioGrp = (RadioGroup) layout.findViewById(R.id.schedule_starttime_radiogrp);
        final RadioButton radioNow = (RadioButton) layout.findViewById(R.id.schedule_starttime_radionow);
        final TextView dateInfo = (TextView) layout.findViewById(R.id.schedule_starttime_dateinfo);
        final TextView timeInfo = (TextView) layout.findViewById(R.id.schedule_starttime_timeinfo);
        final Button dateBut = (Button) layout.findViewById(R.id.schedule_starttime_datebut);
        final Button timeBut = (Button) layout.findViewById(R.id.schedule_starttime_timebut);

        // TODO: to be updated
        Calendar now = Calendar.getInstance();
        dateInfo.setText(new SimpleDateFormat("EEE, d MMM yyyy").format(now.getTime()));
        timeInfo.setText(new SimpleDateFormat("h:mm a").format(now.getTime()));

        // init
        radioNow.setSelected(true);
        futureControlsGrp.setVisibility(View.INVISIBLE);

        radioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                boolean futureSelected = checkedId == R.id.schedule_starttime_radiofuture;
                futureControlsGrp.setVisibility(futureSelected ? View.VISIBLE : View.INVISIBLE);
            }
        });

        dateBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: change this
                Calendar onClickTime = Calendar.getInstance();
                
                DatePickerDialog dateDialog = new DatePickerDialog(ScheduleDetailActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
                                Calendar c= Calendar.getInstance();
                                c.set(Calendar.YEAR,year);
                                c.set(Calendar.MONTH, monthOfYear);
                                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                dateInfo.setText(new SimpleDateFormat("EEE, d MMM yyyy").format(c.getTime()));
                            }},
                            // init with current time
                            onClickTime.get(Calendar.YEAR),onClickTime.get(Calendar.MONTH),onClickTime.get(Calendar.DAY_OF_MONTH));
                // dateDlg.updateDate(2012,12,25);
                dateDialog.show();
            }
        });
        
        timeBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: change this
                Calendar onClickTime = Calendar.getInstance();

                TimePickerDialog timeDialog = new TimePickerDialog(ScheduleDetailActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Calendar c= Calendar.getInstance();
                                c.set(Calendar.HOUR_OF_DAY,hourOfDay);
                                c.set(Calendar.MINUTE, minute);
                                timeInfo.setText(new SimpleDateFormat("h:mm a").format(c.getTime()));

                            }
                        }, onClickTime.get(Calendar.HOUR_OF_DAY), onClickTime.get(Calendar.MINUTE), false);
                timeDialog.show();
            }
        });

        AlertDialog.Builder builder = getAlertDialogBuilder();
        builder.setView(layout);
        builder.setTitle("Configure Start Time");

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

    public void configureRepetition(View v) {

        View layout = inflateScheduleDialogLayout(R.layout.schedule_detail_dialog_repetition);
        
        // get refs
        final Spinner repeatSpinner = (Spinner) layout.findViewById(R.id.schedule_repetition_type);
        final View seekBarGrp = layout.findViewById(R.id.schedule_repetiton_seekBarGrp);
        final SeekBar seekBar = (SeekBar) layout.findViewById(R.id.schedule_repetition_freq);
        final TextView summary = (TextView) layout.findViewById(R.id.schedule_repetition_summary);
        final TextView rangeLower = (TextView) layout.findViewById(R.id.schedule_repetition_freq_lowerN);
        final TextView rangeUpper = (TextView) layout.findViewById(R.id.schedule_repetition_freq_upperN);

        // attach list to spinner
        IdValuePair[] spinnerElems = ScheduleRepeatType.toAdapterValueArray(this);
        ArrayAdapter<IdValuePair> spinnerAdapter = new ArrayAdapter<IdValuePair>(this, android.R.layout.simple_spinner_item, spinnerElems);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repeatSpinner.setAdapter(spinnerAdapter);

        // init state (will trigger setOnItemSelectedListener, do dependent init there)
        repeatSpinner.setSelection(repeatSpinner.getFirstVisiblePosition());

        repeatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {

                IdValuePair selected = (IdValuePair) adapter.getSelectedItem();
                ScheduleRepeatType repType = ScheduleRepeatType.fromId(selected.id);

                if(repType == ScheduleRepeatType.OnceOff){
                    seekBarGrp.setVisibility(View.GONE);
                }
                else{
                    seekBarGrp.setVisibility(View.VISIBLE);
                    seekBar.setMax(repType.rangeUpperLimit - 1); // can't set min, and starts at 0, so compensate
                    rangeLower.setText(String.valueOf(1));
                    rangeUpper.setText(String.valueOf(repType.rangeUpperLimit));
                }

                summary.setText(PinglyUtils.loadStringForPlural(ScheduleDetailActivity.this,
                        repType.getResourceNameForSummary(), seekBar.getProgress() + 1));
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // setup seekbar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                IdValuePair selected = (IdValuePair) repeatSpinner.getSelectedItem();
                ScheduleRepeatType unit = ScheduleRepeatType.fromId(selected.id);
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


        AlertDialog.Builder builder = getAlertDialogBuilder();
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

//    public void configureActiveDays(View v) {
//
//        // resource list for days of week
//        String[] stringValues = DayOfWeek.toStringValueArray(this);
//        boolean[] selections = new boolean[stringValues.length];
//        for(int i=0; i<selections.length; i++){
//            selections[i] = true;
//        }
//
//        AlertDialog.Builder builder = getAlertDialogBuilder();
//        builder.setTitle("Choose Days")
//            .setMultiChoiceItems(stringValues, selections, new DialogInterface.OnMultiChoiceClickListener(){
//                    @Override
//                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//
//                    }
//                });
//        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                //MyActivity.this.finish();
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                dialog.cancel();
//            }
//        });
//        builder.create().show();
//
//    }

    private Context getDialogContext(){
        return new ContextThemeWrapper(this, R.style.PinglyDialogTheme);
    }

    private View inflateScheduleDialogLayout(int layoutId){
        return View.inflate(getDialogContext(), layoutId, (ViewGroup) getCurrentFocus());
    }

    private AlertDialog.Builder getAlertDialogBuilder() {
        return new AlertDialog.Builder(getDialogContext());
    }

}