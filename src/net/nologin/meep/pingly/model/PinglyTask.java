package net.nologin.meep.pingly.model;


public class PinglyTask {

	public long id = -1;
	public String name = "";
	public String desc = "";
	public String url = "";
	
	public PinglyTask(){		
	}
	
	public PinglyTask(long id, String name, String desc, String url) {
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.url = url;
	}
	
	public PinglyTask(String name, String desc, String url) {
		this(-1,name,desc,url);		
	}

	@Override
	public String toString(){		
		return "PinglyTask[id=" + id + ",name='" + name + "']";
	}
	
}
