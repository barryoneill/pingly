package net.nologin.meep.pingly.model.probe;

public class SocketConnectionProbe extends Probe {

	public static final String TYPE_KEY = "SOCK_CONN";

	public String host;
	public int port;

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
