package net.nologin.meep.pingly.model;


// coming to crash a device near you soon
public class ScheduleEntry {

    public ScheduleEntry(){
        // needed by ormlite
    }

    public long id = -1;

    public long probeId = -1;

    public boolean active = false;

}
