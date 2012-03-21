package net.nologin.meep.pingly.model;

import android.content.Context;
import net.nologin.meep.pingly.util.PinglyUtils;

public enum SchedulerRepetitionUnit  {

    Seconds(0),
    Minutes(1),
    Hours(2),
    Days(3),
    Weeks(4),
    Months(5);

    private static String[] SPINNER_RESOURCE_VALUES;

    public int id;

    SchedulerRepetitionUnit(int id){
        this.id = id;
    }

    public String getResourceNameForName(){
        return "scheduler_repetition_unit_" + id + "_name";
    }

    public static SchedulerRepetitionUnit fromId(long id){
        for(SchedulerRepetitionUnit t : SchedulerRepetitionUnit.values()){
            if(id == t.id){
                return t;
            }
        }
        throw new IllegalArgumentException("ID " + id  + " not a valid " + SchedulerRepetitionUnit.class.getSimpleName());
    }

    public static String[] toSpinnerValueArray(Context ctx){

        if(SPINNER_RESOURCE_VALUES == null){
            SPINNER_RESOURCE_VALUES = PinglyUtils.enumToResourceValueArray(SchedulerRepetitionUnit.class,
                                                                           "getResourceNameForName", ctx);
        }
        return SPINNER_RESOURCE_VALUES;
    }


    @Override
    public String toString(){
        return super.toString() + "[" + id + "]";
    }
    
   
}
