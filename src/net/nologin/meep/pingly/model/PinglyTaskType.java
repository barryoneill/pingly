package net.nologin.meep.pingly.model;


public enum PinglyTaskType implements Comparable<PinglyTaskType> {

    ServiceResponding(0),
    ResponseCode(1),
    HeaderPresent(2),
    HeaderValue(3),
    ResponseSize(4),
    ResponsebodyContents(5),
    PinglyJSON(6);

    public long id;
    
    
    PinglyTaskType(long id){
        this.id = id;    
    }

    public String getResourceNameForName(){
        return "task_type_" + id + "_name";
    }

    
    public String getResourceNameForDesc(){
        return "task_type_" + id + "_desc";
    }

}
