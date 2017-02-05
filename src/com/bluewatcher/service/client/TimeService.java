package com.bluewatcher.service.client;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.bluewatcher.Device;
import com.bluewatcher.GattActionListener;
import com.bluewatcher.R;
import com.bluewatcher.activity.TimeConfigActivity;
import com.bluewatcher.ble.BluetoothClientManager;
import com.bluewatcher.ble.Service;
import com.bluewatcher.config.ConfigurationManager;

public class TimeService implements Service, GattActionListener {
	private static final UUID CURRENT_TIME_CHARACTERISTIC_UUID = UUID.fromString("00002a2b-0000-1000-8000-00805f9b34fb");
	
	private final static String TAG = TimeService.class.getSimpleName();

	private BluetoothGattCharacteristic timeGattCharacteristic;
	private BluetoothClientManager bleService;
	private Context context;
	private boolean syncPending = false;

	public TimeService(Context context, BluetoothClientManager bleService) {
		this.bleService = bleService;
		this.context = context;
	}
	
	public boolean isAvailable() {
		return timeGattCharacteristic != null;
	}

	public synchronized void sendTime() throws Exception {
		boolean activated = Boolean.parseBoolean(ConfigurationManager.getInstance().load(TimeConfigActivity.DATETIME_SYNC_CONFIG,
				Boolean.toString(true)));
		if (!activated)
			return;
		if (timeGattCharacteristic == null || bleService.getInternalBleService() == null || !bleService.isConnected()) {
			Toast.makeText(context, context.getString(R.string.error_no_ble_service), Toast.LENGTH_SHORT).show();
			return;
		}
		timeGattCharacteristic.setValue(createCurrentTimeValue());
		timeGattCharacteristic.setWriteType(2);
		bleService.getInternalBleService().writeCharacteristic(timeGattCharacteristic);
	}

	public boolean isReady() {
		return timeGattCharacteristic != null;
	}

	public class TimeSendThread extends Thread {
		public void run() {
			try {
				Thread.sleep(15000);
				if (isReady() && bleService.isConnected()) {
					sendTime();
					Log.i(TAG, "TimeService::Synchronization OK!");
				}
				syncPending = false;
			}
			catch (Exception e) {
				Log.i(TAG, "TimeService::Synchronization ERROR!" + e.getMessage());
			}
		}
	}

	/*
	 * Retrasamos la sincronización 10 segundos puesto que es posible que no se haya completado aún
	 * la conexión
	 */
	@Override
	public void actionGattConnected(Device deviceName) {
		synchronized (this) {
			if (syncPending)
				return;
			syncPending = true;
			new TimeSendThread().start();
		}
	}

	@Override
	public void actionGattDisconnected(Device deviceName) {
		timeGattCharacteristic = null;
	}

	@Override
	public void actionGattServicesDiscovered(Device device) {
		if (bleService.getInternalBleService() == null) {
			Toast.makeText(context, context.getString(R.string.error_no_ble_service), Toast.LENGTH_SHORT).show();
			return;
		}
		List<BluetoothGattService> services = bleService.getInternalBleService().getSupportedGattServices();
		for (BluetoothGattService service : services) {
			for (BluetoothGattCharacteristic gattCharacteristic : service.getCharacteristics()) {
				if (gattCharacteristic.getUuid().equals(CURRENT_TIME_CHARACTERISTIC_UUID)) {
					Log.i(TAG, "TimeService - Discovered!" + gattCharacteristic.getUuid().toString());
					timeGattCharacteristic = gattCharacteristic;
				}
			}
		}
	}

	private static byte[] createCurrentTimeValue() {
		byte[] arrayOfByte = new byte[10];
		Calendar localCalendar = Calendar.getInstance();
		int i = localCalendar.get(1);
		arrayOfByte[0] = (byte) (0xFF & i >>> 0);
		arrayOfByte[1] = (byte) (0xFF & i >>> 8);
		arrayOfByte[2] = (byte) (1 + localCalendar.get(2));
		arrayOfByte[3] = (byte) localCalendar.get(5);
		arrayOfByte[4] = (byte) localCalendar.get(11);
		arrayOfByte[5] = (byte) localCalendar.get(12);
		arrayOfByte[6] = (byte) (localCalendar.get(13) + 1);
		switch (localCalendar.get(7)) {
		case Calendar.MONDAY:
			arrayOfByte[7] = 0x01;
			break;
		case Calendar.TUESDAY:
			arrayOfByte[7] = 0x02;
			break;
		case Calendar.WEDNESDAY:
			arrayOfByte[7] = 0x03;
			break;
		case Calendar.THURSDAY:
			arrayOfByte[7] = 0x04;
			break;
		case Calendar.FRIDAY:
			arrayOfByte[7] = 0x05;
			break;
		case Calendar.SATURDAY:
			arrayOfByte[7] = 0x06;
			break;
		case Calendar.SUNDAY:
			arrayOfByte[7] = 0x07;
			break;
		}
		arrayOfByte[8] = (byte) (int) TimeUnit.MILLISECONDS.toSeconds(256 * localCalendar.get(14));
		arrayOfByte[9] = 0;
		return arrayOfByte;
	}
	
	@Override
	public int getDescriptionResourceId() {
		return com.bluewatcher.R.string.time_sync_service;
	}
	
	@Override
	public void notPairedDevice(Device deviceName) {	
	}
	
	@Override
	public void actionCharacteristicChanged(Device deviceName, UUID characteristic) {
	}
}
