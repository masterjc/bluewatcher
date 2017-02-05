package com.bluewatcher.activity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bluewatcher.R;
import com.bluewatcher.app.generic.GenericAppConfig;
import com.bluewatcher.app.generic.GenericAppConfigManager;
import com.bluewatcher.config.ConfigurationManager;

public class GenericNotificationsActivity extends Activity {

	static final int PICK_INSTALLED_APP_REQUEST = 1;
	static final int PICK_EDIT_APP_REQUEST = 2;

	private ListView listView;
	private List<GenericAppConfig> genericAppConfigs;
	private List<String> orderedAppNames;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generic_notifications);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		listView = (ListView) findViewById(R.id.generic_apps_list);
		listView.setOnItemClickListener(new EditCustomApp());
		try {
			genericAppConfigs = GenericAppConfigManager.load();
		}
		catch (JSONException e) {
			ConfigurationManager.showConfigurationError(getApplicationContext());
			finish();
			return;
		}
		updateGenericApps();
	}

	private void updateGenericApps() {
		orderedAppNames = new ArrayList<String>();
		for (GenericAppConfig app : genericAppConfigs) {
			orderedAppNames.add(app.getAppDisplayName());
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, orderedAppNames);
		adapter.sort(new Comparator<String>() {
			@Override
			public int compare(String lhs, String rhs) {
				return lhs.compareTo(rhs);
			}
		});
		listView.setAdapter(adapter);
	}

	private void requestInstalledApp() {
		final Intent addIntent = new Intent(this, InstalledAppSelectorActivity.class);
		startActivityForResult(addIntent, PICK_INSTALLED_APP_REQUEST);
	}

	private void requestEditApp(GenericAppConfig app) {
		final Intent editIntent = new Intent(this, GenericAppEditorActivity.class);
		editIntent.putExtra(GenericAppEditorActivity.EDIT_APP_INFO, app);
		startActivityForResult(editIntent, PICK_EDIT_APP_REQUEST);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.generic_notifications, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PICK_INSTALLED_APP_REQUEST) {
			if (resultCode != RESULT_OK)
				return;
			GenericAppConfig app = data.getParcelableExtra(InstalledAppSelectorActivity.INSTALLED_APP_INFO);
			requestEditApp(app);
		}
		else if (requestCode == PICK_EDIT_APP_REQUEST) {
			GenericAppConfig app = data.getParcelableExtra(GenericAppEditorActivity.RESULT_APP_INFO);
			if (resultCode == RESULT_CANCELED) {
				for (int i = 0; i < genericAppConfigs.size(); i++) {
					GenericAppConfig compareApp = genericAppConfigs.get(i);
					if (compareApp.getAppPackage().equals(app.getAppPackage())) {
						genericAppConfigs.remove(i);
					}
				}
			}
			else {
				boolean found = false;
				for (int i = 0; i < genericAppConfigs.size(); i++) {
					GenericAppConfig compareApp = genericAppConfigs.get(i);
					if (compareApp.getAppPackage().equals(app.getAppPackage())) {
						genericAppConfigs.set(i, app);
						found = true;
					}
				}
				if (!found) {
					genericAppConfigs.add(app);
				}
			}
			updateGenericApps();
		}
	}

	private class EditCustomApp implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			GenericAppConfig appConfig = getGenericAppConfigByDisplayName(orderedAppNames.get(position));
			requestEditApp(appConfig);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		else if (item.getItemId() == R.id.add_generic_app) {
			requestInstalledApp();
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		try {
			GenericAppConfigManager.save(genericAppConfigs);
		}
		catch (JSONException e) {
			ConfigurationManager.showConfigurationError(getApplicationContext());
		}
		finish();
	}
	
	private GenericAppConfig getGenericAppConfigByDisplayName(String displayName) {
		for( GenericAppConfig app : genericAppConfigs ) {
			if(app.getAppDisplayName().equals(displayName))
				return app; 
		}
		return null;
	}

}
