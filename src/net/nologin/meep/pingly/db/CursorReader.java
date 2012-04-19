package net.nologin.meep.pingly.db;

import android.database.Cursor;
import net.nologin.meep.pingly.util.DBUtils;
import net.nologin.meep.pingly.util.StringUtils;

import java.util.Date;

/**
 * convenience class to simply getting data from the cursor in the right data format
 */
public class CursorReader {
    
    Cursor c = null;

    public CursorReader(Cursor c){
        this.c = c;
    }
    
    public String getString(String name){
        return c.getString(c.getColumnIndexOrThrow(name));
    }

    public long getLong(String name){
        return c.getLong(c.getColumnIndexOrThrow(name));
    }

    public int getInt(String name){
        return c.getInt(c.getColumnIndexOrThrow(name));
    }

	public int getInt(String name, int defaultVal){
		try {
			return getInt(name);
		}
		catch (Exception e) {
			return defaultVal;
		}
	}

    public boolean getBoolean(String name){
        return c.getInt(c.getColumnIndexOrThrow(name)) > 0;
    }

    public Date getDate(String name, boolean curDateOnInvalid) {
        String dateStr = getString(name);
        if(StringUtils.isBlank(dateStr)){
            return curDateOnInvalid ? new Date() : null;
        }
        return DBUtils.fromGMTDateTimeString(dateStr);

    }
    
}
