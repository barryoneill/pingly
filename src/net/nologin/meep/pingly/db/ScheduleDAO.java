package net.nologin.meep.pingly.db;


import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import net.nologin.meep.pingly.model.ScheduleEntry;
import net.nologin.meep.pingly.model.ScheduleRepeatType;
import net.nologin.meep.pingly.model.probe.Probe;
import net.nologin.meep.pingly.util.DBUtils;

import java.util.Date;


// TODO: just like ProbeDAO, I need to find a non-bloated ORM helper
public class ScheduleDAO extends PinglyDataHelper {

	public ScheduleDAO(Context context) {
		super(context);
	}

	public ScheduleEntry findById(long id) {

		Log.d(LOG_TAG, "Looking up entry with ID: " + id);

		SQLiteDatabase db = getReadableDatabase();
		String idClause = TBL_SCHEDULE.COL_ID + "=" + id;

		Cursor cursor = db.query(TBL_SCHEDULE.TBL_NAME, null,
				idClause, null, null, null, null);
		if (!cursor.moveToFirst()) {
			Log.d(LOG_TAG, "No entry found for ID: " + id);
			return null;
		}
		return cursorToEntry(cursor, true);
	}

	public ScheduleEntry findByProbe(long probeId) {

		Log.d(LOG_TAG, "Looking up scheduled items for probe: " + probeId);

		SQLiteDatabase db = getReadableDatabase();
		String nameClause = TBL_SCHEDULE.COL_PROBE_FK + "=?";
		Cursor cursor = db.query(TBL_SCHEDULE.TBL_NAME, null,
				nameClause, new String[]{probeId + ""}, null, null, null);

		if (!cursor.moveToFirst()) {
			Log.d(LOG_TAG, "No entries found for probeId: " + probeId);
			return null;
		}
		return cursorToEntry(cursor, true);

	}


	public Cursor queryForScheduleListCursorAdapter() {

		Log.d(LOG_TAG, "Querying Table");

		// See ScheduleListCursorAdapter.newView() for column use

		SQLiteDatabase db = getReadableDatabase();


		// see ScheduleDAO.queryForScheduleListCursorAdapter() for column use
		// (nb, CursorAdapter requires we include an '_id' column, hence TBL_SCHEDULE.COL_ID

		// note
		String query =
				"SELECT " + TBL_SCHEDULE.TBL_NAME + "." + TBL_SCHEDULE.COL_ID + ","
						+ TBL_PROBE.TBL_NAME + "." + TBL_PROBE.COL_NAME + ","
						+ TBL_SCHEDULE.TBL_NAME + "." + TBL_SCHEDULE.COL_STARTTIME + ","
						+ TBL_SCHEDULE.TBL_NAME + "." + TBL_SCHEDULE.COL_REPEATTYPE_ID + ","
						+ TBL_SCHEDULE.TBL_NAME + "." + TBL_SCHEDULE.COL_REPEAT_VALUE
						+ " FROM " + TBL_PROBE.TBL_NAME + ", " + TBL_SCHEDULE.TBL_NAME
						+ " WHERE " + TBL_PROBE.TBL_NAME + "." + TBL_PROBE.COL_ID + "="
									+ TBL_SCHEDULE.TBL_NAME + "." + TBL_SCHEDULE.COL_PROBE_FK
						+ " ORDER BY " + TBL_SCHEDULE.TBL_NAME + "." + TBL_SCHEDULE.COL_CREATED + " desc";


		Cursor cursor = db.rawQuery(query,null);

//		Cursor cursor = db.query(TBL_SCHEDULE.TBL_NAME, null, null,
//				null, null, null, TBL_SCHEDULE.COL_CREATED);


		return cursor;
	}

	public void delete(ScheduleEntry entry) {

		Log.d(LOG_TAG, "Deleting entry " + entry);

		SQLiteDatabase db = getWritableDatabase();
		String idClause = TBL_SCHEDULE.COL_ID + "=" + entry.id;
		db.delete(TBL_SCHEDULE.TBL_NAME, idClause, null);

	}

	public long saveScheduleEntry(ScheduleEntry entry) {

		Date now = new Date();
		if (entry.startOnSave || entry.startTime == null || entry.startTime.before(now)) {
			entry.startTime = now;
		}

		ContentValues cv = new ContentValues();
		cv.put(TBL_SCHEDULE.COL_PROBE_FK, entry.probe.id);
		cv.put(TBL_SCHEDULE.COL_ACTIVE, entry.active ? 1 : 0);
		cv.put(TBL_SCHEDULE.COL_STARTONSAVE, entry.startOnSave ? 1 : 0);
		cv.put(TBL_SCHEDULE.COL_STARTTIME, DBUtils.toGMTDateTimeString(entry.startTime));
		cv.put(TBL_SCHEDULE.COL_REPEATTYPE_ID, entry.repeatType.id);
		cv.put(TBL_SCHEDULE.COL_REPEAT_VALUE, entry.repeatValue);


		SQLiteDatabase db = getWritableDatabase();
		if (entry.isNew()) {
			// triggers will fill id, create/modify columns
			entry.id = db.insertOrThrow(TBL_SCHEDULE.TBL_NAME, null, cv);
		} else {

			// trigger will update last modified column
			String whereClause = TBL_SCHEDULE.COL_ID + "=" + entry.id;
			db.update(TBL_SCHEDULE.TBL_NAME, cv, whereClause, null);
		}


		return entry.id;
	}


	// keep the param so the caller doesn't forget about cursor responsibility
	// TODO: make note about not using this in long query, rather atomic gets re:load instead of join
	private ScheduleEntry cursorToEntry(Cursor c, boolean closeCursor) {

		CursorReader cr = new CursorReader(c);

		int probeFk = cr.getInt(TBL_SCHEDULE.COL_PROBE_FK);

		Probe probe = ProbeDAO.findProbeById(getDataHelperContext(), probeFk);

		ScheduleEntry entry = new ScheduleEntry(probe);

		entry.id = cr.getInt(TBL_SCHEDULE.COL_ID);
		entry.active = cr.getBoolean(TBL_SCHEDULE.COL_ACTIVE);
		entry.startOnSave = cr.getBoolean(TBL_SCHEDULE.COL_STARTONSAVE);
		entry.startTime = cr.getDate(TBL_SCHEDULE.COL_STARTTIME, false);
		entry.repeatType = ScheduleRepeatType.fromId(cr.getInt(TBL_SCHEDULE.COL_REPEATTYPE_ID));
		entry.repeatValue = cr.getInt(TBL_SCHEDULE.COL_REPEAT_VALUE);

		if (closeCursor) {
			c.close();
		}

		return entry;
	}

}
