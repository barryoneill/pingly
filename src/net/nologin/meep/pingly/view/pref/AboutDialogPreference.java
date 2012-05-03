package net.nologin.meep.pingly.view.pref;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.util.PinglyUtils;

import java.util.Calendar;

/**
 * This is a solution to the problem where using android:dialogLayout in a DialogPreference causes
 * an InflateException (Binary XML file line #28: Error inflating class java.lang.reflect.Constructor)
 *
 * Simply extending DialogPreference and using
 * 'net.nologin.meep.pingly.activity.SettingsActivity.AboutDialogPref' as the element works
 *
 * See - http://stackoverflow.com/a/6406802
 *
 * The implementation also lets us cleanly update certain info dynamically on dialog display
 */
public class AboutDialogPreference extends DialogPreference {
	public AboutDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);

		Context ctx = getContext();

		TextView txtVerYear = (TextView)view.findViewById(R.id.about_version);

		// update the 'Version 1.0 (c) 2012' message with current data
		String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
		String verName = PinglyUtils.getPinglyVersionName(ctx);
		String verYearFmt = ctx.getString(R.string.pingly_about_version_fmt);
		txtVerYear.setText(String.format(verYearFmt, verName, year));



	}
}