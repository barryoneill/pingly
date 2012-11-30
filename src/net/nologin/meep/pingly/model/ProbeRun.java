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


import net.nologin.meep.pingly.model.probe.Probe;

import java.util.Date;

/**
 * Represents a single run of a probe, and records the output and outcome status of that run
 */
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
