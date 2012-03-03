package net.nologin.meep.pingly.model;


public class Probe {

	public long id = -1;
	public String name = "";
	public String desc = "";
	public String url = "";
	
	public Probe(){
	}
	
	public Probe(long id, String name, String desc, String url) {
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.url = url;
	}
	
	public Probe(String name, String desc, String url) {
		this(-1,name,desc,url);		
	}

	public boolean isNew(){
		return id <= 0;
	}
	
	@Override
	public String toString(){		
		return "Probe[id=" + id + ",name='" + name + "']";
	}
	
}
