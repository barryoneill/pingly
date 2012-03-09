package net.nologin.meep.pingly.model;


public class Probe {

	public long id = -1;
	public String name = "";
	public String desc = "";
	public String url = "";
    public ProbeType type;
	
	public Probe(){
	}

	public Probe(long id, String name, String desc, String url, ProbeType type) {
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.url = url;
        this.type = type;
	}

	public Probe(String name, String desc, String url,ProbeType type) {
		this(-1,name,desc,url,type);
	}

	public boolean isNew(){
		return id <= 0;
	}
	
	@Override
	public String toString(){		
		return "Probe[id=" + id + ",type=" + type + ",name='" + name + "']";
	}
	
}
