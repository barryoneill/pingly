package net.nologin.meep.pingly;

public class PinglyConstants {

	public static final String LOG_TAG = "Pingly";

	/**
	 * Format string for converting Date objects to and from Sqlite TIMESTAMP columns, millisecond resolution
	 * http://www.sqlite.org/lang_datefunc.html
	 */
    public static final String SQLITE_FMT_TIMESTAMP = "yyyy-MM-dd HH:mm:ss.SSS";
	// Note - Sqlite CURRENT_TIMESTAMP function does not save millis(.SSS), http://www.sqlite.org/lang_createtable.html


    // TODO: date formats should come from resources!
    public static final String FMT_DAY_DATE_DISPLAY = "EEE, d MMM yyyy";
    public static final String FMT_12HR_MIN_TZ_DISPLAY = "h:mm a z";
	public static final String FMT_24HR_MIN_SEC_TZ_DISPLAY = "HH:mm:ss z";

    public static final String FMT_DATE_AND_TIME_SUMMARY = "EEE, dd MMM yyyy 'at' HH:mm z";
	public static final String FMT_DATE_AND_TIME_SUMMARY_SHORT = "dd MMM yyyy HH:mm z";


}
