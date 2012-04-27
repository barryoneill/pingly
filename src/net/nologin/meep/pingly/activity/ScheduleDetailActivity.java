package net.nologin.meep.pingly.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import net.nologin.meep.pingly.PinglyConstants;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.adapter.ScheduleRepeatTypeAdapter;
import net.nologin.meep.pingly.alarm.AlarmScheduler;
import net.nologin.meep.pingly.model.*;
import net.nologin.meep.pingly.model.probe.Probe;
import net.nologin.meep.pingly.util.PinglyUtils;
import net.nologin.meep.pingly.view.PinglyBooleanPref;
import net.nologin.meep.pingly.view.PinglyExpanderPref;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static net.nologin.meep.pingly.PinglyConstants.*;


public class ScheduleDetailActivity extends BasePinglyActivity {

	private Probe probe;
	private ScheduleEntry schedule;

	private PinglyBooleanPref scheduleEnabled, scheduleNotifySuccess, scheduleNotifyFailure;
	private PinglyExpanderPref scheduleStartTime, scheduleRepetition;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_detail);


		probe = loadProbeParamIfPresent();
		if (probe == null) { // should never happen, but is it the correct handling?
			throw new IllegalArgumentException("This activity expects requires a proble ID parameter");
		}

		// if there's a schedule param, we use that
		schedule = loadScheduleParamIfPresent();
		if(schedule != null){
			// ensure probe is the right one if a schedule entry is specified
			probe = schedule.probe;
		}
		else{
			// new schedule
			schedule = new ScheduleEntry(probe);
		}


		TextView probeName = (TextView) findViewById(R.id.scheduled_probe_name);
		TextView probeSummary = (TextView) findViewById(R.id.scheduled_probe_summary);

		scheduleEnabled = (PinglyBooleanPref) findViewById(R.id.scheduled_probe_enabled);
		scheduleStartTime = (PinglyExpanderPref) findViewById(R.id.scheduled_probe_start_time);
		scheduleRepetition = (PinglyExpanderPref) findViewById(R.id.scheduled_probe_repetition);

		scheduleNotifySuccess = (PinglyBooleanPref) findViewById(R.id.scheduled_probe_notify_success);
		scheduleNotifyFailure = (PinglyBooleanPref) findViewById(R.id.scheduled_probe_notify_failure);


		// init view
		probeName.setText(probe.name);
		probeSummary.setText(probe.desc);
		scheduleEnabled.setChecked(schedule.active);
		scheduleNotifySuccess.setChecked(schedule.notifyOnSuccess);
		scheduleNotifyFailure.setChecked(schedule.notifyOnFailure);
		updateStartTimeSummary();
		updateRepetitionSummary();

		// attach listeners
		findViewById(R.id.but_scheduledetail_cancel).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		findViewById(R.id.but_scheduledetail_save).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				Log.d(LOG_TAG, "Saving schedule: " + schedule);

				// update object before save
				// start time, repetition automatically updated on dialog 'save' actions
				schedule.active = scheduleEnabled.getChecked();
				schedule.notifyOnSuccess = scheduleNotifySuccess.getChecked();
				schedule.notifyOnFailure = scheduleNotifyFailure.getChecked();

				scheduleDAO.saveScheduleEntry(schedule);

				Log.w(LOG_TAG,"Entry " + schedule + " saved, now setting up alarm");
				AlarmScheduler.setAlarm(ScheduleDetailActivity.this, schedule);

				// TODO: i18n
				Toast.makeText(ScheduleDetailActivity.this,"Entry successfully scheduled",Toast.LENGTH_SHORT).show();

				goToScheduleList(v);
			}
		});

	}


	// called by onclick on 'start time' view
	public void configureStartTime(View v) {

		View layout = inflateScheduleDialogLayout(R.layout.schedule_detail_dialog_starttime);

		final View specificTimeGrp = layout.findViewById(R.id.schedule_starttime_specifictime_grp);
		final RadioGroup radioGrp = (RadioGroup) layout.findViewById(R.id.schedule_starttime_radiogrp);
		final RadioButton radioOnSave = (RadioButton) layout.findViewById(R.id.schedule_starttime_radio_onsave);
		final RadioButton radioSpecific = (RadioButton) layout.findViewById(R.id.schedule_starttime_radio_specific);
		final TextView dateInfo = (TextView) layout.findViewById(R.id.schedule_starttime_dateinfo);
		final TextView timeInfo = (TextView) layout.findViewById(R.id.schedule_starttime_timeinfo);
		final Button dateBut = (Button) layout.findViewById(R.id.schedule_starttime_datebut);
		final Button timeBut = (Button) layout.findViewById(R.id.schedule_starttime_timebut);

		// dialog holds its own 'date'.  If canceled, current schedule start date remains untouched
		final Calendar localStartTime = Calendar.getInstance();
		if (schedule.startTime != null) {
			localStartTime.setTime(schedule.startTime);
		}

		radioOnSave.setChecked(schedule.startOnSave);
		radioSpecific.setChecked(!schedule.startOnSave);
		specificTimeGrp.setVisibility(schedule.startOnSave ? View.INVISIBLE : View.VISIBLE);

		dateInfo.setText(new SimpleDateFormat(FMT_DAY_DATE_DISPLAY).format(localStartTime.getTime()));
		timeInfo.setText(new SimpleDateFormat(FMT_12HR_MIN_TZ_DISPLAY).format(localStartTime.getTime()));

		radioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				boolean specific = checkedId == R.id.schedule_starttime_radio_specific;
				specificTimeGrp.setVisibility(specific ? View.VISIBLE : View.INVISIBLE);
			}
		});

		dateBut.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				DatePickerDialog dialog = new DatePickerDialog(ScheduleDetailActivity.this,

						new DatePickerDialog.OnDateSetListener() {

							@Override
							public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

								localStartTime.set(Calendar.YEAR, year);
								localStartTime.set(Calendar.MONTH, monthOfYear);
								localStartTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
								dateInfo.setText(new SimpleDateFormat(FMT_DAY_DATE_DISPLAY).format(localStartTime.getTime()));
							}
						},
						// init with current time
						localStartTime.get(Calendar.YEAR),
						localStartTime.get(Calendar.MONTH),
						localStartTime.get(Calendar.DAY_OF_MONTH));

				dialog.show();
			}
		});

		timeBut.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				TimePickerDialog dialog = new TimePickerDialog(ScheduleDetailActivity.this,
						new TimePickerDialog.OnTimeSetListener() {
							public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

								localStartTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
								localStartTime.set(Calendar.MINUTE, minute);
								timeInfo.setText(new SimpleDateFormat(FMT_12HR_MIN_TZ_DISPLAY).format(localStartTime.getTime()));

							}
						}, localStartTime.get(Calendar.HOUR_OF_DAY), localStartTime.get(Calendar.MINUTE), false);
				dialog.show();
			}
		});

		AlertDialog.Builder builder = PinglyUtils.getAlertDialogBuilder(this);
		builder.setView(layout);
		builder.setTitle("Configure Start Time");// TODO: i18n

		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				// we're done, update the schedule held by the view with the local values
				schedule.startOnSave = radioOnSave.isChecked();
				schedule.startTime = localStartTime.getTime();
				updateStartTimeSummary();

			}
		});
		builder.setNegativeButton("Cancel", null); // TODO: i18n

		builder.create().show();

	}

	private void updateStartTimeSummary() {

		if (schedule.startOnSave) {
			scheduleStartTime.setSummary("Start Immediately on Save"); // TODO: i18n!
		} else {
			DateFormat df = new SimpleDateFormat(PinglyConstants.FMT_DATE_AND_TIME_SUMMARY);
			scheduleStartTime.setSummary(df.format(schedule.startTime));
		}

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
		ScheduleRepeatTypeAdapter adapter = new ScheduleRepeatTypeAdapter(this);
		repeatSpinner.setAdapter(adapter);

		// init
		repeatSpinner.setSelection(adapter.getItemPosition(schedule.repeatType));
		seekBar.setMax(schedule.repeatType.rangeUpperLimit - 1); // setProgress has no effect until max is set
		seekBar.setProgress(schedule.repeatValue);

		repeatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {

				ScheduleRepeatType selected = (ScheduleRepeatType) adapter.getSelectedItem();

				if (selected == ScheduleRepeatType.OnceOff) {
					seekBarGrp.setVisibility(View.INVISIBLE);
				} else {
					seekBarGrp.setVisibility(View.VISIBLE);
					seekBar.setMax(selected.rangeUpperLimit - 1); // can't set min, and starts at 0, so compensate
					rangeLower.setText(String.valueOf(1));
					rangeUpper.setText(String.valueOf(selected.rangeUpperLimit));
				}

				summary.setText(PinglyUtils.loadStringForPlural(ScheduleDetailActivity.this,
						selected.getResourceNameForSummary(), seekBar.getProgress()));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		// setup seekbar
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				ScheduleRepeatType selected = (ScheduleRepeatType) repeatSpinner.getSelectedItem();
				summary.setText(PinglyUtils.loadStringForPlural(ScheduleDetailActivity.this,
						selected.getResourceNameForSummary(), progress + 1));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { // nop
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) { // nop
			}

		});


		AlertDialog.Builder builder = PinglyUtils.getAlertDialogBuilder(this);
		builder.setView(layout);
		builder.setTitle("Configure Frequency");
		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				schedule.repeatType = (ScheduleRepeatType) repeatSpinner.getSelectedItem();
				schedule.repeatValue = seekBar.getProgress() + 1;
				updateRepetitionSummary();

			}
		});
		builder.setNegativeButton("Cancel", null); // TODO: i18n

		builder.create().show();

	}


	private void updateRepetitionSummary() {


		String summary = PinglyUtils.loadStringForPlural(ScheduleDetailActivity.this,
				schedule.repeatType.getResourceNameForSummary(), schedule.repeatValue);

		scheduleRepetition.setSummary(summary);

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
//        builder.setNegativeButton("Cancel",null); // TODO: i18n
//        builder.create().show();
//
//    }

	private View inflateScheduleDialogLayout(int layoutId) {
		return View.inflate(PinglyUtils.getPinglyDialogContext(this), layoutId, (ViewGroup) getCurrentFocus());
	}

}