package net.nologin.meep.pingly.model;


import java.util.Date;

public class ScheduleEntry {

    public long id = -1;
    public long probe;
    public boolean active;
    public boolean startOnSave;
    public Date startTime;
    public ScheduleRepeatType repeatType;
    public int repeatValue;

    // probe for a new entry must exist
    public ScheduleEntry(long probeId){

        this.id = -1;
        this.probe = probeId;
        this.active = true;
        this.startOnSave = true;
        this.startTime = null;
        this.repeatType = ScheduleRepeatType.Minutes;
        this.repeatValue = ScheduleRepeatType.Minutes.defaultValue;

    }

    public boolean isNew(){
        return id <= 0;
    }

    @Override
    public String toString(){
        return "ScheduleEntry[id=" + id + ",probe=" + probe + "]";
    }

}
