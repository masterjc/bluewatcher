package com.bluewatcher.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.bluewatcher.Device;
import com.bluewatcher.R;
import com.bluewatcher.config.DeviceConfiguration;
import com.bluewatcher.control.Command;
import com.bluewatcher.control.PhoneControlMode;
import com.bluewatcher.control.PhoneControlModesManager;

public class ControlModeEditorActivity extends Activity {

	private EditText modeName;
	private Button okButton;
	private Button cancelButton;

	private TextView button1;
	private TextView button2;
	private TextView button3;
	private TextView button4;
	private TextView button5;

	private Spinner command1;
	private Spinner command2;
	private Spinner command3;
	private Spinner command4;
	private Spinner command5;

	private Device currentDevice;

	private int origModePosition;
	private PhoneControlMode currentMode;

	private List<String> callableCommandNames;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_control_mode_editor);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		modeName = (EditText) findViewById(R.id.control_name_edit);
		okButton = (Button) findViewById(R.id.edit_ok_button);
		cancelButton = (Button) findViewById(R.id.edit_cancel);
		okButton.setOnClickListener(new OkButtonListener());
		cancelButton.setOnClickListener(new CancelButtonListener());

		button1 = (TextView) findViewById(R.id.button1_desc);
		button2 = (TextView) findViewById(R.id.button2_desc);
		button3 = (TextView) findViewById(R.id.button3_desc);
		button4 = (TextView) findViewById(R.id.button4_desc);
		button5 = (TextView) findViewById(R.id.button5_desc);

		command1 = (Spinner) findViewById(R.id.button1_spinner);
		command2 = (Spinner) findViewById(R.id.button2_spinner);
		command3 = (Spinner) findViewById(R.id.button3_spinner);
		command4 = (Spinner) findViewById(R.id.button4_spinner);
		command5 = (Spinner) findViewById(R.id.button5_spinner);

		currentDevice = DeviceConfiguration.getCurrentDevice();

		currentMode = (PhoneControlMode) getIntent().getSerializableExtra(ControlModesActivity.EDIT_CONTROL_MODE_EXTRA);
		origModePosition = getIntent().getIntExtra(ControlModesActivity.EDIT_ORIG_CONTROL_MODE_EXTRA, -1);
		if (currentMode == null) {
			currentMode = PhoneControlModesManager.getDefaultPhoneControlModes(currentDevice).get(0);
			currentMode.setName(null);
		}

		callableCommandNames = PhoneControlModesManager.getCallableCommandNames();

		loadControlMode();
	}

	private class OkButtonListener implements View.OnClickListener {
		public void onClick(View v) {
			onBackPressed();
		}
	}

	private class CancelButtonListener implements View.OnClickListener {
		public void onClick(View v) {
			prepareCancel();
		}
	}

	private void loadControlMode() {
		if (currentMode.getName() != null) {
			modeName.setText(currentMode.getName());
		}

		if (currentDevice.isGB5600()) {
			showGB5600ButtonDescriptions();
			loadCommandSpinner(command1, currentMode.getCommand(com.bluewatcher.control.Button.BUTTON_A));
			loadCommandSpinner(command2, currentMode.getCommand(com.bluewatcher.control.Button.BUTTON_B));
		}
		else if (currentDevice.isSTB1000()) {
			showSTB1000ButtonDescriptions();
			loadCommandSpinner(command1, currentMode.getCommand(com.bluewatcher.control.Button.BUTTON_A));
			loadCommandSpinner(command2, currentMode.getCommand(com.bluewatcher.control.Button.BUTTON_B));
			loadCommandSpinner(command3, currentMode.getCommand(com.bluewatcher.control.Button.BUTTON_C));
			loadCommandSpinner(command4, currentMode.getCommand(com.bluewatcher.control.Button.BUTTON_D));
			loadCommandSpinner(command5, currentMode.getCommand(com.bluewatcher.control.Button.BUTTON_E));
		}
		else if (currentDevice.isGBA400()) {
			showGBA400ButtonDescriptions();
			loadCommandSpinner(command1, currentMode.getCommand(com.bluewatcher.control.Button.BUTTON_A));
			loadCommandSpinner(command2, currentMode.getCommand(com.bluewatcher.control.Button.BUTTON_B));
			loadCommandSpinner(command3, currentMode.getCommand(com.bluewatcher.control.Button.BUTTON_C));
			loadCommandSpinner(command4, currentMode.getCommand(com.bluewatcher.control.Button.WHEEL_UP));
			loadCommandSpinner(command5, currentMode.getCommand(com.bluewatcher.control.Button.WHEEL_DOWN));
		}
		else {
			showGB6900ButtonDescriptions();
			loadCommandSpinner(command1, currentMode.getCommand(com.bluewatcher.control.Button.BUTTON_A));
			loadCommandSpinner(command2, currentMode.getCommand(com.bluewatcher.control.Button.BUTTON_B));
			loadCommandSpinner(command3, currentMode.getCommand(com.bluewatcher.control.Button.BUTTON_C));
		}
	}

	private void loadCommandSpinner(Spinner spinner, Command command) {
		AlertTypeItemSelectedListener listener = new AlertTypeItemSelectedListener(command);
		spinner.setOnItemSelectedListener(listener);
		List<String> list = new ArrayList<String>();
		int defaultPos = 0;
		int pos = 0;
		for (String callableCommand : callableCommandNames) {
			int resourceId = getResources().getIdentifier(callableCommand, "string", getPackageName());
			if (resourceId != 0) {
				list.add(getString(resourceId));
			}
			else {
				list.add(callableCommand);
			}
			if (callableCommand.equals(command.getMethod())) {
				defaultPos = pos;
			}
			pos++;
		}
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
		spinner.setSelection(defaultPos);
	}

	private class AlertTypeItemSelectedListener implements OnItemSelectedListener {
		private Command command;

		public AlertTypeItemSelectedListener(Command command) {
			this.command = command;
		}

		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			String selectedMethod = callableCommandNames.get(pos);
			Command newCommand = PhoneControlModesManager.getCommand(selectedMethod);
			if (newCommand != null) {
				command.reset(newCommand);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.control_mode_editor, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void prepareCancel() {
		Intent resultIntent = new Intent();
		resultIntent.putExtra(ControlModesActivity.EDIT_CONTROL_MODE_EXTRA, currentMode);
		resultIntent.putExtra(ControlModesActivity.EDIT_ORIG_CONTROL_MODE_EXTRA, origModePosition);
		setResult(Activity.RESULT_CANCELED, resultIntent);
		finish();
	}

	@Override
	public void onBackPressed() {
		if (modeName.getText() == null || modeName.getText().toString().isEmpty()) {
			prepareCancel();
			return;
		}
		currentMode.setName(modeName.getText().toString());
		Intent resultIntent = new Intent();
		resultIntent.putExtra(ControlModesActivity.EDIT_CONTROL_MODE_EXTRA, currentMode);
		resultIntent.putExtra(ControlModesActivity.EDIT_ORIG_CONTROL_MODE_EXTRA, origModePosition);
		setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}

	private void showGB5600ButtonDescriptions() {
		visible2Buttons();
		button1.setText(getString(R.string.gb5600_down_right_button));
		button2.setText(getString(R.string.gb5600_upper_left_button));
	}

	private void showSTB1000ButtonDescriptions() {
		visible5Buttons();
		button1.setText(getString(R.string.stb1000_upper_right_button));
		button2.setText(getString(R.string.stb1000_down_left_button));
		button3.setText(getString(R.string.stb1000_down_right_button));
		button4.setText(getString(R.string.stb1000_upper_left_button));
		button5.setText(getString(R.string.stb1000_power_button));
	}

	private void showGBA400ButtonDescriptions() {
		visible5Buttons();
		button1.setText(getString(R.string.gba400_down_right_button));
		button2.setText(getString(R.string.gba400_down_left_button));
		button3.setText(getString(R.string.gba400_upper_left_button));
		button4.setText(getString(R.string.gba400_wheel_up_button));
		button5.setText(getString(R.string.gba400_wheel_down_button));
	}

	private void showGB6900ButtonDescriptions() {
		visible3Buttons();
		button1.setText(getString(R.string.default_upper_left_button));
		button2.setText(getString(R.string.default_upper_right_button));
		button3.setText(getString(R.string.default_down_right_button));
	}

	private void visible2Buttons() {
		button1.setVisibility(View.VISIBLE);
		button2.setVisibility(View.VISIBLE);
		button3.setVisibility(View.GONE);
		button4.setVisibility(View.GONE);
		button5.setVisibility(View.GONE);

		command1.setVisibility(View.VISIBLE);
		command2.setVisibility(View.VISIBLE);
		command3.setVisibility(View.GONE);
		command4.setVisibility(View.GONE);
		command5.setVisibility(View.GONE);
	}

	private void visible3Buttons() {
		button1.setVisibility(View.VISIBLE);
		button2.setVisibility(View.VISIBLE);
		button3.setVisibility(View.VISIBLE);
		button4.setVisibility(View.GONE);
		button5.setVisibility(View.GONE);

		command1.setVisibility(View.VISIBLE);
		command2.setVisibility(View.VISIBLE);
		command3.setVisibility(View.VISIBLE);
		command4.setVisibility(View.GONE);
		command5.setVisibility(View.GONE);
	}

	private void visible5Buttons() {
		button1.setVisibility(View.VISIBLE);
		button2.setVisibility(View.VISIBLE);
		button3.setVisibility(View.VISIBLE);
		button4.setVisibility(View.VISIBLE);
		button5.setVisibility(View.VISIBLE);

		command1.setVisibility(View.VISIBLE);
		command2.setVisibility(View.VISIBLE);
		command3.setVisibility(View.VISIBLE);
		command4.setVisibility(View.VISIBLE);
		command5.setVisibility(View.VISIBLE);
	}
}
