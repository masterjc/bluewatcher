package com.bluewatcher.config;

import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.bluewatcher.R;
import com.bluewatcher.activity.SettingsActivity;

/**
 * @version $Revision$
 */
public class ConfigurationManager {
	private static ConfigurationManager INSTANCE;
	
	public static void initialize(SharedPreferences sharedPreferences) {
		INSTANCE = new ConfigurationManager(sharedPreferences);
	}
	
	public static ConfigurationManager getInstance() {
		if( INSTANCE == null )
			throw new RuntimeException("Configuration not initialized!");
		return INSTANCE;
	}
	
	public static boolean isBleServerDisabled() {
		String disable = ConfigurationManager.getInstance().load(SettingsActivity.DISABLE_SERVER_SERVICES, Boolean.toString(false));
		return Boolean.parseBoolean(disable);
	}
	
	public static void showConfigurationError(Context context) {
		Toast.makeText(context, context.getString(R.string.error_apply), Toast.LENGTH_LONG).show();
	}
	
	private SharedPreferences sharedPreferences;
	
	private ConfigurationManager(SharedPreferences sharedPreferences) {
		this.sharedPreferences = sharedPreferences;
	}
	
	public void save( Class<?> appClassConfig, String jsonObject ) throws JSONException {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(appClassConfig.getName(), jsonObject);
		editor.commit();
	}
	
	public void save( String key, String value ) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public String load( Class<?> appClassConfig, String defaultJson ) throws JSONException {
		return sharedPreferences.getString(appClassConfig.getName(), defaultJson);
	}
	
	public void clearAll( String prefix ) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		for( String key : sharedPreferences.getAll().keySet()) {
			if( key.startsWith(prefix)) {
				editor.remove(key);
			}
		}
		editor.commit();
	}
	
	public String load( String key, String defaultValue ) {
		return sharedPreferences.getString(key, defaultValue);
	}
}
