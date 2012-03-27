package net.nologin.meep.pingly.model;


import java.util.Date;

public class ScheduleEntry {

    public long id = -1;
    public Probe probe;
    public boolean active;
    public boolean startOnSave;
    public Date startTime;
    public ScheduleRepeatType repeatType;
    public int repeatAmount;

    public ScheduleEntry(){

        id = -1;
        probe = null;
        active = true;
        startOnSave = true;
        startTime = null;
        repeatType = ScheduleRepeatType.Minutes;
        repeatAmount = ScheduleRepeatType.Minutes.defaultValue;

    }



}
