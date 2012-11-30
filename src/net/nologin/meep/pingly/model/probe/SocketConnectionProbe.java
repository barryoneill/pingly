/*
 *    Pingly - A simple app for checking for signs of life in hosts/services.
 *    Copyright 2012 Barry O'Neill
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package net.nologin.meep.pingly.model.probe;

import android.util.Log;
import net.nologin.meep.pingly.util.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import static net.nologin.meep.pingly.PinglyConstants.LOG_TAG;

/**
 * Probe Type: TCP Connection - make a socket connection.
 * Could be expanded in the future to do some basic conversation stuff..
 */
public class SocketConnectionProbe extends Probe {

	public static final String TYPE_KEY = "SOCK_CONN";

	public static final int PORT_MIN = 1;
	public static final int PORT_MAX = 65535;
	public static final int PORT_DEFAULT = 80;

	/* Example of current JSON format
	 *
	 * {
	 *    "PING": {
	 *       "host":"127.0.0.1",
	 *       "port":5
	 *    }
	 * }
	 *
	 */
	private static final String JSON_ATTR_HOST = "host";
	private static final String JSON_ATTR_PORT = "port";

	public String host;
	public int port;

	@Override
	public String getTypeKey() {
		return TYPE_KEY;
	}
	@Override
	public String configToString() {

		try {

			JSONObject pingAttrs = new JSONObject();
			pingAttrs.put(JSON_ATTR_HOST, host);
			pingAttrs.put(JSON_ATTR_PORT, port);

			JSONObject root = new JSONObject();
			root.put(getTypeKey(),pingAttrs);

			String asJSON = root.toString();
			Log.d(LOG_TAG, "Config of " + this + " = " + asJSON);

			return asJSON;

		}
		catch (JSONException e){
			Log.e(LOG_TAG, "Couldn't convert to JSON - " + e.getMessage(),e);
			return "";
		}

	}

	@Override
	public void configFromString(String config) {

		try {

			if(StringUtils.isBlank(config)){
				Log.d(LOG_TAG, "No config to parse for " + this);
				return;
			}

			Log.d(LOG_TAG, "Parsing config for " + this + " from string " + config);

			JSONObject root = new JSONObject(config);
			if(!root.has(getTypeKey())){
				Log.d(LOG_TAG, "No config for " + getTypeKey() + " in string: " + config);
				return;
			}

			JSONObject pingAttrs = root.getJSONObject(getTypeKey());
			if(pingAttrs.has(JSON_ATTR_HOST)){
				host = pingAttrs.getString(JSON_ATTR_HOST);
			}
			if(pingAttrs.has(JSON_ATTR_PORT)){
				port = pingAttrs.getInt(JSON_ATTR_PORT);
			}

		}
		catch (JSONException e){
			Log.e(LOG_TAG, "Couldn't populate from JSON - " + e.getMessage(),e);
		}

	}

}
