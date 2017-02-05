package com.bluewatcher.app.call;

import org.json.JSONException;
import org.json.JSONObject;

import com.bluewatcher.config.ConfigurationManager;

/**
 * @version $Revision$
 */
public class IncomingCallConfigManager {
	private final static String FIELD_ACTIVATED = "activated";
	private final static String FIELD_RESOLVE_NAMES = "resolve_names";

	private static final Class<?> CONFIG_CLASS_KEY = IncomingCallConfig.class;
	
	public static IncomingCallConfig getDefaultConfig() {
		IncomingCallConfig config = new IncomingCallConfig();
		config.setAppEnabled(true);
		config.setResolveContacts(true);
		return config;
	}

	public static IncomingCallConfig load() throws JSONException {
		String jsonConfig = ConfigurationManager.getInstance().load(CONFIG_CLASS_KEY, serialize(getDefaultConfig()));
		JSONObject jsonObject = new JSONObject(jsonConfig);
		IncomingCallConfig config = new IncomingCallConfig();
		config.setAppEnabled(jsonObject.getBoolean(FIELD_ACTIVATED));
		config.setResolveContacts(jsonObject.getBoolean(FIELD_RESOLVE_NAMES));
		return config;
	}

	public static void save(IncomingCallConfig config) throws JSONException {
		ConfigurationManager.getInstance().save(CONFIG_CLASS_KEY, serialize(config));
	}
	
	private static String serialize(IncomingCallConfig config) throws JSONException {
		JSONObject object = new JSONObject();
		object.put(FIELD_ACTIVATED, config.isAppEnabled());
		object.put(FIELD_RESOLVE_NAMES, config.isResolveContacts());
		return object.toString(1);
	}
}
