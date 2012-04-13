package net.nologin.meep.pingly.model.probe;

import android.util.Log;
import net.nologin.meep.pingly.PinglyConstants;
import net.nologin.meep.pingly.util.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class PingProbe extends Probe {

	public static final String TYPE_KEY = "PING";

	private static final String JSON_ATTR_HOST = "host";
	private static final String JSON_ATTR_PACKET_COUNT = "packetCount";
	private static final String JSON_ATTR_DEADLINE = "deadline";

	public String host;
	public int packetCount;
	public int deadline;

	@Override
	public String getTypeKey() {
		return TYPE_KEY;
	}

	@Override
	public String configToString() {

		try {

			JSONObject pingAttrs = new JSONObject();
			pingAttrs.put(JSON_ATTR_HOST, host);
			pingAttrs.put(JSON_ATTR_PACKET_COUNT, packetCount);
			pingAttrs.put(JSON_ATTR_DEADLINE, deadline);

			JSONObject root = new JSONObject();
			root.put(getTypeKey(),pingAttrs);

			String asJSON = root.toString();
			Log.d(PinglyConstants.LOG_TAG, "Config of " + this + " = " + asJSON);

			return asJSON;

		}
		catch (JSONException e){
			Log.e(PinglyConstants.LOG_TAG, "Couldn't convert to JSON - " + e.getMessage(),e);
			return "";
		}

	}

	@Override
	public void configFromString(String config) {

		try {

			if(StringUtils.isBlank(config)){
				Log.d(PinglyConstants.LOG_TAG, "No config to parse for " + this);
				return;
			}

			Log.d(PinglyConstants.LOG_TAG, "Parsing config for " + this + " from string " + config);

			JSONObject root = new JSONObject(config);
			if(!root.has(getTypeKey())){
				Log.d(PinglyConstants.LOG_TAG, "No config for " + getTypeKey() + " in string: " + config);
				return;
			}

			JSONObject pingAttrs = root.getJSONObject(getTypeKey());
			if(pingAttrs.has(JSON_ATTR_HOST)){
				host = pingAttrs.getString(JSON_ATTR_HOST);
			}
			if(pingAttrs.has(JSON_ATTR_PACKET_COUNT)){
				packetCount = pingAttrs.getInt(JSON_ATTR_PACKET_COUNT);
			}
			if(pingAttrs.has(JSON_ATTR_DEADLINE)){
				deadline = pingAttrs.getInt(JSON_ATTR_DEADLINE);
			}

		}
		catch (JSONException e){
			Log.e(PinglyConstants.LOG_TAG, "Couldn't populate from JSON - " + e.getMessage(),e);
		}

	}

}
