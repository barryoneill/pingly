package net.nologin.meep.pingly.db;


import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.Date;


// TODO: just like ProbeDAO, I need to find a non-bloated ORM helper
public class ScheduleDAO extends PinglyDataHelper {

    public ScheduleDAO(Context context) {
        super(context);
    }

//
//	public ScheduleEntry findById(long id) {
//
//		Log.d(LOG_TAG, "Looking up probe with ID: " + id);
//
//		SQLiteDatabase db = getReadableDatabase();
//		String idClause = TBL_SCHEDULER_ENTRY.COL_ID + "=" + id;
//
//		Cursor cursor = db.query(TBL_SCHEDULER_ENTRY.TBL_NAME, TBL_SCHEDULER_ENTRY.FROM_ALL,
//				idClause, null, null, null, null);
//		if(!cursor.moveToFirst()){
//			Log.d(LOG_TAG, "No probe found for ID: " + id);
//			return null;
//		}
//		return cursorToEntry(cursor, true);
//	}
//
//	public ScheduleEntry findByProbe(int probeId) {
//
//		Log.d(LOG_TAG, "Looking up scheduled items for probe: " + probeId);
//
//		SQLiteDatabase db = getReadableDatabase();
//		String nameClause = TBL_SCHEDULER_ENTRY.COL_PROBE_ID + "=?";
//		Cursor cursor = db.query(TBL_SCHEDULER_ENTRY.TBL_NAME, TBL_SCHEDULER_ENTRY.FROM_ALL,
//				nameClause, new String[]{probeId+""}, null, null, null);
//
//		if(!cursor.moveToFirst()){
//			Log.d(LOG_TAG, "No entries found for probeId: " + probeId);
//			return null;
//		}
//		return cursorToEntry(cursor, true);
//
//	}
//
//
    public Cursor findAllScheduledItems() {

		Log.d(LOG_TAG, "Querying Table");

		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TBL_SCHEDULE.TBL_NAME, TBL_SCHEDULE.FROM_ALL, null,
				null, null, null, TBL_SCHEDULE.COL_CREATED);

		return cursor;
	}
//
//	public void delete(ScheduleEntry entry) {
//
//		Log.d(LOG_TAG, "Deleting entry " + entry);
//
//		SQLiteDatabase db = getWritableDatabase();
//		String idClause = TBL_SCHEDULER_ENTRY.COL_ID + "=" + entry.id;
//		db.delete(TBL_SCHEDULER_ENTRY.TBL_NAME, idClause, null);
//
//	}
//
//
//	public long save(ScheduleEntry probe) {
//
//		ContentValues cv = new ContentValues();
//
//        cv.put(TBL_SCHEDULER_ENTRY.COL_TYPE_ID, probe.type.id);
//		cv.put(TBL_SCHEDULER_ENTRY.COL_NAME, probe.name);
//		cv.put(TBL_SCHEDULER_ENTRY.COL_DESC, probe.desc);
//		cv.put(TBL_SCHEDULER_ENTRY.COL_URL, probe.url);
//
//		SQLiteDatabase db = getWritableDatabase();
//		if (probe.isNew()) {
//			// triggers will fill id, create/modify columns
//			return db.insertOrThrow(TBL_SCHEDULER_ENTRY.TBL_NAME, null, cv);
//		} else {
//
//			// leave ID and create date alone, but update last modified
//			cv.put(TBL_SCHEDULER_ENTRY.COL_LASTMOD, DATETIME_ISO8601.format(new Date()));
//
//			String whereClause = TBL_SCHEDULER_ENTRY.COL_ID + "=" + probe.id;
//			db.update(TBL_SCHEDULER_ENTRY.TBL_NAME, cv, whereClause, null);
//			return probe.id;
//		}
//	}
//
//
//	// keep the param so the caller doesn't forget about cursor responsibility
//	private ScheduleEntry cursorToEntry(Cursor c, boolean closeCursor) {
//
//		ScheduleEntry entry = new ScheduleEntry();
//        entry.id = c.getLong(c.getColumnIndexOrThrow(TBL_SCHEDULER_ENTRY.COL_ID));
//        long typeId = c.getLong(c.getColumnIndexOrThrow(TBL_SCHEDULER_ENTRY.COL_TYPE_ID));
//        entry.type = ProbeType.fromId(typeId);
//        entry.name = c.getString(c.getColumnIndexOrThrow(TBL_SCHEDULER_ENTRY.COL_NAME));
//        entry.desc = c.getString(c.getColumnIndexOrThrow(TBL_SCHEDULER_ENTRY.COL_DESC));
//        entry.url = c.getString(c.getColumnIndexOrThrow(TBL_SCHEDULER_ENTRY.COL_URL));
//
//		if (closeCursor) {
//			c.close();
//		}
//
//		return entry;
//	}

}
