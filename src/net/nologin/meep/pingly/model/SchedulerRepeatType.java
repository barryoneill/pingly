package net.nologin.meep.pingly.model;

import android.content.Context;
import net.nologin.meep.pingly.util.PinglyUtils;

public enum SchedulerRepeatType {

    // Name(id,maxvalue)
    OnceOff(0,1),
    Seconds(1,59),
    Minutes(2,59),
    Hours(3,23),
    Days(4,31),
    Weeks(5,52),
    Months(6,12);

    private static IdValuePair[] ADAPTER_VALUES;

    public int id;
    public int rangeUpperLimit;
    
    SchedulerRepeatType(int id, int rangeUpperLimit){
        this.id = id;
        this.rangeUpperLimit = rangeUpperLimit;
    }

    public int getId(){
        return id;
    }
    
    public String getResourceNameForName(){
        return "scheduler_repetition_unit_" + id + "_name";
    }

    public String getResourceNameForSummary(){
        return "scheduler_repetition_unit_" + id + "_summary";
    }

    public static SchedulerRepeatType fromId(long id){
        for(SchedulerRepeatType t : SchedulerRepeatType.values()){
            if(id == t.id){
                return t;
            }
        }
        throw new IllegalArgumentException("ID " + id  + " not a valid " + SchedulerRepeatType.class.getSimpleName());
    }

    public static IdValuePair[] toAdapterValueArray(Context ctx){

        if(ADAPTER_VALUES == null){
            ADAPTER_VALUES = PinglyUtils.enumToAdapterValuesArray(ctx, SchedulerRepeatType.class,
                                                                "getId","getResourceNameForName");
        }
        return ADAPTER_VALUES;
    }


    @Override
    public String toString(){
        return super.toString() + "[" + id + "]";
    }
    
   
}
