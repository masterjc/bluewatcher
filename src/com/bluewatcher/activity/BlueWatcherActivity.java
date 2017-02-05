package com.bluewatcher.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.bluewatcher.BlueWatcherHelp;
import com.bluewatcher.BluewatcherActionReceiver;
import com.bluewatcher.Device;
import com.bluewatcher.NotificationAccessLauncher;
import com.bluewatcher.R;
import com.bluewatcher.ReconnectionActionListener;
import com.bluewatcher.ServicesContainer;
import com.bluewatcher.StatusBarNotificationManager;
import com.bluewatcher.app.AppsConfigurator;
import com.bluewatcher.app.AppsManager;
import com.bluewatcher.app.BlueWatcherConfig;
import com.bluewatcher.app.MissedNotifications;
import com.bluewatcher.app.call.IncomingCallConfig;
import com.bluewatcher.app.call.IncomingCallConfigManager;
import com.bluewatcher.app.finder.PhoneFinderConfigManager;
import com.bluewatcher.app.generic.GenericAppConfig;
import com.bluewatcher.app.generic.GenericAppConfigManager;
import com.bluewatcher.app.generic.TestServicesApp;
import com.bluewatcher.app.whatsapp.WhatsappConfig;
import com.bluewatcher.app.whatsapp.WhatsappConfigManager;
import com.bluewatcher.ble.BluetoothClientManager;
import com.bluewatcher.ble.BluetoothServerManager;
import com.bluewatcher.ble.ServerService;
import com.bluewatcher.camera.CameraController;
import com.bluewatcher.config.ConfigurationManager;
import com.bluewatcher.config.ConfigurationOption;
import com.bluewatcher.config.DeviceConfiguration;
import com.bluewatcher.config.OnConfigurationOptionSelected;
import com.bluewatcher.control.PhoneControlModesManager;
import com.bluewatcher.service.client.AlertServiceWrapper;
import com.bluewatcher.service.client.CasioServiceActivator;
import com.bluewatcher.service.client.DefaultAlertService;
import com.bluewatcher.service.client.Gba400AlertService;
import com.bluewatcher.service.client.LogClientService;
import com.bluewatcher.service.client.PhoneFinderService;
import com.bluewatcher.service.client.TimeService;
import com.bluewatcher.service.server.WatchCtrlService;
import com.bluewatcher.util.AndroidAppLauncher;
import com.bluewatcher.util.BatteryController;
import com.bluewatcher.util.BwToast;
import com.bluewatcher.util.License;
import com.bluewatcher.util.LollipopDetection;
import com.bluewatcher.util.MusicController;
import com.bluewatcher.util.WhatsNew;
import com.bluewatcher.view.ConnectionStatusView;
import com.bluewatcher.view.SelectWatchButtonView;

public class BlueWatcherActivity extends Activity {

	private static final int SELECT_WATCH_REQUEST = 1;
	private static final int ENABLE_BLUETOOTH = 2;

	private BluetoothServerManager serverService;
	private BluetoothClientManager clientService;
	private BluewatcherActionReceiver bwActionReceiver;
	private StatusBarNotificationManager notificationManager;

	private LogClientService logClientService;
	private DefaultAlertService defaultAlertService;
	private Gba400AlertService gba400AlertService;
	private AlertServiceWrapper alertServiceWrapper;
	private TimeService timeService;
	private CasioServiceActivator casioActivator;
	private WatchCtrlService ctrlService;
	private PhoneFinderService finderService;
	
	private ServicesContainer servicesContainer = new ServicesContainer();

	private AppsManager appsManager;
	private AppsConfigurator appConfigurator;

	private ConnectionStatusView connectionStatusView;
	private SelectWatchButtonView selectWatchView;

