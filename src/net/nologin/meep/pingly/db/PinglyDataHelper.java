package net.nologin.meep.pingly.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

/**
 * TODO: change this code and the DAO code to use an ORM library
 * (whenever I find one that isn't huge and seems usable - OrmLite perhaps?)
 */
public abstract class PinglyDataHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Pingly.db";
    private static final int DATABASE_VERSION = 1;

    public PinglyDataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public static final class TBL_PROBE {
        public static final String TBL_NAME = "pingly_probes";

        public static final String COL_ID = BaseColumns._ID;
        public static final String COL_TYPE_ID = "type_id";
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

        public static final String[] FROM_ALL = { COL_ID, COL_TYPE_ID, COL_NAME, COL_DESC,
                COL_URL, COL_CREATED, COL_LASTMOD };
    }

	public static final class TBL_SCHEDULE {
		public static final String TBL_NAME = "pingly_schedule";

		public static final String COL_ID = BaseColumns._ID;
        public static final String COL_PROBE_ID = "probe_id";
		public static final String COL_ACTIVE = "is_active";
		public static final String COL_CREATED = "t_created";
		public static final String COL_LASTMOD = "t_lastmod";

		// make sure names match above
        public static final String CREATE_SQL = "CREATE TABLE " + TBL_NAME + "( "
				+ COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_PROBE_ID + " INTEGER NOT NULL, "
                + COL_CREATED + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + COL_LASTMOD + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + COL_ACTIVE + " INTEGER NOT NULL, "
                + "FOREIGN KEY(" + COL_PROBE_ID + ") REFERENCES "
                        + TBL_PROBE.TBL_NAME + "(" + TBL_PROBE.COL_ID + ")"
				+ "   )";

		public static final String[] FROM_ALL = { COL_ID, COL_PROBE_ID, COL_ACTIVE,
				                                COL_CREATED, COL_LASTMOD };
	}


    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d(LOG_TAG, "Creating table: " + TBL_PROBE.CREATE_SQL);
        Log.d(LOG_TAG, "Creating table: " + TBL_SCHEDULE.CREATE_SQL);

        db.execSQL(TBL_PROBE.CREATE_SQL);
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
