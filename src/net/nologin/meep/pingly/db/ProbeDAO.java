package net.nologin.meep.pingly.db;

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

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

public class ProbeDAO extends PinglyDataHelper {

    public ProbeDAO(Context context) {
        super(context);
    }

	// convenience method
	public static Probe findProbeById(Context ctx, long id) {

		ProbeDAO dao = new ProbeDAO(ctx);
		Probe result = dao.findProbeById(id);
		dao.close();
		return result;
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

    public void deleteProbe(Probe probe) {

        Log.d(LOG_TAG, "Deleting probe " + probe);

        SQLiteDatabase db = getWritableDatabase();
        String idClause = TBL_PROBE.COL_ID + "=" + probe.id;
        db.delete(TBL_PROBE.TBL_NAME, idClause, null);

    }

	/**
	 * Generates some test probes.
	 */
    public void generateFirstRunItems() {

		// --------- HTTP to Microsoft --------------
		HTTPResponseProbe sampleHTTP = new HTTPResponseProbe();
		sampleHTTP.name = "Example: Microsoft HTTP Test";
		sampleHTTP.desc = "Attempt a HTTP connection to Microsoft";
		sampleHTTP.url = "http://www.microsoft.com";
		if(findProbeByName(sampleHTTP.name) != null){
			sampleHTTP.name += " [" + Long.toHexString(System.currentTimeMillis()) + "]";
		}
		saveProbe(sampleHTTP);

		// --------- ping google --------------
		PingProbe samplePing = new PingProbe();
		samplePing.name = "Example: Ping Localhost";
		samplePing.desc = "Ping localhost with 5 packets & 5 sec deadline ";
		samplePing.host = "localhost";
		samplePing.packetCount = 5;
		samplePing.deadline = 5;
		if(findProbeByName(samplePing.name) != null){
			samplePing.name += " [" + Long.toHexString(System.currentTimeMillis()) + "]";
		}
		saveProbe(samplePing);

		// --------- socket connection to guardian 80 --------------
		SocketConnectionProbe sampleTCP = new SocketConnectionProbe();
		sampleTCP.name = "Example: Google TCP 443";
		sampleTCP.desc = "Connect to www.google.com port 443";
		sampleTCP.host = "www.google.com";
		sampleTCP.port = 443;
		if(findProbeByName(sampleTCP.name) != null){
			sampleTCP.name += " [" + Long.toHexString(System.currentTimeMillis()) + "]";
		}
		saveProbe(sampleTCP);


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
