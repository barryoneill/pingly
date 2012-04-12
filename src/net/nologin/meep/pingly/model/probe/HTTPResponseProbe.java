package net.nologin.meep.pingly.model.probe;

public class HTTPResponseProbe extends Probe {

	public static final String TYPE_KEY = "HTTP_RESP";

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
