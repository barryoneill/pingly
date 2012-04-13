package net.nologin.meep.pingly.model.probe;

import android.content.Context;
import android.util.Log;
import net.nologin.meep.pingly.util.PinglyUtils;
import net.nologin.meep.pingly.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

public abstract class Probe implements Cloneable {

	public long id = -1;
	public String name = "";
	public String desc = "";

	public Probe() {
		this.id = -1;
		this.name = "";
		this.desc = "";
	}

	public boolean isNew(){
		return id <= 0;
	}

	public String getTypeName(Context ctx){
		return Probe.getTypeName(ctx, getTypeKey());
	}

	public static String getTypeName(Context ctx, String typeKey){
		return PinglyUtils.loadStringForName(ctx,"probe_type_" + typeKey + "_name");
	}

	public String getTypeDesc(Context ctx){
		return Probe.getTypeName(ctx, getTypeKey());
	}

	public static String getTypeDesc(Context ctx, String typeKey){
		return PinglyUtils.loadStringForName(ctx,"probe_type_" + typeKey + "_desc");
	}

	@Override
	public String toString(){		
		return getClass().getSimpleName()
				+ "[typeKey=" + getTypeKey()
				+ ",id=" + id
				+ ",name='" + name
				+ "']";
	}

	public boolean isType(String typeKey){
		return typeKey != null && typeKey.equals(getTypeKey());
	}

	public abstract String getTypeKey();

	public abstract String configToString();

	public abstract void configFromString(String config);

	public static List<String> getProbeTypeKeys() {

		List<String> keys = new ArrayList<String>();
		keys.add(PingProbe.TYPE_KEY);
		keys.add(SocketConnectionProbe.TYPE_KEY);
		keys.add(HTTPResponseProbe.TYPE_KEY);
		return keys;
	}

	// bleh, a factory is overkill
	public static Probe getInstance(String typeKey){

		if(StringUtils.isBlank(typeKey)){
			throw new IllegalArgumentException("Invalid TypeKey");
		}
		if(PingProbe.TYPE_KEY.equals(typeKey)){
			return new PingProbe();
		}
		if(HTTPResponseProbe.TYPE_KEY.equals(typeKey)){
			return new HTTPResponseProbe();
		}
		if(SocketConnectionProbe.TYPE_KEY.equals(typeKey)){
			return new SocketConnectionProbe();
		}

		Log.e(LOG_TAG, "Unrecognized type key " + typeKey + ", returning a HTTP probe as default");
		return new HTTPResponseProbe();

	}

	public static Probe getInstance(String typeKey, Probe copyFrom){

		Probe newProbe = getInstance(typeKey);
		newProbe.id = copyFrom.id;
		newProbe.name = copyFrom.name;
		newProbe.desc = copyFrom.desc;
		newProbe.configFromString(copyFrom.configToString());

		return newProbe;
	}


}
