package com.bluewatcher.app.generic;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bluewatcher.AlertType;
import com.bluewatcher.config.ConfigurationManager;

/**
 * @version $Revision$
 */
public class GenericAppConfigManager {
	private final static String FIELD_ALERT_TYPE = "alert_type";
	private final static String FIELD_APP_DISPLAY_NAME = "display_name";
	private final static String FIELD_PACKAGE = "package";
	private final static String FIELD_MESSAGE_FILTERS = "message_filters";
	private final static String FIELD_PREFIX = "prefix";
	private final static String FIELD_LABEL = "label";

	private static final Class<?> CONFIG_CLASS_KEY = GenericAppConfig.class;

	public static List<GenericAppConfig> getDefaultConfig() {
		return new ArrayList<GenericAppConfig>();
	}

	public static List<GenericAppConfig> load() throws JSONException {
		String jsonConfig = ConfigurationManager.getInstance().load(CONFIG_CLASS_KEY, serialize(getDefaultConfig()));
		JSONArray list = new JSONArray(jsonConfig);
		List<GenericAppConfig> apps = new ArrayList<GenericAppConfig>();
		for (int i = 0; i < list.length(); i++) {
			JSONObject object = list.getJSONObject(i);
			String pack = object.getString(FIELD_PACKAGE);
			String displayName = object.getString(FIELD_APP_DISPLAY_NAME);
			if (pack == null || displayName == null)
				continue;
			GenericAppConfig config = new GenericAppConfig(pack, displayName);
			int alertType = object.getInt(FIELD_ALERT_TYPE);
			config.setAlertType(AlertType.fromAlertId(alertType));
			if (object.has(FIELD_PREFIX)) {
				config.setPrefix(object.getString(FIELD_PREFIX));
			}
			if (object.has(FIELD_LABEL)) {
				config.setLabel(object.getString(FIELD_LABEL));
			}
			
			if (object.has(FIELD_MESSAGE_FILTERS)) {
				JSONArray filters = object.getJSONArray(FIELD_MESSAGE_FILTERS);
				List<String> filtersList = new ArrayList<String>();
				for( int f = 0; f < filters.length(); f++ ) {
					filtersList.add(filters.get(f).toString());
				}
				config.setMessageFilters(filtersList);
			}
			apps.add(config);
		}
		return apps;
	}

	public static void save(List<GenericAppConfig> config) throws JSONException {
		ConfigurationManager.getInstance().save(CONFIG_CLASS_KEY, serialize(config));
	}

	private static String serialize(List<GenericAppConfig> configs) throws JSONException {
		JSONArray list = new JSONArray();
		for (GenericAppConfig config : configs) {
			JSONObject object = new JSONObject();
			object.put(FIELD_ALERT_TYPE, config.getAlertType().getAlertId());
			object.put(FIELD_APP_DISPLAY_NAME, config.getAppDisplayName());
			object.put(FIELD_PACKAGE, config.getAppPackage());
			List<String> filters = config.getMessageFilters();
			JSONArray filtersArray = new JSONArray();
			for( String filter : filters ) {
				filtersArray.put(filter);
			}
			object.put(FIELD_MESSAGE_FILTERS, filtersArray);
			object.put(FIELD_PREFIX, config.getPrefix());
			object.put(FIELD_LABEL, config.getLabel());
			list.put(object);
		}
		return list.toString(1);
	}
}
