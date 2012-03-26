package net.nologin.meep.pingly.util;

import android.util.Log;
import net.nologin.meep.pingly.PinglyConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public final class DBUtils {

    // Since the SDF class is mutable, there's a risk callers could change this
    // object.  However, instantiating the class between possible repeated method
    // calls below is a performance tradeoff
    private static SimpleDateFormat dateTimeGMTFormatter;
    static {
        dateTimeGMTFormatter = new SimpleDateFormat(PinglyConstants.SQLITE_FMT_CURRENT_TIMESTAMP);
        dateTimeGMTFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
    }


    public static Date fromGMTDateTimeString(String dateTime){

        try {
            return dateTimeGMTFormatter.parse(dateTime);
        }
        catch(ParseException e){
            Log.e(PinglyConstants.LOG_TAG,"Invalid dateTime string '" + dateTime + "'");
            throw new IllegalArgumentException("Expected datetime, but got value '" + dateTime + "'");
        }

    }
 
    public static String toGMTDateTimeString(Date d){

        return dateTimeGMTFormatter.format(d);

    }
    
}
