package net.nologin.meep.pingly.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public abstract class PinglyDAO {

    /* private to prevent (further) misuse
       We're keeping a single instance of the helper in the application object
       so we don't offer any close() functionality here.
     */
    private PinglyDataHelper dataHelper;

    public PinglyDAO(PinglyDataHelper dataHelper) {
        this.dataHelper = dataHelper;
    }

    protected SQLiteDatabase getReadableDB(){
        return this.dataHelper.getReadableDatabase();
    }

    protected SQLiteDatabase getWriteableDB(){
        return this.dataHelper.getWritableDatabase();
    }

    protected Context getDataHelperContext(){
        return this.dataHelper.getDataHelperContext();
    }


}
