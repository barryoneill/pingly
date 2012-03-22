package net.nologin.meep.pingly.model;

import android.content.Context;
import net.nologin.meep.pingly.util.PinglyUtils;

public enum SchedulerRepetitionUnit  {

    // Name(id,maxvalue)
    Seconds(0,59),
    Minutes(1,59),
    Hours(2,23),
    Days(3,31),
    Weeks(4,52),
    Months(5,12);

    private static IdValuePair[] ADAPTER_VALUES;

    public int id;
    public int rangeUpperLimit;
    
    SchedulerRepetitionUnit(int id, int rangeUpperLimit){
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

    public static SchedulerRepetitionUnit fromId(long id){
        for(SchedulerRepetitionUnit t : SchedulerRepetitionUnit.values()){
            if(id == t.id){
                return t;
            }
        }
        throw new IllegalArgumentException("ID " + id  + " not a valid " + SchedulerRepetitionUnit.class.getSimpleName());
    }

    public static IdValuePair[] toAdapterValueArray(Context ctx){

        if(ADAPTER_VALUES == null){
            ADAPTER_VALUES = PinglyUtils.enumToAdapterValuesArray(ctx, SchedulerRepetitionUnit.class,
                                                                "getId","getResourceNameForName");
        }
        return ADAPTER_VALUES;
    }


    @Override
    public String toString(){
        return super.toString() + "[" + id + "]";
    }
    
   
}
