package net.nologin.meep.pingly.model;

public class IdValuePair {

    public int id;
    public String value;
    
    public IdValuePair(int id, String value){
        this.id = id;
        this.value = value;
    }

    /* arrayadapter etc will call toString for the display value */
    public String toString(){
        return value;
    }
}
