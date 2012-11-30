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
package net.nologin.meep.pingly.model;

import android.content.Context;
import net.nologin.meep.pingly.R;


public enum ProbeRunStatus {

	Inactive("INACTIVE",R.color.probe_runner_status_inactive, R.string.probe_runner_status_inactive),
	Running("RUNNING",R.color.probe_runner_status_running, R.string.probe_runner_status_running),
	Success("SUCCESS",R.color.probe_runner_status_success, R.string.probe_runner_status_success),
	Failed("FAILED",R.color.probe_runner_status_failed, R.string.probe_runner_status_failed);

	private String key;
	public final int colorResId;
	public final int nameFormatterResId;

	ProbeRunStatus(String key, int colorResId, int nameFormatterResId) {
		this.key = key;
		this.colorResId = colorResId;
		this.nameFormatterResId = nameFormatterResId;
	}

	public String getKeyForDisplay(Context ctx){
		return ctx.getString(nameFormatterResId);
	}

	public String getKey(){
		return key;
	}

	public static ProbeRunStatus fromKey(String key) {
		for (ProbeRunStatus t : ProbeRunStatus.values()) {
			if (t.key.equals(key)) {
				return t;
			}
		}
		throw new IllegalArgumentException("String '" + key + "' not a valid " + ProbeRunStatus.class.getSimpleName());
	}
}