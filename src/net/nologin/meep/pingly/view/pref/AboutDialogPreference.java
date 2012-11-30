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
package net.nologin.meep.pingly.view.pref;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import net.nologin.meep.pingly.R;
import net.nologin.meep.pingly.util.PinglyUtils;


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

		TextView txtVersion = (TextView)view.findViewById(R.id.about_version);

		// update the 'Version 1.0 (c) 2012' message with current data
		String verName = PinglyUtils.getPinglyVersionName(ctx);
		txtVersion.setText(ctx.getString(R.string.pingly_about_version_fmt,verName));


	}
}