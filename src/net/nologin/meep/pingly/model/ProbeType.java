package net.nologin.meep.pingly.model;


public enum ProbeType implements Comparable<ProbeType> {

    ServiceResponding(0),
    ResponseCode(1),
    HeaderPresent(2),
    HeaderValue(3),
    ResponseSize(4),
    ResponsebodyContents(5),
    PinglyJSON(6);

    public long id;
    
    
    ProbeType(long id){
        this.id = id;    
    }

    
    public String getResourceNameForName(){
        return "probe_type_" + id + "_name";
    }

    
    public String getResourceNameForDesc(){
        return "probe_type_" + id + "_desc";
    }


    public static ProbeType fromId(long id){
        for(ProbeType t : ProbeType.values()){
            if(id == t.id){
                return t;
            }
        }
        throw new IllegalArgumentException("ID " + id  + " not a valid " + ProbeType.class.getSimpleName());
    }

    @Override
    public String toString(){
        return super.toString() + "[" + id + "]";
    }
}
