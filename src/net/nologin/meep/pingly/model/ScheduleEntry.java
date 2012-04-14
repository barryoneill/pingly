package net.nologin.meep.pingly.model;


import net.nologin.meep.pingly.model.probe.Probe;

import java.util.Date;

public class ScheduleEntry {

    public long id = -1;
    public Probe probe;
    public boolean active;
    public boolean startOnSave;
    public Date startTime;
    public ScheduleRepeatType repeatType;
    public int repeatValue;

    // probe for a new entry must exist
    public ScheduleEntry(Probe probe){

        this.id = -1;
        this.probe = probe;
        this.active = true;
        this.startOnSave = true;
        this.startTime = null;
        this.repeatType = ScheduleRepeatType.Minutes; // TODO: revisit defaults
        this.repeatValue = 10; // TODO: revisit!

    }

    public boolean isNew(){
        return id <= 0;
    }

    @Override
    public String toString(){
        return "ScheduleEntry[id=" + id + ",probe=" + probe + "]";
    }

}
