package net.nologin.meep.pingly.db;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import net.nologin.meep.pingly.model.probe.HTTPResponseProbe;
import net.nologin.meep.pingly.model.probe.PingProbe;
import net.nologin.meep.pingly.model.probe.Probe;
import net.nologin.meep.pingly.model.probe.SocketConnectionProbe;

public class ProbeDAO extends PinglyDataHelper {

    public ProbeDAO(Context context) {
        super(context);
    }

    public Probe findProbeById(long id) {

        Log.d(LOG_TAG, "Looking up probe with ID: " + id);

        SQLiteDatabase db = getReadableDatabase();
        String idClause = TBL_PROBE.COL_ID + "=" + id;

        Cursor cursor = db.query(TBL_PROBE.TBL_NAME, null,
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
        Cursor cursor = db.query(TBL_PROBE.TBL_NAME, null,
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
        return db.query(TBL_PROBE.TBL_NAME, null, null,
                null, null, null, TBL_PROBE.COL_CREATED);

    }

	public long getNumProbes(){
		SQLiteDatabase db = getReadableDatabase();
		return DatabaseUtils.queryNumEntries(db, TBL_PROBE.TBL_NAME);
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

		// --------- ping google --------------
		PingProbe p1 = new PingProbe();
		p1.name = "Ping Google";
		p1.desc = "Ping Google, c=5, w=5";
		p1.host = "www.google.com";
		p1.packetCount = 5;
		p1.deadline = 5;
		if(findProbeByName(p1.name) != null){
			p1.name += " [" + Long.toHexString(System.currentTimeMillis()) + "]";
		}
		saveProbe(p1);

		// --------- socket connection to guardian 80 --------------
		SocketConnectionProbe p2 = new SocketConnectionProbe();
		p2.name = "Guardian TCP 80";
		p2.desc = "Make a TCP connection to the guardian port 80";
		p2.host = "guardian.co.uk";
		p2.port = 80;
		if(findProbeByName(p2.name) != null){
			p2.name += " [" + Long.toHexString(System.currentTimeMillis()) + "]";
		}
		saveProbe(p2);

		// --------- HTTP to Microsoft --------------
		HTTPResponseProbe p3 = new HTTPResponseProbe();
		p3.name = "Microsoft HTTP";
		p3.desc = "Make HTTP connection to Microsoft";
		p3.url = "http://www.microsoft.com";
		if(findProbeByName(p3.name) != null){
			p3.name += " [" + Long.toHexString(System.currentTimeMillis()) + "]";
		}
		saveProbe(p3);
	}

    public long saveProbe(Probe probe) {

		Log.d(LOG_TAG, "Saving probe " + probe);

        ContentValues cv = new ContentValues();

        cv.put(TBL_PROBE.COL_TYPE_KEY, probe.getTypeKey());
        cv.put(TBL_PROBE.COL_NAME, probe.name);
        cv.put(TBL_PROBE.COL_DESC, probe.desc);
        cv.put(TBL_PROBE.COL_CONFIG, probe.configToString());

        SQLiteDatabase db = getWritableDatabase();
        if (probe.isNew()) {
            // triggers will fill id, create/modify columns
            return db.insertOrThrow(TBL_PROBE.TBL_NAME, null, cv);
        } else {

            // trigger will update last modified column
            String whereClause = TBL_PROBE.COL_ID + "=" + probe.id;
            db.update(TBL_PROBE.TBL_NAME, cv, whereClause, null);
            return probe.id;
        }
    }

	public static long cursorToProbeId(Cursor c){
		 return c.getInt(c.getColumnIndexOrThrow(TBL_PROBE.COL_ID));
	}

    // keep the param so the caller doesn't forget about cursor responsibility
    public static Probe cursorToProbe(Cursor c, boolean closeCursor) {

		String typeKey = c.getString(c.getColumnIndexOrThrow(TBL_PROBE.COL_TYPE_KEY));
		Probe probe = Probe.getInstance(typeKey);

        probe.id = c.getInt(c.getColumnIndexOrThrow(TBL_PROBE.COL_ID));
        probe.name = c.getString(c.getColumnIndexOrThrow(TBL_PROBE.COL_NAME));
        probe.desc = c.getString(c.getColumnIndexOrThrow(TBL_PROBE.COL_DESC));
        probe.configFromString(c.getString(c.getColumnIndexOrThrow(TBL_PROBE.COL_CONFIG)));

        if (closeCursor) {
            c.close();
        }

        return probe;
    }



}
