package com.bluewatcher.activity;

import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.bluewatcher.R;
import com.bluewatcher.app.call.IncomingCallConfig;
import com.bluewatcher.app.call.IncomingCallConfigManager;
import com.bluewatcher.config.ConfigurationManager;

public class IncomingCallConfigActivity extends Activity {
	
	private CheckBox incomingCallCheckbox;
	private CheckBox callResolveContact;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_incoming_call_config);
		
		incomingCallCheckbox = (CheckBox) findViewById(R.id.incoming_call_check);
		callResolveContact = (CheckBox) findViewById(R.id.resolve_contacts_calls);
		incomingCallCheckbox.setOnCheckedChangeListener(new IncomingCallCheckBoxListener());
		
		try {
			loadConfiguration();
		} catch(JSONException e) {
			ConfigurationManager.showConfigurationError(getApplicationContext());
			finish();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.incoming_call_config, menu);
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
		setResult(RESULT_OK);
		finish();
	}

	private class IncomingCallCheckBoxListener implements OnCheckedChangeListener {
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) {
				callResolveContact.setEnabled(true);
			}
			else {
				callResolveContact.setEnabled(false);
			}
		}
	}
	
	private void loadConfiguration() throws JSONException {
		IncomingCallConfig config = IncomingCallConfigManager.load();
		incomingCallCheckbox.setChecked(config.isAppEnabled());
		callResolveContact.setChecked(config.isResolveContacts());
		if( !config.isAppEnabled() ) {
			callResolveContact.setEnabled(false);
		}
	}
	
	private void saveConfiguration() throws JSONException {
		IncomingCallConfig config = new IncomingCallConfig();
		config.setAppEnabled(incomingCallCheckbox.isChecked());
		config.setResolveContacts(callResolveContact.isChecked());
		IncomingCallConfigManager.save(config);
	}

}
