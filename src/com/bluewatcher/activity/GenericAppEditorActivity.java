package com.bluewatcher.activity;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bluewatcher.AlertType;
import com.bluewatcher.Device;
import com.bluewatcher.R;
import com.bluewatcher.app.generic.GenericAppConfig;
import com.bluewatcher.config.DeviceConfiguration;

public class GenericAppEditorActivity extends Activity {
	public static final String EDIT_APP_INFO = "com.bluewatcher.EDIT_APP_INFO_EXTRA";
	public static final String RESULT_APP_INFO = "com.bluewatcher.RESULT_APP_INFO_EXTRA";

	private TextView displayName;
	private CheckBox prefixCheckbox;
	private EditText prefix;
	private CheckBox labelCheckbox;
	private EditText label;
	private TextView alertSpinnerText;
	private Spinner alertTypeSpinner;
	private ListView headerFiltersList;
	private Button addHeaderFilterButton;

	private AlertType selectedAlertType;

	private Button okButton;
	private Button cancelButton;

	private GenericAppConfig appConfiguration;
	private boolean isGba400 = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generic_app_editor);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		appConfiguration = getIntent().getParcelableExtra(EDIT_APP_INFO);
		Device device = DeviceConfiguration.getCurrentDevice();
		if (device != null && device.isGBA400())
			isGba400 = true;

		displayName = (TextView) findViewById(R.id.generic_app_name);
		displayName.setText(appConfiguration.getAppDisplayName());
		prefixCheckbox = (CheckBox) findViewById(R.id.custom_prefix_checkboxk);
		labelCheckbox = (CheckBox) findViewById(R.id.custom_label_checkbox);
		prefix = (EditText) findViewById(R.id.edit_alert_prefix_value);
		label = (EditText) findViewById(R.id.label_edit_text);
		headerFiltersList = (ListView) findViewById(R.id.header_filters_list);
		addHeaderFilterButton = (Button) findViewById(R.id.add_header_filter_button);
		alertSpinnerText = (TextView) findViewById(R.id.alert_type_text);

		alertTypeSpinner = (Spinner) findViewById(R.id.edit_alert_type_id);

		displayName.setText(appConfiguration.getAppDisplayName());
		prefix.setText(appConfiguration.getPrefix());
		label.setText(appConfiguration.getLabel());
		if (appConfiguration.getLabel() != null && !appConfiguration.getLabel().isEmpty()) {
			labelCheckbox.setChecked(true);
		}
		else {
			labelCheckbox.setChecked(false);
		}
		if (appConfiguration.getPrefix() != null && !appConfiguration.getPrefix().isEmpty()) {
			prefixCheckbox.setChecked(true);
		}
		else {
			prefixCheckbox.setChecked(false);
		}

		selectedAlertType = appConfiguration.getAlertType();

		okButton = (Button) findViewById(R.id.edit_ok_button);
		cancelButton = (Button) findViewById(R.id.edit_cancel);

		reloadHeaderFiltersList();
		configureAlertTypeSpinner();
		registerListeners();
	}

	private void reloadHeaderFiltersList() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getBaseContext(), android.R.layout.simple_list_item_1);
		for (String filter : appConfiguration.getMessageFilters()) {
			adapter.add(filter);
		}
		headerFiltersList.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	private void configureAlertTypeSpinner() {
		if (isGba400) {
			alertSpinnerText.setVisibility(View.GONE);
			alertTypeSpinner.setVisibility(View.GONE);
			return;
		}
		alertSpinnerText.setVisibility(View.VISIBLE);
		alertTypeSpinner.setVisibility(View.VISIBLE);
		List<String> list = new ArrayList<String>();
		int defaultPos = 0;
		for (int i = 0; i < AlertType.values().length; i++) {
			AlertType type = AlertType.values()[i];
			list.add(type.getStringId());
			if (type.equals(selectedAlertType)) {
				defaultPos = i;
			}
		}
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		alertTypeSpinner.setAdapter(dataAdapter);
		alertTypeSpinner.setSelection(defaultPos);

	}

	private void registerListeners() {
		okButton.setOnClickListener(new OkButtonListener());
		cancelButton.setOnClickListener(new CancelButtonListener());
		alertTypeSpinner.setOnItemSelectedListener(new AlertTypeItemSelectedListener());
		addHeaderFilterButton.setOnClickListener(new AddHeaderFilterButtonListener(this));
		headerFiltersList.setOnItemClickListener(new OnHeaderFilterSelected(this));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.generic_app_editor, menu);
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
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}

	private class OkButtonListener implements View.OnClickListener {
		public void onClick(View v) {
			returnModifiedApp();
		}
	}

	@Override
	public void onBackPressed() {
		returnModifiedApp();
		finish();
	}

	private void returnModifiedApp() {
		appConfiguration.setAppDisplayName(displayName.getText().toString());
		if (!prefixCheckbox.isChecked()) {
			appConfiguration.setPrefix(null);
		}
		else {
			appConfiguration.setPrefix(prefix.getText().toString());
		}

		if (!labelCheckbox.isChecked()) {
			appConfiguration.setLabel(null);
		}
		else {
			appConfiguration.setLabel(label.getText().toString());
		}
		appConfiguration.setAlertType(selectedAlertType);
		returnResultApplicationInfo();
	}

	private class CancelButtonListener implements View.OnClickListener {
		public void onClick(View v) {
			returnRemoveApplicationInfo();
		}
	}

	private class AlertTypeItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			selectedAlertType = AlertType.fromStringId(parent.getItemAtPosition(pos).toString());
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	private void returnResultApplicationInfo() {
		Intent resultIntent = new Intent();
		resultIntent.putExtra(RESULT_APP_INFO, appConfiguration);
		setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}

	private void returnRemoveApplicationInfo() {
		Intent resultIntent = new Intent();
		resultIntent.putExtra(RESULT_APP_INFO, appConfiguration);
		setResult(Activity.RESULT_CANCELED, resultIntent);
		finish();
	}

	private class OnHeaderFilterSelected implements OnItemClickListener {
		private Activity parent;

		public OnHeaderFilterSelected(Activity parent) {
			this.parent = parent;
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			final String originalFilter = (String) headerFiltersList.getItemAtPosition(position);
			AlertDialog.Builder alert = new AlertDialog.Builder(parent);
			alert.setTitle(parent.getText(R.string.header_filter_text));
			final EditText input = new EditText(parent);
			input.setText(originalFilter);
			alert.setView(input);
			alert.setPositiveButton(parent.getText(R.string.ok_text), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					String filter = input.getText().toString();
					appConfiguration.getMessageFilters().remove(originalFilter);
					appConfiguration.getMessageFilters().add(filter);
					reloadHeaderFiltersList();
				}
			});
			alert.setNegativeButton(parent.getText(R.string.remove_text), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					appConfiguration.getMessageFilters().remove(originalFilter);
					reloadHeaderFiltersList();
				}
			});
			alert.show();
		}
	}

	private class AddHeaderFilterButtonListener implements View.OnClickListener {
		private Activity parent;

		public AddHeaderFilterButtonListener(Activity parent) {
			this.parent = parent;
		}

		public void onClick(View v) {
			AlertDialog.Builder alert = new AlertDialog.Builder(parent);
			alert.setTitle(parent.getText(R.string.header_filter_text));
			final EditText input = new EditText(parent);
			alert.setView(input);
			alert.setPositiveButton(parent.getText(R.string.ok_text), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					String filter = input.getText().toString();
					appConfiguration.getMessageFilters().add(filter);
					reloadHeaderFiltersList();
				}
			});
			alert.setNegativeButton(parent.getText(R.string.cancel_text), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			alert.show();
		}
	}

}
