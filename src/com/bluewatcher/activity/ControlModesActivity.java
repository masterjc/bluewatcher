package com.bluewatcher.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bluewatcher.Device;
import com.bluewatcher.R;
import com.bluewatcher.config.DeviceConfiguration;
import com.bluewatcher.control.PhoneControlMode;
import com.bluewatcher.control.PhoneControlModes;
import com.bluewatcher.control.PhoneControlModesManager;

public class ControlModesActivity extends Activity {
	public static final String EDIT_CONTROL_MODE_EXTRA = "com.bluewatcher.EDIT_CONTROL_MODE_EXTRA";
	public static final String EDIT_ORIG_CONTROL_MODE_EXTRA = "com.bluewatcher.EDIT_ORIG_CONTROL_MODE_EXTRA";
	
	private static int EDIT_CONTROL_MODE = 1;
	private static int ADD_CONTROL_MODE = 2;
	
	
	private ListView controlModesList;
	private Button helpButton;
	private TextView notAvailableText;
	private PhoneControlModes currentControlModes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_control_modes);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		controlModesList = (ListView) findViewById(R.id.control_modes_list);
		controlModesList.setOnItemClickListener(new EditControlModeListener());
		helpButton = (Button) findViewById(R.id.control_modes_help);
		notAvailableText = (TextView) findViewById(R.id.phone_control_description);
		
		Device device = DeviceConfiguration.getCurrentDevice();
		if( device == null ) {
			showNoDevice();
			return;
		} 
		showDevice();
		currentControlModes = PhoneControlModesManager.load();
		updateControlModes();
	}
	
	private void updateControlModes() {
		List<String> modeNames = currentControlModes.getControlModesNames();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, modeNames);
		controlModesList.setAdapter(adapter);
	}
	
	private class EditControlModeListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			PhoneControlMode mode = currentControlModes.get(position);
			requestEditMode(mode, position);
		}
	}
	
	private void requestEditMode(PhoneControlMode mode, int modePosition) {
		final Intent editIntent = new Intent(this, ControlModeEditorActivity.class);
		editIntent.putExtra(EDIT_CONTROL_MODE_EXTRA, mode);
		editIntent.putExtra(EDIT_ORIG_CONTROL_MODE_EXTRA, modePosition);
		startActivityForResult(editIntent, EDIT_CONTROL_MODE);
	}
	
	private void requestAddMode() {
		final Intent editIntent = new Intent(this, ControlModeEditorActivity.class);
		startActivityForResult(editIntent, ADD_CONTROL_MODE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Device device = DeviceConfiguration.getCurrentDevice();
		if( device != null ) {
			getMenuInflater().inflate(R.menu.control_modes, menu);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		else if (item.getItemId() == R.id.add_control_mode) {
			requestAddMode();
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		PhoneControlModesManager.save(currentControlModes);
		finish();
	}
	
	private void showNoDevice() {
		controlModesList.setVisibility(View.GONE);
		helpButton.setVisibility(View.GONE);
		notAvailableText.setVisibility(View.VISIBLE);
	}
	
	private void showDevice() {
		controlModesList.setVisibility(View.VISIBLE);
		helpButton.setVisibility(View.VISIBLE);
		notAvailableText.setVisibility(View.GONE);
		helpButton.setOnClickListener(new HelpButtonListener(this));
	}
	
	private class HelpButtonListener implements View.OnClickListener {
		private Activity activity;
		
		public HelpButtonListener(Activity activity) {
			this.activity = activity;
		}
		
		public void onClick(View v) {
			final Intent intent = new Intent(activity, ControlModesHelpActivity.class);
			startActivity(intent);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( requestCode == ADD_CONTROL_MODE ) {
			if( resultCode == RESULT_OK ) {
				PhoneControlMode mode = (PhoneControlMode)data.getSerializableExtra(EDIT_CONTROL_MODE_EXTRA);
				currentControlModes.add(mode);
			}
		} 
		else if (requestCode == EDIT_CONTROL_MODE) {
			PhoneControlMode mode = (PhoneControlMode)data.getSerializableExtra(EDIT_CONTROL_MODE_EXTRA);
			int origModePosition = data.getIntExtra(EDIT_ORIG_CONTROL_MODE_EXTRA, -1);
			if (resultCode == RESULT_CANCELED) {
				currentControlModes.remove(origModePosition);
			}
			else {
				currentControlModes.set(origModePosition, mode);
			}
		}
		updateControlModes();
	}
}
