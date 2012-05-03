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

	public String formatForKey(Context ctx){
		return "";
	}

	public String formatForProbe(Context ctx, String probeName) {
		String fmt = ctx.getString(nameFormatterResId);
		return String.format(fmt, probeName);
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