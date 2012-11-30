/*
 *    Pingly - A simple app for checking for signs of life in hosts/services.
 *    Copyright 2012 Barry O'Neill
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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
    private final static SimpleDateFormat dateTimeGMTFormatter;
    static {
        dateTimeGMTFormatter = new SimpleDateFormat(PinglyConstants.SQLITE_FMT_TIMESTAMP);
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
