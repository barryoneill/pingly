package net.nologin.meep.pingly.model;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

// TODO: This is *vomit* inducing - evaluate a _small_ ORM lib (even ORMLite is too big) and get rid of hand rolled SQL
// TODO: error handling
public class PinglyDataHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "Pingly.db";
	private static final int DATABASE_VERSION = 1;

	// for saving strings into 'DATETIME' fields, ContentValues lacks support
	private static final DateFormat DATETIME_ISO8601 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static final class TBL_TASK {
		public static final String TBL_NAME = "pingly_tasks";

		public static final String COL_ID = BaseColumns._ID;
		public static final String COL_NAME = "task_name";
		public static final String COL_DESC = "desc";
		public static final String COL_URL = "url";
		public static final String COL_CREATED = "t_created";
		public static final String COL_LASTMOD = "t_lastmod";

		// make sure names match above
		public static final String CREATE_SQL = "CREATE TABLE pingly_tasks " + "("
				+ "   _id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "   t_created DATETIME DEFAULT CURRENT_TIMESTAMP,"
				+ "   t_lastmod DATETIME DEFAULT CURRENT_TIMESTAMP,"
				+ "   task_name TEXT NOT NULL UNIQUE," 
				+ "   desc TEXT,"
				+ "   url URL" + ")";
		
		public static final String[] FROM_ALL = { COL_ID, COL_NAME, COL_DESC,
				COL_URL, COL_CREATED, COL_LASTMOD };
	}

	public PinglyDataHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		Log.d(LOG_TAG, "Creating table: " + TBL_TASK.CREATE_SQL);

		db.execSQL(TBL_TASK.CREATE_SQL);
	}

	// TODO: verify impl!
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		Log.d(LOG_TAG, "Upgrading table");

		db.execSQL("DROP TABLE IF EXISTS " + TBL_TASK.TBL_NAME);
		onCreate(db);
	}

	public PinglyTask findTaskById(long id) {

		Log.d(LOG_TAG, "Looking up task " + id);

		SQLiteDatabase db = getReadableDatabase();
		String idClause = TBL_TASK.COL_ID + "=" + id;

		Cursor cursor = db.query(TBL_TASK.TBL_NAME, TBL_TASK.FROM_ALL,
				idClause, null, null, null, null);
		cursor.moveToFirst();

		return cursorToTask(cursor, true);

	}

	public Cursor findAllTasks() {

		Log.d(LOG_TAG, "Querying Table");

		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TBL_TASK.TBL_NAME, TBL_TASK.FROM_ALL, null,
				null, null, null, TBL_TASK.COL_NAME);

		return cursor;
	}

	public void deleteTask(PinglyTask task) {

		Log.d(LOG_TAG, "Deleting task " + task);

		SQLiteDatabase db = getReadableDatabase();
		String idClause = TBL_TASK.COL_ID + "=" + task.id;
		db.delete(TBL_TASK.TBL_NAME, idClause, null);

	}

	public long saveTask(PinglyTask task) {

		ContentValues cv = new ContentValues();

		cv.put(TBL_TASK.COL_NAME, task.name);
		cv.put(TBL_TASK.COL_DESC, task.desc);
		cv.put(TBL_TASK.COL_URL, task.url);

		SQLiteDatabase db = getWritableDatabase();
		if (task.isNew()) {			
			// triggers will fill id, create/modify columns
			return db.insertOrThrow(TBL_TASK.TBL_NAME, null, cv);
		} else {
			
			// leave ID and create date alone, but update last modified
			cv.put(TBL_TASK.COL_LASTMOD, DATETIME_ISO8601.format(new Date()));
			
			String whereClause = TBL_TASK.COL_ID + "=" + task.id;
			db.update(TBL_TASK.TBL_NAME, cv, whereClause, null);
			return task.id;
		}
	}

	public boolean isTaskNameInUse(String name) {

		Log.d(LOG_TAG, "Checking for duplicate name: " + name);
						
		String querySQL = String.format("SELECT %s from %s where %s=?", 
				TBL_TASK.COL_ID, TBL_TASK.TBL_NAME, TBL_TASK.COL_ID);
		
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(querySQL, new String[]{name});
		
		return cursor.moveToFirst(); // should be false (no results) if the name
										// isn't in use

	}

	// keep the param so the caller doesn't forget about cursor responsibility
	private PinglyTask cursorToTask(Cursor c, boolean closeCursor) {

		PinglyTask task = new PinglyTask();
		task.id = c.getLong(c.getColumnIndexOrThrow(TBL_TASK.COL_ID));
		task.name = c.getString(c.getColumnIndexOrThrow(TBL_TASK.COL_NAME));
		task.desc = c.getString(c.getColumnIndexOrThrow(TBL_TASK.COL_DESC));
		task.url = c.getString(c.getColumnIndexOrThrow(TBL_TASK.COL_URL));

		if (closeCursor) {
			c.close();
		}

		return task;
	}

}
