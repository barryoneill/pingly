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
