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
import net.nologin.meep.pingly.util.StringUtils;

import java.util.Date;

public class ScheduleEntry {

    public long id = -1;
    public Probe probe;
    public boolean active;
    public boolean startOnSave;
    public Date startTime;
    public ScheduleRepeatType repeatType;
    public int repeatValue;
	public boolean notifyOnSuccess;
	public boolean notifyOnFailure;

    // probe for a new entry must exist
    public ScheduleEntry(Probe probe){

        this.id = -1;
        this.probe = probe;
        this.active = true;
        this.startOnSave = true;
        this.startTime = null;
        this.repeatType = ScheduleRepeatType.Minutes; // TODO: revisit defaults
        this.repeatValue = 10; // TODO: revisit!
		this.notifyOnSuccess = false;
		this.notifyOnFailure = true;

    }

    public boolean isNew(){
        return id <= 0;
    }

	// TODO: doc
	public String getNotifyOptsString(){

		String str = "";
		if(notifyOnSuccess){
			str += "S";
		}
		if(notifyOnFailure){
			str += "F";
		}
		return str;
	}

	public void setNotifyOptsFromString(String str){
		if(StringUtils.isBlank(str)){
			notifyOnFailure = false;
			notifyOnSuccess = false;
			return;
		}
		notifyOnSuccess = str.contains("S");
		notifyOnFailure = str.contains("F");
	}

    @Override
    public String toString(){
        return "ScheduleEntry[id=" + id + ",probe=" + probe + "]";
    }

}
