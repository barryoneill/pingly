package net.nologin.meep.pingly.model;


import java.util.Date;

public class ScheduleEntry {

    public long id = -1;
    public Probe probe = null;
    public boolean active = true;
    public boolean startOnSave = true;
    public Date startTime = null;
    public ScheduleRepeatType repetition = ScheduleRepeatType.Minutes;


    public ScheduleEntry(){

    }



}
