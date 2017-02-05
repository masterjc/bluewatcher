package com.bluewatcher.app.whatsapp;

import org.json.JSONException;
import org.json.JSONObject;

import com.bluewatcher.config.ConfigurationManager;

/**
 * @version $Revision$
 */
public class WhatsappConfigManager {
	private final static String DEFAULT_FILTER = "Message from"; 
	private final static String FIELD_ACTIVATED = "activated";
	private final static String FIELD_NOTIFY_GROUPS = "notify_groups";
	private final static String FIELD_ALGORITHM = "algorithm";
	private final static String FIELD_FILTER = "filter";
	private final static String FIELD_DELAY_TIME = "delay_time";

	private static final Class<?> CONFIG_CLASS_KEY = WhatsappConfig.class;
	
	public static WhatsappConfig getDefaultConfig() {
		WhatsappConfig config = new WhatsappConfig();
		config.setAppEnabled(true);
		config.setFilter(DEFAULT_FILTER);
		config.setNotifyGroups(true);
		config.setAlgorithm(AfterReadAlgorithm.class.getName());
		config.setDelayTime("5");
		return config;
	}

	public static WhatsappConfig load() throws JSONException {
		String jsonConfig = ConfigurationManager.getInstance().load(CONFIG_CLASS_KEY, serialize(getDefaultConfig()));
		JSONObject jsonObject = new JSONObject(jsonConfig);
		WhatsappConfig config = new WhatsappConfig();
		config.setAppEnabled(jsonObject.getBoolean(FIELD_ACTIVATED));
		config.setAlgorithm(jsonObject.getString(FIELD_ALGORITHM));
		if( jsonObject.has(FIELD_DELAY_TIME)) {
			config.setDelayTime(jsonObject.getString(FIELD_DELAY_TIME));
		}
		config.setFilter(jsonObject.getString(FIELD_FILTER));
		config.setNotifyGroups(jsonObject.getBoolean(FIELD_NOTIFY_GROUPS));
		return config;
	}

	public static void save(WhatsappConfig config) throws JSONException {
		ConfigurationManager.getInstance().save(CONFIG_CLASS_KEY, serialize(config));
	}
	
	private static String serialize(WhatsappConfig config) throws JSONException {
		JSONObject object = new JSONObject();
		object.put(FIELD_ACTIVATED, config.isAppEnabled());
		object.put(FIELD_NOTIFY_GROUPS, config.isNotifyGroups());
		object.put(FIELD_ALGORITHM, config.getAlgorithm());
		object.put(FIELD_FILTER, config.getFilter());
		object.put(FIELD_DELAY_TIME, config.getDelayTime());
		return object.toString(1);
	}
}
