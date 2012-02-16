package net.nologin.meep.pingly.model;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.nologin.meep.pingly.activity.PinglyDashActivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

// TODO: This is *vomit* inducing - evaluate a _small_ ORM lib (even ORMLite is too big) and get rid of hand rolled SQL
// TODO: error handling
public class PinglyTaskDataHelper extends SQLiteOpenHelper {

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

	public PinglyTaskDataHelper(Context context) {
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

		Log.d(LOG_TAG, "Looking up task with ID: " + id);

		SQLiteDatabase db = getReadableDatabase();
		String idClause = TBL_TASK.COL_ID + "=" + id;

		Cursor cursor = db.query(TBL_TASK.TBL_NAME, TBL_TASK.FROM_ALL,
				idClause, null, null, null, null);
		if(!cursor.moveToFirst()){
			Log.d(LOG_TAG, "No task found for ID: " + id);
			return null;
		}
		return cursorToTask(cursor, true);	
	}

	public PinglyTask findTaskByName(String name) {

		Log.d(LOG_TAG, "Looking up task with name: " + name);
		
		SQLiteDatabase db = getReadableDatabase();	
		String nameClause = TBL_TASK.COL_NAME + "=?";
		Cursor cursor = db.query(TBL_TASK.TBL_NAME, TBL_TASK.FROM_ALL,
				nameClause, new String[]{name}, null, null, null);
		
		if(!cursor.moveToFirst()){
			Log.d(LOG_TAG, "No task found for name: " + name);
			return null;
		}
		return cursorToTask(cursor, true);	

	}

	
	public Cursor findAllTasks() {

		Log.d(LOG_TAG, "Querying Table");

		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TBL_TASK.TBL_NAME, TBL_TASK.FROM_ALL, null,
				null, null, null, TBL_TASK.COL_CREATED);

		return cursor;
	}

	// danger here
	public void deleteAll(){
		
		Log.d(LOG_TAG, "Deleting all tasks ");
		
		SQLiteDatabase db = getWritableDatabase();		
		db.delete(TBL_TASK.TBL_NAME, null, null);
	}
	
	public void deleteTask(PinglyTask task) {

		Log.d(LOG_TAG, "Deleting task " + task);

		SQLiteDatabase db = getWritableDatabase();
		String idClause = TBL_TASK.COL_ID + "=" + task.id;
		db.delete(TBL_TASK.TBL_NAME, idClause, null);

	}

	// dummy some test data
	public void generateTestItems() {
	
		String[][] items = {
				{"Guardian Football Mobile", "Check the mobile version of the Guardian Football site", "http://m.guardian.co.uk/football?cat=football"},
				{"Test Google","Do a test run against Google","http://www.google.com"},
				{"Test Google HTTPS","Do a test run against Google (https)","https://www.google.com"},
				{"Microsoft","Same again, against Microsoft","http://www.microsoft.com"},
				{"Redbrick","This is a really long string to test that the truncation in the list view is working","http://www.redbrick.dcu.ie"},
				{"Scrabblefinder","Ding ding ding", "http://www.scrabblefinder.com"}
		};
		
		for(String[] line : items){
			
			String name = line[0];
			if(findTaskByName(name) != null){
				// names have to be unique, add something to further duplicates
				name += " [" + Long.toHexString(System.currentTimeMillis()) + "]";	
			}
			
			saveTask(new PinglyTask(name,line[1],line[2]));
		}
		
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