	private ListView listView;
	private License license;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blue_watcher);

		checkLicensing();
		checkBleSupported();
		
		NotificationAccessLauncher.requestAccess(this);
		ConfigurationManager.initialize(getPreferences(Context.MODE_PRIVATE));
		PhoneFinderConfigManager.resetFindMeFlag();
		
		boolean bluetoothEnabled = isBluetoothEnabled();
		if( bluetoothEnabled ) {
			initializeBlueWatcher();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		launchFindMe();
		launchCameraApp();
	}
	
	private void initializeBlueWatcher() {
		WhatsNew.show(this);
		LollipopDetection.show(this);
		notificationManager = new StatusBarNotificationManager(this);
		
		clientService = new BluetoothClientManager(this, notificationManager);
		connectionStatusView = new ConnectionStatusView(this);
		initializeConfigurationOptions();

		defaultAlertService = new DefaultAlertService(getApplicationContext(), clientService);
		gba400AlertService = new Gba400AlertService(getApplicationContext(), clientService);
		alertServiceWrapper = new AlertServiceWrapper();
		alertServiceWrapper.add(defaultAlertService);
		alertServiceWrapper.add(gba400AlertService);
		
		finderService = new PhoneFinderService(this, clientService);
		
		timeService = new TimeService(getApplicationContext(), clientService);
		casioActivator = new CasioServiceActivator(getApplicationContext(), clientService);
		logClientService = new LogClientService(clientService);
		
		ctrlService = new WatchCtrlService(this, clientService, alertServiceWrapper);
		ctrlService.registerListener(connectionStatusView);
		ctrlService.registerListener(notificationManager);
		
		List<Object> controlObjects = new ArrayList<Object>();
		controlObjects.add(new MusicController(this));
		controlObjects.add(CameraController.getInstance());
		controlObjects.add(new AndroidAppLauncher(this));
		controlObjects.add(ctrlService);
		controlObjects.add(new BatteryController(this.getApplicationContext(), alertServiceWrapper));
		PhoneControlModesManager.initialize(controlObjects);
		
		List<ServerService> serverServices = new ArrayList<ServerService>();
		serverServices.add(ctrlService);
		
		if(!ConfigurationManager.isBleServerDisabled()) {
			serverService = new BluetoothServerManager(this, notificationManager, serverServices);
		}

		MissedNotifications missedNotifications = new MissedNotifications(alertServiceWrapper, this);
		appsManager = new AppsManager(missedNotifications);

		servicesContainer.addService(alertServiceWrapper);
		servicesContainer.addService(timeService);
		servicesContainer.addService(ctrlService);
		servicesContainer.addService(finderService);

		selectWatchView = new SelectWatchButtonView(this, clientService);
		selectWatchView.registerListenersForResult();

		bwActionReceiver = new BluewatcherActionReceiver(this);
		bwActionReceiver.registerGattActionListener(connectionStatusView);
		bwActionReceiver.registerGattActionListener(notificationManager);
		bwActionReceiver.registerGattActionListener(defaultAlertService);
		bwActionReceiver.registerGattActionListener(gba400AlertService);
		bwActionReceiver.registerGattActionListener(timeService);
		bwActionReceiver.registerGattActionListener(finderService);
		bwActionReceiver.registerGattActionListener(casioActivator);
		bwActionReceiver.registerGattActionListener(ctrlService);
		bwActionReceiver.registerGattActionListener(logClientService);
		bwActionReceiver.registerGattActionListener(new ReconnectionActionListener(this, clientService));
		bwActionReceiver.registerNotificationActionListener(appsManager);
		bwActionReceiver.registerGenericActionListener(appsManager);
		bwActionReceiver.registerGattActionListener(appsManager);

		appConfigurator = new AppsConfigurator(appsManager, alertServiceWrapper);
		paintIfConnecting();
		applyConfiguration(true);

		clientService.autoWatchConnect();
	}

	private void paintIfConnecting() {
		Device device = DeviceConfiguration.getCurrentDevice();
		if (device == null)
			return;
		bwActionReceiver.setDevice(device);
		connectionStatusView.connecting(device.getName());
	}

	private void initializeConfigurationOptions() {
		listView = (ListView) findViewById(R.id.list_config_view);

		ArrayAdapter<ConfigurationOption> adapter = new ArrayAdapter<ConfigurationOption>(this.getBaseContext(), android.R.layout.simple_list_item_1);
		adapter.add(new ConfigurationOption(NotificationsActivity.class, getString(R.string.notifications_service)));
		adapter.add(new ConfigurationOption(ControlModesActivity.class, getString(R.string.phone_control_menu_text)));
		adapter.add(new ConfigurationOption(PhoneFinderConfigActivity.class, getString(R.string.phone_finder_service), servicesContainer));
		adapter.add(new ConfigurationOption(TimeConfigActivity.class, getString(R.string.time_sync_service)));
		adapter.add(new ConfigurationOption(AvailableServicesActivity.class, getString(R.string.show_available_services_text), servicesContainer));
		adapter.add(new ConfigurationOption(SettingsActivity.class, getString(R.string.bluewatcher_settings)));
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnConfigurationOptionSelected(this, listView));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.blue_watcher, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.click_exit_button), Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.bluewatcher_help:
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(BlueWatcherHelp.HELP_URL));
			startActivity(browserIntent);
			break;
		case R.id.action_settings:
			final Intent intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
			break;
		case R.id.test_watch:
			TestServicesApp testApp = new TestServicesApp(this, alertServiceWrapper, timeService);
			testApp.sendTestNotifications();
			break;
		case R.id.exit_settings:
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		license.destroy();
		if (bwActionReceiver != null) {
			bwActionReceiver.unload();
		}
		if (clientService != null) {
			clientService.stopAutoWatchConnect();
			clientService.destroy();
		}
		if (serverService != null) {
			serverService.destroy();
		}

		if (notificationManager != null) {
			notificationManager.destroy();
		}
		if (!isFinishing()) {
			Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.destroy_watcher_activity_resources), Toast.LENGTH_LONG)
					.show();
		}
		BwToast.getInstance().cancel();
	}

	private void checkBleSupported() {
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_LONG).show();
			finish();
		}
	}
	
	private boolean isBluetoothEnabled() {
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

		if (bluetoothAdapter == null) {
			Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_LONG).show();
			finish();
		}
	
		boolean enabled = true;
		if (!bluetoothAdapter.isEnabled()) {
			enabled = false;
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH);
		}
		return enabled;
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SELECT_WATCH_REQUEST) {
			watchSelected(data);
			return;
		} 
		
		if( requestCode == ENABLE_BLUETOOTH ) {
			if (resultCode == Activity.RESULT_CANCELED ) {
				finish();
				return;
			}
			initializeBlueWatcher();
			return;
		}
		
		applyConfiguration(false);
	}

	private void watchSelected(Intent data) {
		if (data != null) {
			String deviceAddress = data.getStringExtra(WatchSelectorActivity.EXTRAS_DEVICE_ADDRESS);
			String deviceName = data.getStringExtra(WatchSelectorActivity.EXTRAS_DEVICE_NAME);
			Device device = new Device(deviceAddress, deviceName);
			DeviceConfiguration.saveCurrentDevice(device);
			bwActionReceiver.setDevice(device);
			clientService.connect(device);
		}
		else {
			if (!clientService.isConnected()) {
				Device device = clientService.autoWatchConnect();
				if (device != null) {
					bwActionReceiver.setDevice(device);
				}
			}
		}
	}

	private void applyConfiguration(boolean onLoad) {
		if (!onLoad) {
			bwActionReceiver.unload();
		}
		ctrlService.reloadPhoneControlModes();
		appConfigurator.applyConfig(getBlueWatcherConfig());
		bwActionReceiver.load(appsManager.getRegisteredActions());
	}

	private BlueWatcherConfig getBlueWatcherConfig() {
		try {
			IncomingCallConfig callConfig = IncomingCallConfigManager.load();
			WhatsappConfig wConfig = WhatsappConfigManager.load();
			List<GenericAppConfig> customConfig = GenericAppConfigManager.load();
			return new BlueWatcherConfig(callConfig, wConfig, customConfig);
		}
		catch (Exception e) {
			ConfigurationManager.showConfigurationError(getApplicationContext());
			return null;
		}
	}

	private void checkLicensing() {
		license = new License(this);
		license.checkLicensing();
	}
	
	private void launchFindMe() {
		boolean findMe = PhoneFinderConfigManager.isFindMe();
		if( !findMe )
			return;
		PhoneFinderConfigManager.resetFindMeFlag();
		final Intent intent = new Intent(this, FindMeActivity.class);
		startActivity(intent);
	}
	
	private void launchCameraApp() {

		boolean startCamera = CameraController.isStartCamera();
		if( !startCamera )
			return;
		CameraController.resetStartCamera();
		final Intent intent = new Intent(this, CameraActivity.class);
		startActivity(intent);
	}
}

