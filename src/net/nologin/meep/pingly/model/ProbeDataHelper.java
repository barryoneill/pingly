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
public class ProbeDataHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "Pingly.db";
	private static final int DATABASE_VERSION = 1;

	// for saving strings into 'DATETIME' fields, ContentValues lacks support
	private static final DateFormat DATETIME_ISO8601 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static final class TBL_PROBE {
		public static final String TBL_NAME = "pingly_probes";

		public static final String COL_ID = BaseColumns._ID;
        public static final String COL_TYPE_ID = "type_id";
		public static final String COL_NAME = "probe_name";
		public static final String COL_DESC = "desc";
		public static final String COL_URL = "url";
		public static final String COL_CREATED = "t_created";
		public static final String COL_LASTMOD = "t_lastmod";

		// make sure names match above
		public static final String CREATE_SQL = "CREATE TABLE pingly_probes " + "("
				+ "   _id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "   type_id INTEGER NOT NULL,"
				+ "   t_created DATETIME DEFAULT CURRENT_TIMESTAMP,"
				+ "   t_lastmod DATETIME DEFAULT CURRENT_TIMESTAMP,"
				+ "   probe_name TEXT NOT NULL UNIQUE,"
				+ "   desc TEXT,"
				+ "   url URL" + ")";
		
		public static final String[] FROM_ALL = { COL_ID, COL_TYPE_ID, COL_NAME, COL_DESC,
				                                COL_URL, COL_CREATED, COL_LASTMOD };
	}

	public ProbeDataHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		Log.d(LOG_TAG, "Creating table: " + TBL_PROBE.CREATE_SQL);

		db.execSQL(TBL_PROBE.CREATE_SQL);
	}

	// TODO: verify impl!
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		Log.d(LOG_TAG, "Upgrading table");

		db.execSQL("DROP TABLE IF EXISTS " + TBL_PROBE.TBL_NAME);
		onCreate(db);
	}

	public Probe findProbeById(long id) {

		Log.d(LOG_TAG, "Looking up probe with ID: " + id);

		SQLiteDatabase db = getReadableDatabase();
		String idClause = TBL_PROBE.COL_ID + "=" + id;

		Cursor cursor = db.query(TBL_PROBE.TBL_NAME, TBL_PROBE.FROM_ALL,
				idClause, null, null, null, null);
		if(!cursor.moveToFirst()){
			Log.d(LOG_TAG, "No probe found for ID: " + id);
			return null;
		}
		return cursorToProbe(cursor, true);
	}

	public Probe findProbeByName(String name) {

		Log.d(LOG_TAG, "Looking up probe with name: " + name);
		
		SQLiteDatabase db = getReadableDatabase();	
		String nameClause = TBL_PROBE.COL_NAME + "=?";
		Cursor cursor = db.query(TBL_PROBE.TBL_NAME, TBL_PROBE.FROM_ALL,
				nameClause, new String[]{name}, null, null, null);
		
		if(!cursor.moveToFirst()){
			Log.d(LOG_TAG, "No probe found for name: " + name);
			return null;
		}
		return cursorToProbe(cursor, true);

	}

	
	public Cursor findAllProbes() {

		Log.d(LOG_TAG, "Querying Table");

		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TBL_PROBE.TBL_NAME, TBL_PROBE.FROM_ALL, null,
				null, null, null, TBL_PROBE.COL_CREATED);

		return cursor;
	}

	// danger here
	public void deleteAll(){
		
		Log.d(LOG_TAG, "Deleting all probes");
		
		SQLiteDatabase db = getWritableDatabase();		
		db.delete(TBL_PROBE.TBL_NAME, null, null);
	}
	
	public void deleteProbe(Probe probe) {

		Log.d(LOG_TAG, "Deleting probe " + probe);

		SQLiteDatabase db = getWritableDatabase();
		String idClause = TBL_PROBE.COL_ID + "=" + probe.id;
		db.delete(TBL_PROBE.TBL_NAME, idClause, null);

	}

	// dummy some test data
	public void generateTestItems() {
	
		String[][] items = {
				{"Guardian Football Mobile", "0", "Check the mobile version of the Guardian Football site", "http://m.guardian.co.uk/football?cat=football"},
				{"Test Google", "1", "Do a test run against Google","http://www.google.com"},
				{"Test Google HTTPS", "2", "Do a test run against Google (https)","https://www.google.com"},
				{"Microsoft", "3", "Same again, against Microsoft","http://www.microsoft.com"},
				{"Redbrick", "4", "This is a really long string to test that the truncation in the list view is working","http://www.redbrick.dcu.ie"},
				{"Scrabblefinder", "5", "Ding ding ding", "http://www.scrabblefinder.com"}
		};
		
        String name, desc, url;
        ProbeType type;
        
		for(String[] line : items){
			
			name = line[0];
            type = ProbeType.fromId(Long.parseLong(line[1]));
            desc = line[2];
            url = line[3];
            
			if(findProbeByName(name) != null){
				// names have to be unique, add something to further duplicates
				name += " [" + Long.toHexString(System.currentTimeMillis()) + "]";	
			}
			            
            
			saveProbe(new Probe(name,desc,url,type));
		}
		
	}
	
	public long saveProbe(Probe probe) {

		ContentValues cv = new ContentValues();

        cv.put(TBL_PROBE.COL_TYPE_ID, probe.type.id);
		cv.put(TBL_PROBE.COL_NAME, probe.name);
		cv.put(TBL_PROBE.COL_DESC, probe.desc);
		cv.put(TBL_PROBE.COL_URL, probe.url);        

		SQLiteDatabase db = getWritableDatabase();
		if (probe.isNew()) {
			// triggers will fill id, create/modify columns
			return db.insertOrThrow(TBL_PROBE.TBL_NAME, null, cv);
		} else {
			
			// leave ID and create date alone, but update last modified
			cv.put(TBL_PROBE.COL_LASTMOD, DATETIME_ISO8601.format(new Date()));
			
			String whereClause = TBL_PROBE.COL_ID + "=" + probe.id;
			db.update(TBL_PROBE.TBL_NAME, cv, whereClause, null);
			return probe.id;
		}
	}


	// keep the param so the caller doesn't forget about cursor responsibility
	private Probe cursorToProbe(Cursor c, boolean closeCursor) {

		Probe probe = new Probe();
		probe.id = c.getLong(c.getColumnIndexOrThrow(TBL_PROBE.COL_ID));
        long typeId = c.getLong(c.getColumnIndexOrThrow(TBL_PROBE.COL_TYPE_ID));
        probe.type = ProbeType.fromId(typeId);
		probe.name = c.getString(c.getColumnIndexOrThrow(TBL_PROBE.COL_NAME));
		probe.desc = c.getString(c.getColumnIndexOrThrow(TBL_PROBE.COL_DESC));
		probe.url = c.getString(c.getColumnIndexOrThrow(TBL_PROBE.COL_URL));

		if (closeCursor) {
			c.close();
		}

		return probe;
	}

}
