package net.nologin.meep.pingly.model;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

// TODO: This is *vomit* inducing - evaluate a _small_ ORM lib (even ORMLite is too big) and get rid of hand rolled SQL 
public class PinglyData extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "Pingly.db";
	private static final int DATABASE_VERSION = 1;

	public static final class TBL_TASK {
		public static final String TBL_NAME = "pingly_tasks";
			
		public static final String COL_ID = BaseColumns._ID;
		public static final String COL_NAME = "task_name";
		public static final String COL_DESC = "task_desc";
		public static final String COL_URL = "url";
		public static final String COL_CREATE_TIME = "create_time";
		
		public static final String[] FROM_ALL = { TBL_TASK.COL_ID, TBL_TASK.COL_NAME, TBL_TASK.COL_DESC, TBL_TASK.COL_URL, TBL_TASK.COL_CREATE_TIME };
	}

	public PinglyData(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// TODO: revisit data formats
		String sql_fmt = "create table %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
										"%s TEXT NOT NULL, " +
										"%s TEXT NOT NULL, " +
										"%s URL NOT NULL, " +
										"%s INTEGER)";
		String sql = String.format(sql_fmt, TBL_TASK.TBL_NAME, TBL_TASK.COL_ID,
				TBL_TASK.COL_NAME, TBL_TASK.COL_DESC, TBL_TASK.COL_URL, TBL_TASK.COL_CREATE_TIME);

		Log.d(LOG_TAG, "Creating table: " + sql);	
		
		db.execSQL(sql);
	}

	// TODO: verify impl!
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		Log.d(LOG_TAG, "Upgrading table");
		
		db.execSQL("DROP TABLE IF EXISTS " + TBL_TASK.TBL_NAME);
		onCreate(db);
	}
	
	public long updateTask(PinglyTask task){

		ContentValues cv = new ContentValues();
		populateFromTask(cv, task);
	
		String whereClause = TBL_TASK.COL_ID  + "=" + task.id;
		
		SQLiteDatabase db = getWritableDatabase();
		return db.update(TBL_TASK.TBL_NAME, cv, whereClause, null);
	}
	
	public long insert_task(PinglyTask task) {
		
		Log.d(LOG_TAG, "Inserting Task");
		
	  SQLiteDatabase db = getWritableDatabase();

	  ContentValues values = new ContentValues();
	  populateFromTask(values,task);
	  
	  // TODO: auto-populate
	  values.put(TBL_TASK.COL_CREATE_TIME, 1);

	  return db.insertOrThrow(TBL_TASK.TBL_NAME, null, values);
	  
	}
	
	public PinglyTask findTaskById(long id) {
		
		Log.d(LOG_TAG, "Looking up task " + id);

		SQLiteDatabase db = getReadableDatabase();
		String idClause = TBL_TASK.COL_ID + "=" + id;
		
		Cursor cursor = db.query(TBL_TASK.TBL_NAME, TBL_TASK.FROM_ALL, idClause, null, null, null, null);
		cursor.moveToFirst();
				
		return cursorToTask(cursor, true);
			
	}
	
	public void deleteTask(PinglyTask task) {
		
		Log.d(LOG_TAG, "Deleting task " + task);
		
		SQLiteDatabase db = getReadableDatabase();
		String idClause = TBL_TASK.COL_ID + "=" + task.id; 
		db.delete(TBL_TASK.TBL_NAME, idClause, null);
		
		
	}
	

	public Cursor getAllTasks() {
		
		Log.d(LOG_TAG, "Querying Table");

		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TBL_TASK.TBL_NAME, TBL_TASK.FROM_ALL, null, null, null, null, TBL_TASK.COL_NAME);		
		
		return cursor;
	}
	
	private void populateFromTask(ContentValues cv, PinglyTask task){
		// not ID, not db generated props
		cv.put(TBL_TASK.COL_NAME, task.name);
		cv.put(TBL_TASK.COL_DESC, task.desc);
		cv.put(TBL_TASK.COL_URL, task.url);
	}
	
	// keep the param so the caller doesn't forget about cursor responsibility
	private PinglyTask cursorToTask(Cursor c, boolean closeCursor){
		
		PinglyTask task = new PinglyTask();
		task.id = c.getLong(c.getColumnIndexOrThrow(TBL_TASK.COL_ID));
		task.name = c.getString(c.getColumnIndexOrThrow(TBL_TASK.COL_NAME));
		task.desc = c.getString(c.getColumnIndexOrThrow(TBL_TASK.COL_DESC));
		task.url = c.getString(c.getColumnIndexOrThrow(TBL_TASK.COL_URL));
		
		if(closeCursor){
			c.close();
		}
		
		return task;
	}
	

}
