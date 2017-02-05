package com.bluewatcher.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.bluewatcher.InstalledAppsListAdapter;
import com.bluewatcher.R;
import com.bluewatcher.app.generic.GenericAppConfig;

public class InstalledAppSelectorActivity extends Activity {

	public static final String INSTALLED_APP_INFO = "com.bluewatcher.INSTALLED_APP_INFO_EXTRA";
	
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_installed_app_selector);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		listView = (ListView) findViewById(R.id.installed_apps_list);
		
		updateApplicationsList(null);
	}
	
	private void updateApplicationsList(String filter) {
		final InstalledAppsListAdapter adapter = new InstalledAppsListAdapter(this.getLayoutInflater(), getPackageManager(), filter);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				GenericAppConfig app = adapter.getApp(position);
				returnSelectedApp(app);
			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.installed_app_selector, menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		} else
		if( item.getItemId() == R.id.find_app) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle(this.getText(R.string.find_app_title));
			final EditText input = new EditText(this);
			alert.setView(input);
			alert.setPositiveButton(this.getText(R.string.ok_text), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String filter = input.getText().toString();
					updateApplicationsList(filter);
				}
			});
			alert.setNegativeButton(this.getText(R.string.cancel_text), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			alert.show();
		}
		return super.onOptionsItemSelected(item);
	}

	private void returnSelectedApp(GenericAppConfig app) {
		Intent resultIntent = new Intent();
		resultIntent.putExtra(INSTALLED_APP_INFO, app);
		setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}

}
