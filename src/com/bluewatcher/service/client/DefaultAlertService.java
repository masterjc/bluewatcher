package com.bluewatcher.service.client;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.bluewatcher.Device;
import com.bluewatcher.GattActionListener;
import com.bluewatcher.R;
import com.bluewatcher.ble.BluetoothClientManager;
import com.bluewatcher.util.Transliterator;

public class DefaultAlertService implements AlertService, GattActionListener {
	public static final UUID ALERT_CHARACTERISTIC_UUID = UUID.fromString("00002a46-0000-1000-8000-00805f9b34fb");
	
	private final static String TAG = DefaultAlertService.class.getSimpleName();
	
	private BluetoothGattCharacteristic alertGattCharacteristic;
	private BluetoothClientManager bleService;
	private Context context;
	private Transliterator transliterator;

	public DefaultAlertService(Context context, BluetoothClientManager bleService) {
		this.bleService = bleService;
		this.context = context;
		transliterator = new Transliterator(context);
	}
	
	@Override
	public boolean isAvailable() {
		return alertGattCharacteristic != null;
	}
	
	@Override
	public synchronized void notifyWatch(int notificationId, String message) {
		if( alertGattCharacteristic == null || bleService.getInternalBleService() == null || !bleService.isConnected() ) {
			Toast.makeText(context, context.getString(R.string.error_no_ble_service), Toast.LENGTH_SHORT).show();
			return;
		}
		message = transliterator.translate(message);
		byte[] arrayOfByte1 = null;
		try {
			if( message.length() > 18 )
				arrayOfByte1 = message.subSequence(0, 17).toString().getBytes("UTF-8");
			else
				arrayOfByte1 = message.getBytes("UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        byte[] arrayOfByte2 = new byte[2 + arrayOfByte1.length];
        arrayOfByte2[0] = (byte)notificationId;
        arrayOfByte2[1] = 1;
        for (int i = 0; ; ++i)
        {
          if (i >= arrayOfByte1.length)
          {
        	  alertGattCharacteristic.setValue(arrayOfByte2);
        	  alertGattCharacteristic.setWriteType(1);
        	  bleService.getInternalBleService().writeCharacteristic(alertGattCharacteristic);
        	  break;
          }
          arrayOfByte2[(i + 2)] = arrayOfByte1[i];
        }
	}
	
	@Override
	public void actionGattConnected(Device deviceName) {
	}

	@Override
	public void actionGattDisconnected(Device deviceName) {
		alertGattCharacteristic = null;
	}

	@Override
	public void actionGattServicesDiscovered(Device device) {
		if( bleService.getInternalBleService() == null ) {
			Toast.makeText(context, context.getString(R.string.error_no_ble_service), Toast.LENGTH_SHORT).show();
			return;
		}
		List<BluetoothGattService> services = bleService.getInternalBleService().getSupportedGattServices();
		for (BluetoothGattService service : services) {
			for (BluetoothGattCharacteristic gattCharacteristic : service.getCharacteristics()) {
				if (gattCharacteristic.getUuid().equals(ALERT_CHARACTERISTIC_UUID)) {
					Log.i(TAG, "AlertService - Discovered!" + gattCharacteristic.getUuid().toString());
					alertGattCharacteristic = gattCharacteristic;
				}
			}
		}
	}
	
	@Override
	public int getDescriptionResourceId() {
		return com.bluewatcher.R.string.notifications_service;
	}
	
	@Override
	public void notPairedDevice(Device deviceName) {	
	}

	@Override
	public void actionCharacteristicChanged(Device deviceName, UUID characteristic) {
	}

	@Override
	public void cleanMessage(String message) {
	}

	@Override
	public boolean needCleanMessage() {
		return false;
	}

}
