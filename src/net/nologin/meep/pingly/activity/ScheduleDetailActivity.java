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
import net.nologin.meep.pingly.model.ScheduleEntry;
import net.nologin.meep.pingly.model.ScheduleRepeatType;
import net.nologin.meep.pingly.model.probe.Probe;
import net.nologin.meep.pingly.util.PinglyUtils;
import net.nologin.meep.pingly.view.PinglyBooleanPref;
import net.nologin.meep.pingly.view.PinglyExpanderPref;
import net.nologin.meep.pingly.view.PinglyProbeDetailsView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static net.nologin.meep.pingly.PinglyConstants.*;


public class ScheduleDetailActivity extends BasePinglyActivity {

	private ScheduleEntry schedule;

	private PinglyBooleanPref scheduleEnabled, scheduleNotifySuccess, scheduleNotifyFailure;
	private PinglyExpanderPref scheduleStartTime, scheduleRepetition;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_detail);

		// if this param is present, we're editing a schedule entry
		schedule = getIntentExtraScheduleEntry();

		// if it's not, it's a new schedule.  A probe parameter must be specified in this case
		if(schedule == null){

			Probe probe = getIntentExtraProbe();
			if (probe == null) { // should never happen, but is it the correct handling?
				throw new IllegalArgumentException("To create a new schedule, a probe must be specified");
			}

			schedule = new ScheduleEntry(probe);
		}



		PinglyProbeDetailsView probeDetails = (PinglyProbeDetailsView)findViewById(R.id.scheduled_probe_details);

		scheduleEnabled = (PinglyBooleanPref) findViewById(R.id.scheduled_probe_enabled);
		scheduleStartTime = (PinglyExpanderPref) findViewById(R.id.scheduled_probe_start_time);
		scheduleRepetition = (PinglyExpanderPref) findViewById(R.id.scheduled_probe_repetition);

		scheduleNotifySuccess = (PinglyBooleanPref) findViewById(R.id.scheduled_probe_notify_success);
		scheduleNotifyFailure = (PinglyBooleanPref) findViewById(R.id.scheduled_probe_notify_failure);

		// init view
		probeDetails.initForProbe(schedule.probe,true);
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
				// start time & repetition automatically updated on dialog 'save' actions
				schedule.active = scheduleEnabled.getChecked();
				schedule.notifyOnSuccess = scheduleNotifySuccess.getChecked();
				schedule.notifyOnFailure = scheduleNotifyFailure.getChecked();

				int successMsgId = schedule.isNew() ? R.string.toast_schedule_added : R.string.toast_probe_updated;

				schedule.id = scheduleDAO.saveScheduleEntry(schedule);

				if(schedule.active){
					Log.d(LOG_TAG,"Active schedule " + schedule.id + " saved, setting alarm");
					AlarmScheduler.setAlarm(ScheduleDetailActivity.this, schedule);
				}
				else{
					Log.d(LOG_TAG,"Inactive schedule " + schedule.id + " saved, removing alarm, if set");
					AlarmScheduler.cancelAlarm(ScheduleDetailActivity.this, schedule);
				}

				PinglyUtils.showToast(ScheduleDetailActivity.this,successMsgId, schedule.probe.name);

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
		final RadioButton radioSpecific = (RadioButton) layout.findViewById(R.id.schedule_starttime_radio_later);
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
				boolean specific = checkedId == R.id.schedule_starttime_radio_later;
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
		builder.setTitle(R.string.schedule_detail_starttime_dialogtitle);

		builder.setPositiveButton(R.string.button_save, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				// we're done, update the schedule held by the view with the local values
				schedule.startOnSave = radioOnSave.isChecked();
				schedule.startTime = localStartTime.getTime();
				updateStartTimeSummary();

			}
		});
		builder.setNegativeButton(R.string.button_cancel, null);

		builder.create().show();

	}

	private void updateStartTimeSummary() {

		if (schedule.startOnSave) {
			scheduleStartTime.setSummary(getString(R.string.schedule_detail_starttime_startonsave));
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
		builder.setTitle(R.string.schedule_detail_frequency_dialogtitle);
		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				schedule.repeatType = (ScheduleRepeatType) repeatSpinner.getSelectedItem();
				schedule.repeatValue = seekBar.getProgress() + 1;
				updateRepetitionSummary();

			}
		});
		builder.setNegativeButton(R.string.button_cancel, null);

		builder.create().show();

	}


	private void updateRepetitionSummary() {

		String summary = PinglyUtils.loadStringForPlural(ScheduleDetailActivity.this,
				schedule.repeatType.getResourceNameForSummary(), schedule.repeatValue);

		scheduleRepetition.setSummary(summary);

	}

	private View inflateScheduleDialogLayout(int layoutId) {
		return View.inflate(PinglyUtils.getPinglyDialogContext(this), layoutId, (ViewGroup) getCurrentFocus());
	}

}