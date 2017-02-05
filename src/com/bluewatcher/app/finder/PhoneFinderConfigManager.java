package com.bluewatcher.app.finder;

import org.json.JSONException;
import org.json.JSONObject;

import com.bluewatcher.config.ConfigurationManager;

/**
 * @version $Revision$
 */
public class PhoneFinderConfigManager {
	private final static String FIELD_ACTIVATED = "activated";
	private final static String FIELD_FINDER_DELAY = "finder_delay";
	private final static String FIELD_FINDER_VOLUME = "finder_volume";
	private final static String FIND_ME_FLAG = "FIND_ME_FLAG";
	private final static String ALREADY_FINDING = "ALREADY_FINDING";

	private static final Class<?> CONFIG_CLASS_KEY = PhoneFinderConfig.class;
	
	public static PhoneFinderConfig getDefaultConfig() {
		PhoneFinderConfig config = new PhoneFinderConfig();
		config.setAppEnabled(true);
		config.setDelayTime(15);
		return config;
	}

	public static PhoneFinderConfig load(int defaultVolumeValue) throws JSONException {
		String jsonConfig = ConfigurationManager.getInstance().load(CONFIG_CLASS_KEY, serialize(getDefaultConfig()));
		JSONObject jsonObject = new JSONObject(jsonConfig);
		PhoneFinderConfig config = new PhoneFinderConfig();
		config.setAppEnabled(jsonObject.getBoolean(FIELD_ACTIVATED));
		config.setDelayTime(jsonObject.getInt(FIELD_FINDER_DELAY));
		int volume = 0;
		try {
			volume = jsonObject.getInt(FIELD_FINDER_VOLUME);
		} catch( JSONException e ) {
			volume = defaultVolumeValue;
		}
		config.setVolume(volume);
		return config;
	}

	public static void save(PhoneFinderConfig config) throws JSONException {
		ConfigurationManager.getInstance().save(CONFIG_CLASS_KEY, serialize(config));
	}
	
	private static String serialize(PhoneFinderConfig config) throws JSONException {
		JSONObject object = new JSONObject();
		object.put(FIELD_ACTIVATED, config.isAppEnabled());
		object.put(FIELD_FINDER_DELAY, config.getDelayTime());
		if( config.getVolume() != null ) {
			object.put(FIELD_FINDER_VOLUME, config.getVolume().intValue());
		}
		return object.toString(1);
	}
	
	public static void setFindMeFlag() {
		ConfigurationManager.getInstance().save(FIND_ME_FLAG, Boolean.TRUE.toString());
	}
	
	public static void resetFindMeFlag() {
		ConfigurationManager.getInstance().save(FIND_ME_FLAG, Boolean.FALSE.toString());
	}
	
	public static boolean isFindMe() {
		String findMe = ConfigurationManager.getInstance().load(FIND_ME_FLAG, "false");
		return Boolean.parseBoolean(findMe);
	}
	
	public static void setFindingFlag() {
		ConfigurationManager.getInstance().save(ALREADY_FINDING, Boolean.TRUE.toString());
	}
	
	public static void resetFindingFlag() {
		ConfigurationManager.getInstance().save(ALREADY_FINDING, Boolean.FALSE.toString());
	}
	
	public static boolean isFinding() {
		String findMe = ConfigurationManager.getInstance().load(ALREADY_FINDING, "false");
		return Boolean.parseBoolean(findMe);
	}
}
