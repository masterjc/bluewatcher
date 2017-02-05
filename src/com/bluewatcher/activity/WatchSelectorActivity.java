package com.bluewatcher.activity;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.bluewatcher.R;
import com.bluewatcher.ble.BleDeviceListAdapter;

public class WatchSelectorActivity extends ListActivity {
	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

	private static final int REQUEST_ENABLE_BT = 1;
	private static final long SCAN_PERIOD = 10000;

	private BluetoothAdapter bluetoothAdapter;
	private BleDeviceListAdapter devicesListAdapter;
	private Handler mHandler;
	private boolean scanningBleDevices = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mHandler = new Handler();
		getActionBar().setTitle(R.string.devices_activity);
		initBluetoothManager();
	}

	@Override
	protected void onPause() {
		super.onPause();
		scanLeDevice(false);
		devicesListAdapter.clear();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);

		if (!scanningBleDevices) {
			menu.findItem(R.id.menu_stop).setVisible(false);
			menu.findItem(R.id.menu_scan).setVisible(true);
			menu.findItem(R.id.menu_refresh).setActionView(null);
		}
		else {
			menu.findItem(R.id.menu_stop).setVisible(true);
			menu.findItem(R.id.menu_scan).setVisible(false);
			menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_indeterminate_progress);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_scan:
			devicesListAdapter.clear();
			scanLeDevice(true);
			break;
		case R.id.menu_stop:
			scanLeDevice(false);
			break;
		case android.R.id.home:
			finish();
			break;
		}
		return true;
	}
	
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final BluetoothDevice device = devicesListAdapter.getDevice(position);
        if (device == null) return;
        
        if (scanningBleDevices) {
            bluetoothAdapter.stopLeScan(mLeScanCallback);
            scanningBleDevices = false;
        }
        
        final Intent intent = new Intent();
        intent.putExtra(EXTRAS_DEVICE_NAME, device.getName());
        intent.putExtra(EXTRAS_DEVICE_ADDRESS, device.getAddress());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

	@Override
	protected void onResume() {
		super.onResume();
		checkBluetoothEnabled();
		devicesListAdapter = new BleDeviceListAdapter(this.getLayoutInflater());
		setListAdapter(devicesListAdapter);
		scanLeDevice(true);
	}

	private void initBluetoothManager() {
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		bluetoothAdapter = bluetoothManager.getAdapter();

		if (bluetoothAdapter == null) {
			Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_LONG).show();
			finish();
		}
	}

	private void checkBluetoothEnabled() {
		if (!bluetoothAdapter.isEnabled()) {
			if (!bluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
			finish();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void scanLeDevice(final boolean enable) {
		if (enable) {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					scanningBleDevices = false;
					bluetoothAdapter.stopLeScan(mLeScanCallback);
					invalidateOptionsMenu();
				}
			}, SCAN_PERIOD);

			scanningBleDevices = true;
			bluetoothAdapter.startLeScan(mLeScanCallback);
		}
		else {
			scanningBleDevices = false;
			bluetoothAdapter.stopLeScan(mLeScanCallback);
		}
		invalidateOptionsMenu();
	}
	
	
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					devicesListAdapter.addDevice(device);
					devicesListAdapter.notifyDataSetChanged();
				}
			});
		}
	};

}
