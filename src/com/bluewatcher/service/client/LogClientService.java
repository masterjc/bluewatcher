package com.bluewatcher.service.client;

import java.util.List;
import java.util.UUID;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import com.bluewatcher.Device;
import com.bluewatcher.GattActionListener;
import com.bluewatcher.ble.BluetoothClientManager;

public class LogClientService implements GattActionListener {
	private final static String TAG = LogClientService.class.getSimpleName();
	
	private BluetoothClientManager bleService;

	public LogClientService(BluetoothClientManager bleService) {
		this.bleService = bleService;
	}

	@Override
	public void actionGattConnected(Device deviceName) {
		Log.i(TAG, "LogClientService - Connected!");
	}

	@Override
	public void actionGattDisconnected(Device deviceName) {
		Log.i(TAG, "LogClientService - Disconnected!");
	}

	@Override
	public void actionGattServicesDiscovered(Device device) {
		List<BluetoothGattService> services = bleService.getInternalBleService().getSupportedGattServices();
		for (BluetoothGattService service : services) {
			for (BluetoothGattCharacteristic gattCharacteristic : service.getCharacteristics()) {
				Log.i(TAG, "LogClientService - Discovered. Service: " + service.getUuid().toString() + " - Characteristic: " + gattCharacteristic.getUuid().toString());
			}
		}
	}
	
	@Override
	public void notPairedDevice(Device deviceName) {	
	}
	
	@Override
	public void actionCharacteristicChanged(Device deviceName, UUID characteristic) {
	}
}
