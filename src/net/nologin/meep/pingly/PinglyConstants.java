package net.nologin.meep.pingly;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class PinglyConstants {

	public static final String LOG_TAG = "Pingly";

    // http://www.sqlite.org/lang_createtable.html
    public static final String SQLITE_FMT_CURRENT_TIMESTAMP = "yyyy-MM-dd HH:mm:ss";
    public static final String SQLITE_FMT_CURRENT_TIME = "HH:mm:ss";
    public static final String SQLITE_FMT_CURRENT_DATE = "yyyy-MM-dd";

    // TODO: date formats should come from resources!
    public static final String FMT_DAY_DATE_DISPLAY = "EEE, d MMM yyyy";
    public static final String FMT_12HR_MIN_TZ_DISPLAY = "h:mm a z";
	public static final String FMT_24HR_MIN_SEC_TZ_DISPLAY = "HH:mm:ss z";

    public static final String FMT_DATE_AND_TIME_SUMMARY = "EEE, dd MMM yyyy 'at' HH:mm z";
	public static final String FMT_DATE_AND_TIME_SUMMARY_SHORT = "dd MMM yyyy HH:mm z";


}
