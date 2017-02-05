package com.bluewatcher.activity;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.SeekBar;

import com.bluewatcher.R;
import com.bluewatcher.app.finder.PhoneFinderConfig;
import com.bluewatcher.app.finder.PhoneFinderConfigManager;
import com.bluewatcher.config.ConfigurationManager;

public class PhoneFinderConfigActivity extends Activity {
	private CheckBox phoneFinderEnabled;
	private EditText finderDelay;
	private SeekBar volumeBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_phone_finder_config);

		phoneFinderEnabled = (CheckBox) findViewById(R.id.phone_finder_check);
		finderDelay = (EditText) findViewById(R.id.finder_time_delay);
		volumeBar = (SeekBar) findViewById(R.id.finder_volume_seekbar);
		phoneFinderEnabled.setOnCheckedChangeListener(new EnabledCheckBoxListener());

		try {
			loadConfiguration();
		}
		catch (JSONException e) {
			ConfigurationManager.showConfigurationError(getApplicationContext());
			finish();
		}
	}

	private class EnabledCheckBoxListener implements OnCheckedChangeListener {
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) {
				finderDelay.setEnabled(true);
				volumeBar.setEnabled(true);
			}
			else {
				finderDelay.setEnabled(false);
				volumeBar.setEnabled(false);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.phone_finder_config, menu);
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
			if (!saveConfiguration())
				return;
		}
		catch (JSONException e) {
			ConfigurationManager.showConfigurationError(getApplicationContext());
		}
		setResult(RESULT_OK);
		finish();
	}

	private void loadConfiguration() throws JSONException {
		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		PhoneFinderConfig config = PhoneFinderConfigManager.load(maxVolume / 2);
		phoneFinderEnabled.setChecked(config.isAppEnabled());
		finderDelay.setEnabled(config.isAppEnabled());
		finderDelay.setText(Integer.toString(config.getDelayTime()));
		volumeBar.setMax(maxVolume);
		volumeBar.setEnabled(config.isAppEnabled());
		volumeBar.setProgress(config.getVolume().intValue());
	}

	private boolean saveConfiguration() throws JSONException {
		PhoneFinderConfig config = new PhoneFinderConfig();
		config.setAppEnabled(phoneFinderEnabled.isChecked());
		if (!finderDelay.getText().toString().isEmpty()) {
			config.setDelayTime(Integer.parseInt(finderDelay.getText().toString()));
		}
		else {
			return false;
		}
		config.setVolume(volumeBar.getProgress());
		PhoneFinderConfigManager.save(config);
		return true;
	}
}
