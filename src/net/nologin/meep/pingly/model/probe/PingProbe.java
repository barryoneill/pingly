package net.nologin.meep.pingly.model.probe;

public class PingProbe extends Probe {

	public static final String TYPE_KEY = "PING";

	@Override
	public String getTypeKey() {
		return TYPE_KEY;
	}

	@Override
	public String configToString() {
		return ""; // TODO
	}

	@Override
	public void configFromString(String config) {
		// TODO:
	}

}
