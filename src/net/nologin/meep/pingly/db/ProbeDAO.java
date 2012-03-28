package net.nologin.meep.pingly.db;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import net.nologin.meep.pingly.model.Probe;
import net.nologin.meep.pingly.model.ProbeType;

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
        Cursor cursor = db.query(TBL_PROBE.TBL_NAME, null, null,
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
                {"Google", "1", "Do a test run against Google","http://www.google.com"},
                {"Google HTTPS", "2", "Do a test run against Google (https)","https://www.google.com"},
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

            // trigger will update last modified column
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
