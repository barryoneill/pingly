package net.nologin.meep.pingly.model;


public enum ScheduleRepeatType {

    // Name(id,maxvalue)
    OnceOff(0,1,1),
    Seconds(1,59,30),
    Minutes(2,59,30),
    Hours(3,23,1),
    Days(4,31,1),
    Weeks(5,52,1),
    Months(6,12,1);

    public int id;
    public int rangeLowerLimit;
    public int rangeUpperLimit;
    public int defaultValue;
    
    ScheduleRepeatType(int id, int rangeUpperLimit, int defaultValue){
        this(id,1,rangeUpperLimit,defaultValue);
    }

    ScheduleRepeatType(int id, int rangeLowerLimit, int rangeUpperLimit, int defaultValue){
        this.id = id;
        this.rangeLowerLimit = rangeLowerLimit;
        this.rangeUpperLimit = rangeUpperLimit;
        this.defaultValue = defaultValue;
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

    public static ScheduleRepeatType fromId(long id){
        for(ScheduleRepeatType t : ScheduleRepeatType.values()){
            if(id == t.id){
                return t;
            }
        }
        throw new IllegalArgumentException("ID " + id  + " not a valid " + ScheduleRepeatType.class.getSimpleName());
    }


    @Override
    public String toString(){
        return super.toString() + "[" + id + "]";
    }
    
   
}
