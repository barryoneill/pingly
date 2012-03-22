package net.nologin.meep.pingly;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class PinglyConstants {

	public static final String LOG_TAG = "Pingly";

    // for saving strings into 'DATETIME' fields, ContentValues lacks support
    public static final DateFormat DATETIME_ISO8601 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
}
