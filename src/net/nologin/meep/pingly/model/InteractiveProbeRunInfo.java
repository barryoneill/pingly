package net.nologin.meep.pingly.model;


import android.content.Context;
import net.nologin.meep.pingly.R;

public class InteractiveProbeRunInfo {

	public long probeRunId = 0;
	public long probeId = 0;
	public RunStatus status = RunStatus.Inactive;
	public StringBuilder runLog;

	private String newLine = System.getProperty("line.separator");

	public InteractiveProbeRunInfo(long probeRunId, long probeId){
		this.probeRunId = probeRunId;
		this.probeId = probeId;
		status = RunStatus.Inactive;
		runLog = new StringBuilder();
	}

	public void writeLog(String line){
		runLog.append(line);
	}

	public void writeLogLine(String line){
		writeLog(line + newLine);
	}

	public void setFinishedWithSuccess(){
		status = RunStatus.Success;
	}

	public void setFinishedWithFailure(){
		status = RunStatus.Failed;
	}

	public boolean isFinished(){
		return RunStatus.Failed.equals(status) || RunStatus.Success.equals(status);
	}

	// bleh
	public enum RunStatus {

		Inactive(R.color.probe_runner_status_inactive, R.string.probe_runner_status_inactive),
		Running(R.color.probe_runner_status_running, R.string.probe_runner_status_running),
		Success(R.color.probe_runner_status_success, R.string.probe_runner_status_success),
		Failed(R.color.probe_runner_status_failed, R.string.probe_runner_status_failed);

		public final int colorResId;
		public final int nameFormatterResId;

		RunStatus(int colorResId, int nameFormatterResId) {
			this.colorResId = colorResId;
			this.nameFormatterResId = nameFormatterResId;
		}

		public String formatName(Context ctx, String probeName) {
			String fmt = ctx.getString(nameFormatterResId);
			return String.format(fmt, probeName);
		}
	}


}
