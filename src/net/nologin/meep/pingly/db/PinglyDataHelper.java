package net.nologin.meep.pingly.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import net.nologin.meep.pingly.model.Probe;
import net.nologin.meep.pingly.model.ScheduleRepeatType;

import java.util.Date;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

/**
 * TODO: change this code and the DAO code to use an ORM library
 * (whenever I find one that isn't huge and seems usable - OrmLite perhaps?)
 */
public abstract class PinglyDataHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Pingly.db";
    private static final int DATABASE_VERSION = 1;
	private final Context context;

    public PinglyDataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
    }

	protected Context getDataHelperContext(){
		return context;
	}

    public static final class TBL_PROBE {
        public static final String TBL_NAME = "pingly_probes";

        public static final String COL_ID = BaseColumns._ID;
        public static final String COL_TYPE_ID = "probe_type_id";
        public static final String COL_NAME = "probe_name";
        public static final String COL_DESC = "desc";
        public static final String COL_URL = "url";
        public static final String COL_CREATED = "t_created";
        public static final String COL_LASTMOD = "t_lastmod";

        public static final String CREATE_SQL = "CREATE TABLE " + TBL_NAME + "( "
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TYPE_ID + " INTEGER NOT NULL, "
                + COL_CREATED + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + COL_LASTMOD + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + COL_NAME + " TEXT NOT NULL UNIQUE, "
                + COL_DESC + " TEXT, "
                + COL_URL + " TEXT )";

    }

	public static final class TBL_SCHEDULE {
		public static final String TBL_NAME = "pingly_schedule";

		public static final String COL_ID = BaseColumns._ID;
        public static final String COL_PROBE_FK = "probe_fk";
		public static final String COL_ACTIVE = "is_active";
		public static final String COL_CREATED = "t_created";
		public static final String COL_LASTMOD = "t_lastmod";
        public static final String COL_STARTONSAVE = "start_on_save";
        public static final String COL_STARTTIME = "start_time";
        public static final String COL_REPEATTYPE_ID = "repeat_type_id";
        public static final String COL_REPEAT_VALUE = "repeat_value";

        public static final String CREATE_SQL = "CREATE TABLE " + TBL_NAME + "( "
				+ COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_PROBE_FK + " INTEGER NOT NULL, "
                + COL_CREATED + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + COL_LASTMOD + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + COL_ACTIVE + " INTEGER NOT NULL DEFAULT 1, "
                + COL_STARTONSAVE + " INTEGER NOT NULL DEFAULT 1, "
                + COL_STARTTIME + " DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL, "
                + COL_REPEATTYPE_ID + " INTEGER NOT NULL DEFAULT 0,"
                + COL_REPEAT_VALUE + " INTEGER, "
                + "FOREIGN KEY(" + COL_PROBE_FK + ") REFERENCES "
                        + TBL_PROBE.TBL_NAME + "(" + TBL_PROBE.COL_ID + ")"
				+ "   ) ";

	}

    private static String generateLastModTrigger(String tblName, String idName, String lastModName){

        return " CREATE TRIGGER " + tblName + "_LM_TRIG "
                + "AFTER UPDATE ON " + tblName + " FOR EACH ROW BEGIN "
                + "UPDATE " + tblName + " SET " + lastModName + "=CURRENT_TIMESTAMP "
                + "WHERE " + idName + "=OLD." + idName  + "; END";
        
    }
        
    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d(LOG_TAG, "Creating tables/triggers for: " + TBL_PROBE.TBL_NAME);
        db.execSQL(TBL_PROBE.CREATE_SQL);
        db.execSQL(generateLastModTrigger(TBL_PROBE.TBL_NAME,TBL_PROBE.COL_ID, TBL_PROBE.COL_LASTMOD));


        Log.d(LOG_TAG, "Creating tables/triggers for: " + TBL_PROBE.TBL_NAME);
        db.execSQL(TBL_SCHEDULE.CREATE_SQL);
        db.execSQL(generateLastModTrigger(TBL_SCHEDULE.TBL_NAME,TBL_SCHEDULE.COL_ID, TBL_SCHEDULE.COL_LASTMOD));
    }

    // TODO: verify impl!
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d(LOG_TAG, "Upgrading tables");

        db.execSQL("DROP TABLE IF EXISTS " + TBL_PROBE.TBL_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TBL_SCHEDULE.TBL_NAME);
        onCreate(db);
    }

}
