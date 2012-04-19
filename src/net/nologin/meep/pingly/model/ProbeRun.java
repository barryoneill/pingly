package net.nologin.meep.pingly.model;


import net.nologin.meep.pingly.model.probe.Probe;

import java.util.Date;

public class ProbeRun {

	public long id;
	public Probe probe;
	public ScheduleEntry scheduleEntry;
	public Date startTime;
	public Date endTime;
	public ProbeRunStatus status;
	public String runSummary;
	public String logText;

	public ProbeRun(Probe probe){
		this(probe,null);
	}

	public ProbeRun(Probe probe, ScheduleEntry entry){
		this.id = -1;
		this.probe = probe;
		this.scheduleEntry = entry;
		startTime = new Date();
		endTime = null;
		status = ProbeRunStatus.Inactive;
		runSummary = "";
		logText = "";
	}

	public boolean isNew(){
		return id <= 0;
	}

	public void appendLogLine(String line) {
		this.logText += line + "\n";
	}

	public void setFinishedWithFailure(){
		status = ProbeRunStatus.Failed;
	}

	public void setFinishedWithSuccess(){
		status = ProbeRunStatus.Success;
	}

	public boolean isFinished(){
		return ProbeRunStatus.Failed.equals(status)
				|| ProbeRunStatus.Success.equals(status);
	}

	public String toString(){
		return "ProbeRun[id=" + id +
				", probe="
				+ (probe == null ? "null" : probe.id) +
				", schedule="
				+ (scheduleEntry == null ? "null" : scheduleEntry.id)
				+ "]";
	}

}
