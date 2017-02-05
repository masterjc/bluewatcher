package com.bluewatcher.activity;

import org.json.JSONException;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.bluewatcher.NotificationAccessLauncher;
import com.bluewatcher.R;
import com.bluewatcher.config.ConfigurationManager;

public class SettingsActivity extends Activity {
	public final static String STARTUP_AT_BOOT_CONFIG = "startup_at_boot";
	
	public final static String RECONNECT_MESSAGE_CONFIG = "show_reconnect_message";
	public final static String SEND_CONTROL_MODE = "send_control_mode";
	public final static String GBA400_CLEAN_NOTIFICATION = "gba400_clean_notification";
	public final static String DISABLE_SERVER_SERVICES = "disable_server_services";
	
	private CheckBox startAtBoot;
	
	private CheckBox showReconnectMessage;
	private CheckBox sendControlMode;
	private CheckBox gba400CleanNotification;
	private CheckBox disableServer;
	private Button notificationsAccessButton;
	private Button startCameraButton;
	private PackageManager pm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_settings);
		pm = getPackageManager();
		
		startAtBoot = (CheckBox) findViewById(R.id.startup_at_boot);
		
		showReconnectMessage = (CheckBox) findViewById(R.id.show_reconnect_message);
		sendControlMode = (CheckBox) findViewById(R.id.send_control_mode);
		gba400CleanNotification = (CheckBox) findViewById(R.id.gba400_clean_notification);
		disableServer = (CheckBox) findViewById(R.id.disableServerOption);
		startCameraButton = (Button)findViewById(R.id.start_camera);
		startCameraButton.setOnClickListener(new StartCameraButtonListener(this));
		
		notificationsAccessButton = (Button)findViewById(R.id.notifications_config_button);
		notificationsAccessButton.setOnClickListener(new NotificationsConfigButtonListener(this));
		
		try {
			loadConfiguration();
		} catch(JSONException e) {
			ConfigurationManager.showConfigurationError(getApplicationContext());
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
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
	
	private class NotificationsConfigButtonListener implements View.OnClickListener {
		private Activity activity;
		
		NotificationsConfigButtonListener(Activity activity) {
			this.activity = activity;
		}
		public void onClick(View v) {
			NotificationAccessLauncher.openNotificationsConfig(activity);
		}
	}
	
	private class StartCameraButtonListener implements View.OnClickListener {
		private Activity activity;
		
		StartCameraButtonListener(Activity activity) {
			this.activity = activity;
		}
		public void onClick(View v) {
			Intent intent = new Intent(activity, CameraActivity.class);
			activity.startActivity(intent);
		}
	}
	
	private void loadConfiguration() throws JSONException {
		ComponentName componentName = new ComponentName("com.bluewatcher", "com.bluewatcher.BootupLoader");
	    int setting = pm.getComponentEnabledSetting(componentName);
	    boolean enabled = false;
	    if(setting == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
	    	enabled = true;
	    }
		String reconnectMessage = ConfigurationManager.getInstance().load(RECONNECT_MESSAGE_CONFIG, Boolean.toString(true));
		String controlMode = ConfigurationManager.getInstance().load(SEND_CONTROL_MODE, Boolean.toString(true));
		String gba400Clean = ConfigurationManager.getInstance().load(GBA400_CLEAN_NOTIFICATION, Boolean.toString(false));
		String disableServerOption = ConfigurationManager.getInstance().load(DISABLE_SERVER_SERVICES, Boolean.toString(false));
		startAtBoot.setChecked(enabled);
		showReconnectMessage.setChecked(Boolean.parseBoolean(reconnectMessage));
		sendControlMode.setChecked(Boolean.parseBoolean(controlMode));
		gba400CleanNotification.setChecked(Boolean.parseBoolean(gba400Clean));
		disableServer.setChecked(Boolean.parseBoolean(disableServerOption));
	}
	
	private void saveConfiguration() throws JSONException {
		PackageManager pm = getPackageManager();
		ComponentName componentName = new ComponentName("com.bluewatcher", "com.bluewatcher.BootupLoader");
		int state = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
		if( !startAtBoot.isChecked() )
			state = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
		pm.setComponentEnabledSetting(componentName, state, PackageManager.DONT_KILL_APP);
		ConfigurationManager.getInstance().save(RECONNECT_MESSAGE_CONFIG, Boolean.toString(showReconnectMessage.isChecked()));
		ConfigurationManager.getInstance().save(SEND_CONTROL_MODE, Boolean.toString(sendControlMode.isChecked()));
		ConfigurationManager.getInstance().save(GBA400_CLEAN_NOTIFICATION, Boolean.toString(gba400CleanNotification.isChecked()));
		ConfigurationManager.getInstance().save(DISABLE_SERVER_SERVICES, Boolean.toString(disableServer.isChecked()));
	}
}
