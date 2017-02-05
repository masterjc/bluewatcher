package com.bluewatcher.service.client;

import java.util.List;
import java.util.UUID;

import org.json.JSONException;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.util.Log;

import com.bluewatcher.Device;
import com.bluewatcher.GattActionListener;
import com.bluewatcher.activity.BlueWatcherActivity;
import com.bluewatcher.app.finder.PhoneFinderConfig;
import com.bluewatcher.app.finder.PhoneFinderConfigManager;
import com.bluewatcher.ble.BluetoothClientManager;
import com.bluewatcher.ble.Service;

/**
 * @version $Revision$
 */
public class PhoneFinderService implements Service, GattActionListener {
	private final static String TAG = DefaultAlertService.class.getSimpleName();
	private static final UUID CASIO_IMMEDIATE_ALERT_SERVICE_UUID = UUID.fromString("26eb0005-b012-49a8-b1f8-394fb2032b0f");
	private static final UUID ALERT_LEVEL_CHARACTERISTIC_UUID = UUID.fromString("00002a06-0000-1000-8000-00805f9b34fb");
	private static final UUID CCC_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

	private BluetoothClientManager bleService;
	private Device connectedDevice;
	private Activity activity;

	public PhoneFinderService(Activity activity, BluetoothClientManager bleService) {
		this.bleService = bleService;
		this.activity = activity;
	}

	@Override
	public boolean isAvailable() {
		return connectedDevice != null;
	}

	@Override
	public int getDescriptionResourceId() {
		return com.bluewatcher.R.string.phone_finder_service;
	}

	@Override
	public void actionGattConnected(Device deviceName) {
		Log.i(TAG, "PhoneFinderService - actionGattConnected!");
	}

	@Override
	public void actionGattDisconnected(Device deviceName) {
		connectedDevice = null;
		Log.i(TAG, "PhoneFinderService - actionGattDisconnected!");
	}

	@Override
	public void actionGattServicesDiscovered(Device deviceName) {
		if (bleService.getInternalBleService() == null)
			return;
		
		if(!isPhoneFindingEnabled())
			return;
		
		List<BluetoothGattService> services = bleService.getInternalBleService().getSupportedGattServices();
		for (BluetoothGattService service : services) {
			if (service.getUuid().equals(CASIO_IMMEDIATE_ALERT_SERVICE_UUID)) {
				BluetoothGattCharacteristic characteristic = service.getCharacteristic(ALERT_LEVEL_CHARACTERISTIC_UUID);
				if (characteristic != null) {
					bleService.getInternalBleService().setCharacteristicNotification(characteristic, true);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {}
					BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CCC_DESCRIPTOR_UUID);
					if(descriptor == null)
						return;
					descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
					bleService.getInternalBleService().writeDescriptor(descriptor);
					Log.i(TAG, "PhoneFinderService - Discovered!");
					connectedDevice = deviceName;
					break;
				}
			}
		}
	}

	@Override
	public void notPairedDevice(Device deviceName) {
	}

	@Override
	public void actionCharacteristicChanged(Device deviceName, UUID characteristic) {
		if( !characteristic.equals(ALERT_LEVEL_CHARACTERISTIC_UUID))
			return;
		
		if( !isPhoneFindingEnabled() || PhoneFinderConfigManager.isFinding() )
			return;
		
		final Intent intent = new Intent(activity, BlueWatcherActivity.class);
		PhoneFinderConfigManager.setFindMeFlag();
		activity.startActivity(intent);
	}

	private boolean isPhoneFindingEnabled() {
		PhoneFinderConfig config = null;
		try {
			config = PhoneFinderConfigManager.load(0);
		}
		catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}
		return config.isAppEnabled();
	}
	
}
