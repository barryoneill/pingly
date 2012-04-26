package net.nologin.meep.pingly.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import net.nologin.meep.pingly.model.ProbeRun;
import net.nologin.meep.pingly.model.ProbeRunStatus;
import net.nologin.meep.pingly.model.ScheduleEntry;
import net.nologin.meep.pingly.model.probe.Probe;
import net.nologin.meep.pingly.util.DBUtils;

import java.util.Date;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

public class ProbeRunDAO extends PinglyDataHelper {

	public ProbeRunDAO(Context context) {
		super(context);
	}

	public ProbeRun findProbeRunById(long id) {

		Log.d(LOG_TAG, "Looking up probe run with ID: " + id);

		SQLiteDatabase db = getReadableDatabase();
		String idClause = TBL_PROBE_RUN.COL_ID + "=" + id;

		Cursor cursor = db.query(TBL_PROBE_RUN.TBL_NAME, null,
				idClause, null, null, null, null);
		if(!cursor.moveToFirst()){
			Log.d(LOG_TAG, "No probe run found for ID: " + id);
			return null;
		}
		ProbeRun log = cursorToProbeRun(cursor);
		cursor.close();

		return log;
	}

	public void deleteHistoryForProbe(long probeId) {

		Log.d(LOG_TAG, "Deleting entries for probe " + probeId);

		SQLiteDatabase db = getWritableDatabase();
		String idClause = TBL_PROBE_RUN.COL_PROBE_FK + "=" + probeId;
		db.delete(TBL_PROBE_RUN.TBL_NAME, idClause, null);

	}


	public Cursor queryForProbeRunHistoryCursorAdapter(Long probeId) {

		Log.d(LOG_TAG, "queryForProbeRunHistoryCursorAdapter (probeId=" + probeId +")");

		// See ProbeRunHistoryCursorAdapter.newView() for column use

		SQLiteDatabase db = getReadableDatabase();

		String idClause = null;
		if(probeId != null){
			idClause = TBL_PROBE_RUN.COL_PROBE_FK + "=" + probeId;
		}

		// a simple query on the run history table will do here
		Cursor cursor = db.query(TBL_PROBE_RUN.TBL_NAME, null, idClause,
					null, null, null, TBL_PROBE_RUN.COL_STARTTIME);

		return cursor;
	}


	public ProbeRun prepareNewProbeRun(Probe probe, ScheduleEntry entry) {

		ProbeRun probeRun = new ProbeRun(probe,entry);
		probeRun.id = saveProbeRun(probeRun);
		return probeRun;
	}

	public long saveProbeRun(ProbeRun probeRun) {

		Log.d(LOG_TAG, "Saving probe run " + probeRun);

		ScheduleEntry entry = probeRun.scheduleEntry;
		Date start = probeRun.startTime;
		Date end = probeRun.endTime;

		ContentValues cv = new ContentValues();
		cv.put(TBL_PROBE_RUN.COL_PROBE_FK,probeRun.probe.id);
		cv.put(TBL_PROBE_RUN.COL_SCHEDULEENTRY_FK, entry == null ? null : entry.id);
		cv.put(TBL_PROBE_RUN.COL_STARTTIME, start == null ? null : DBUtils.toGMTDateTimeString(start));
		cv.put(TBL_PROBE_RUN.COL_ENDTIME, end == null ? null : DBUtils.toGMTDateTimeString(end));
		cv.put(TBL_PROBE_RUN.COL_STATUS,probeRun.status.getKey());
		cv.put(TBL_PROBE_RUN.COL_RUN_SUMMARY, probeRun.runSummary);
		cv.put(TBL_PROBE_RUN.COL_LOGTEXT,probeRun.logText);

		SQLiteDatabase db = getWritableDatabase();
		if (probeRun.isNew()) {
			// id should be automatically generated
			return db.insertOrThrow(TBL_PROBE_RUN.TBL_NAME, null, cv);
		} else {
			String whereClause = TBL_PROBE_RUN.COL_ID + "=" + probeRun.id;
			db.update(TBL_PROBE_RUN.TBL_NAME, cv, whereClause, null);
			return probeRun.id;
		}
	}


	private ProbeRun cursorToProbeRun(Cursor c) {

		CursorReader cr = new CursorReader(c);

		int probeFk = cr.getInt(TBL_PROBE_RUN.COL_PROBE_FK);
		Probe probe = ProbeDAO.findProbeById(getDataHelperContext(), probeFk);

		// schedule will be null if it was a manual run
		int scheduleFk = cr.getInt(TBL_PROBE_RUN.COL_SCHEDULEENTRY_FK,-1);
		ScheduleEntry schedule = null;
		if(scheduleFk > 0) {
			schedule = ScheduleDAO.findScheduleEntryById(getDataHelperContext(), scheduleFk);
		}

		ProbeRun probeRun = new ProbeRun(probe,schedule);
		probeRun.id = cr.getInt(TBL_PROBE_RUN.COL_ID);
		probeRun.startTime = cr.getDate(TBL_PROBE_RUN.COL_STARTTIME, false);
		probeRun.endTime = cr.getDate(TBL_PROBE_RUN.COL_ENDTIME, false);
		probeRun.runSummary = cr.getString(TBL_PROBE_RUN.COL_RUN_SUMMARY);
		probeRun.logText = cr.getString(TBL_PROBE_RUN.COL_LOGTEXT);

		String statusKey = cr.getString(TBL_PROBE_RUN.COL_STATUS);
		probeRun.status = ProbeRunStatus.fromKey(statusKey);

		return probeRun;
	}

}
