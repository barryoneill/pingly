package net.nologin.meep.pingly.model;

import android.content.Context;
import net.nologin.meep.pingly.util.PinglyUtils;


public enum DayOfWeek {

    Monday(0),
    Tuesday(1),
    Wednesday(2),
    Thursday(3),
    Friday(4),
    Saturday(5),
    Sunday(6);

    public long id;

    private static String[] STRING_VALUES;

    DayOfWeek(long id){
        this.id = id;
    }

    public String getResourceNameForName(){
        return "day_of_week_" + id + "_name";
    }

    public static String[] toStringValueArray(Context ctx){

        if(STRING_VALUES == null){
            STRING_VALUES = PinglyUtils.enumToStringValuesArray(ctx, DayOfWeek.class,"getResourceNameForName");
        }
        return STRING_VALUES;
    }

    public static DayOfWeek fromId(long id){
        for(DayOfWeek t : DayOfWeek.values()){
            if(id == t.id){
                return t;
            }
        }
        throw new IllegalArgumentException("ID " + id  + " not a valid " + DayOfWeek.class.getSimpleName());
    }

    @Override
    public String toString(){
        return super.toString() + "[" + id + "]";
    }

}
