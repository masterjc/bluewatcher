package com.bluewatcher.activity;

import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;

import com.bluewatcher.R;
import com.bluewatcher.config.ConfigurationManager;

public class TimeConfigActivity extends Activity {
	public final static String DATETIME_SYNC_CONFIG = "date_time_sync";
	
	private CheckBox syncDateTime;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_time_config);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		syncDateTime = (CheckBox) findViewById(R.id.date_time_sync);
		
		try {
			loadConfiguration();
		} catch(JSONException e) {
			ConfigurationManager.showConfigurationError(getApplicationContext());
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.time_config, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		try {
			saveConfiguration();
		} catch( JSONException e ) {
			ConfigurationManager.showConfigurationError(getApplicationContext());
		}
		finish();
	}
	
	private void loadConfiguration() throws JSONException {
		String dateTime = ConfigurationManager.getInstance().load(DATETIME_SYNC_CONFIG, Boolean.toString(true));
		syncDateTime.setChecked(Boolean.parseBoolean(dateTime));
	}
	
	private void saveConfiguration() throws JSONException {
		ConfigurationManager.getInstance().save(DATETIME_SYNC_CONFIG, Boolean.toString(syncDateTime.isChecked()));
	}
}
