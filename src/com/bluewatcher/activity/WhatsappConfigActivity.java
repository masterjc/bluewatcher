package com.bluewatcher.activity;

import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bluewatcher.R;
import com.bluewatcher.app.whatsapp.AfterReadAlgorithm;
import com.bluewatcher.app.whatsapp.DelayedMessageAlgorithm;
import com.bluewatcher.app.whatsapp.NotifyAllAlgorithm;
import com.bluewatcher.app.whatsapp.WhatsappConfig;
import com.bluewatcher.app.whatsapp.WhatsappConfigManager;
import com.bluewatcher.config.ConfigurationManager;

public class WhatsappConfigActivity extends Activity {

	private CheckBox whatsappCheckbox;
	private CheckBox whatsappGroupCheckbox;
	private RadioGroup radioGroup;
	private RadioButton radioDelay;
	private RadioButton radioOnRead;
	private RadioButton radioAll;
	private EditText notificationFilterText;
	private EditText delayTimeText;
	private int checkedAlgorithm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_whatsapp_config);

		whatsappCheckbox = (CheckBox) findViewById(R.id.whatsapp_notification_checkbox);
		whatsappGroupCheckbox = (CheckBox) findViewById(R.id.whatsappGroupCheckbox);
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		radioDelay = (RadioButton) findViewById(R.id.radioDelay);
		radioOnRead = (RadioButton) findViewById(R.id.radio_after_read);
		radioAll = (RadioButton) findViewById(R.id.radio_notify_all);
		notificationFilterText = (EditText) findViewById(R.id.notification_filter);
		delayTimeText = (EditText) findViewById(R.id.delay_time);
		whatsappCheckbox.setOnCheckedChangeListener(new WhatsappCheckBoxListener());
		radioGroup.setOnCheckedChangeListener(new WhatsappAlgorithmListener());

		try {
			loadConfiguration();
		}
		catch (JSONException e) {
			ConfigurationManager.showConfigurationError(getApplicationContext());
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.whatsapp_config, menu);
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
		}
		catch (JSONException e) {
			ConfigurationManager.showConfigurationError(getApplicationContext());
		}
		setResult(RESULT_OK);
		finish();
	}

	private class WhatsappCheckBoxListener implements OnCheckedChangeListener {
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) {
				enableAll();
			}
			else {
				disableAll();
			}
		}
	}

	private void disableAll() {
		radioGroup.setEnabled(false);
		radioDelay.setEnabled(false);
		radioOnRead.setEnabled(false);
		radioAll.setEnabled(false);
		whatsappGroupCheckbox.setEnabled(false);
		notificationFilterText.setEnabled(false);
		delayTimeText.setEnabled(false);
	}

	private void enableAll() {
		radioGroup.setEnabled(true);
		radioDelay.setEnabled(true);
		radioOnRead.setEnabled(true);
		radioAll.setEnabled(true);
		whatsappGroupCheckbox.setEnabled(true);
		notificationFilterText.setEnabled(true);
		delayTimeText.setEnabled(true);
	}

	private class WhatsappAlgorithmListener implements android.widget.RadioGroup.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			if (checkedId == R.id.radioDelay) {
				if (delayTimeText.getText() == null || delayTimeText.getText().length() == 0) {
					radioGroup.check(checkedAlgorithm);
					Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.error_null_whatsapp_delay), Toast.LENGTH_LONG)
							.show();
					return;
				}
				checkedAlgorithm = R.id.radioDelay;
			}
			else if (checkedId == R.id.radio_after_read) {
				checkedAlgorithm = R.id.radio_after_read;
			}
			else if (checkedId == R.id.radio_notify_all) {
				checkedAlgorithm = R.id.radio_notify_all;
			}
		}
	}
	
	private void loadConfiguration() throws JSONException {
		WhatsappConfig config = WhatsappConfigManager.load();
		if (config.isAppEnabled()) {
			enableAll();
		}
		else {
			disableAll();
		}
		if (config.getDelayTime() != null) {
			delayTimeText.setText(config.getDelayTime());
		}
		whatsappCheckbox.setChecked(config.isAppEnabled());
		whatsappGroupCheckbox.setChecked(config.isNotifyGroups());
		if (config.getAlgorithm().equals(NotifyAllAlgorithm.class.getName())) {
			radioGroup.check(R.id.radio_notify_all);
			radioGroup.check(R.id.radio_notify_all);
			radioDelay.setChecked(false);
			radioOnRead.setChecked(false);
			radioAll.setChecked(true);
		}
		else if (config.getAlgorithm().equals(DelayedMessageAlgorithm.class.getName())) {
			checkedAlgorithm = R.id.radioDelay;
			radioGroup.check(R.id.radioDelay);
			radioDelay.setChecked(true);
			radioOnRead.setChecked(false);
			radioAll.setChecked(false);
		}
		else {
			checkedAlgorithm = R.id.radio_after_read;
			radioGroup.check(R.id.radio_after_read);
			radioDelay.setChecked(false);
			radioOnRead.setChecked(true);
			radioAll.setChecked(false);
		}
		notificationFilterText.setText(config.getFilter());
	}

	private void saveConfiguration() throws JSONException {
		WhatsappConfig config = new WhatsappConfig();
		config.setAppEnabled(whatsappCheckbox.isChecked());
		config.setNotifyGroups(whatsappGroupCheckbox.isChecked());
		config.setFilter(notificationFilterText.getText().toString());
		if (delayTimeText != null && delayTimeText.getText() != null && delayTimeText.getText().toString() != null) {
			config.setDelayTime(delayTimeText.getText().toString());
		}
		if (radioAll.isChecked()) {
			config.setAlgorithm(NotifyAllAlgorithm.class.getName());
		}
		else if (radioDelay.isChecked()) {
			config.setAlgorithm(DelayedMessageAlgorithm.class.getName());
		}
		else {
			config.setAlgorithm(AfterReadAlgorithm.class.getName());
		}
		WhatsappConfigManager.save(config);
	}

}
