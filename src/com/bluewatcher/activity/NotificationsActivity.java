package com.bluewatcher.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bluewatcher.R;
import com.bluewatcher.config.ConfigurationOption;
import com.bluewatcher.config.OnConfigurationOptionSelected;

public class NotificationsActivity extends Activity {

	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_notifications);

		initializeNotificationsList();
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
		setResult(RESULT_OK);
		finish();
	}

	private void initializeNotificationsList() {
		listView = (ListView) findViewById(R.id.list_notifications_view);

		ArrayAdapter<ConfigurationOption> adapter = new ArrayAdapter<ConfigurationOption>(this.getBaseContext(), android.R.layout.simple_list_item_1);
		adapter.add(new ConfigurationOption(IncomingCallConfigActivity.class, getString(R.string.incoming_call_config)));
		adapter.add(new ConfigurationOption(WhatsappConfigActivity.class, getString(R.string.whatsapp_config)));
		adapter.add(new ConfigurationOption(GenericNotificationsActivity.class, getString(R.string.custom_notification_config)));
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnConfigurationOptionSelected(this, listView));
	}
}
