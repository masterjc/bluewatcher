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
import com.bluewatcher.activity.SettingsActivity;
import com.bluewatcher.ble.BluetoothClientManager;
import com.bluewatcher.ble.NotificationTypeConstants;
import com.bluewatcher.config.ConfigurationManager;
import com.bluewatcher.util.Transliterator;

public class Gba400AlertService implements AlertService, GattActionListener {
	public static final UUID ALERT_SERVICE_UUID = UUID.fromString("26eb001a-b012-49a8-b1f8-394fb2032b0f");
	public static final UUID ALERT_CHARACTERISTIC_UUID = UUID.fromString("26eb001c-b012-49a8-b1f8-394fb2032b0f");

	private final static String TAG = Gba400AlertService.class.getSimpleName();

	private BluetoothGattCharacteristic alertGattCharacteristic;
	private BluetoothClientManager bleService;
	private Context context;
	private Transliterator transliterator;
	private boolean needCleanMessage;

	public Gba400AlertService(Context context, BluetoothClientManager bleService) {
		this.bleService = bleService;
		this.context = context;
		this.needCleanMessage = false;
		transliterator = new Transliterator(context);
		
	}

	@Override
	public boolean isAvailable() {
		return alertGattCharacteristic != null;
	}

	@Override
	public synchronized void notifyWatch(int notificationId, String message) {
		if (alertGattCharacteristic == null || bleService.getInternalBleService() == null || !bleService.isConnected()) {
			Toast.makeText(context, context.getString(R.string.error_no_ble_service), Toast.LENGTH_SHORT).show();
			return;
		}
		notifyMessage(message);
	}
	
	private void notifyMessage(String message) {
		message = transliterator.translate(message);
		byte[] abyte0 = null;
		try {
			if (message.length() > 18)
				abyte0 = message.subSequence(0, 17).toString().getBytes("UTF-8");
			else
				abyte0 = message.getBytes("UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		byte abyte1[] = new byte[3 + abyte0.length];
		abyte1[0] = 0;
		abyte1[1] = 10;
		abyte1[2] = 1;
		int i = 0;
		do {
			if (i >= abyte0.length) {
				alertGattCharacteristic.setValue(abyte1);
				alertGattCharacteristic.setWriteType(2);
				bleService.getInternalBleService().writeCharacteristic(alertGattCharacteristic);
				needCleanMessage = true;
				return;
			}
			abyte1[i + 3] = abyte0[i];
			i++;
		} while (true);
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
		if (!device.isGBA400())
			return;
		if (bleService.getInternalBleService() == null) {
			Toast.makeText(context, context.getString(R.string.error_no_ble_service), Toast.LENGTH_SHORT).show();
			return;
		}
		List<BluetoothGattService> services = bleService.getInternalBleService().getSupportedGattServices();
		for (BluetoothGattService service : services) {
			if (service.getUuid().equals(ALERT_SERVICE_UUID)) {
				BluetoothGattCharacteristic gattCharacteristic = service.getCharacteristic(ALERT_CHARACTERISTIC_UUID);
				if (gattCharacteristic != null) {
					Log.i(TAG, "GBA-400 AlertService - Discovered!" + gattCharacteristic.getUuid().toString());
					alertGattCharacteristic = gattCharacteristic;
				}
			}
		}
	}

	@Override
	public int getDescriptionResourceId() {
		return com.bluewatcher.R.string.gba400_notifications_service;
	}

	@Override
	public void notPairedDevice(Device deviceName) {
	}

	@Override
	public void actionCharacteristicChanged(Device deviceName, UUID characteristic) {
	}

	@Override
	public void cleanMessage(String message) {
		notifyWatch(NotificationTypeConstants.SNS_NOTIFICATION_ID, message);
		needCleanMessage = false;
	}

	@Override
	public boolean needCleanMessage() {
		String gba400Clean = ConfigurationManager.getInstance().load(SettingsActivity.GBA400_CLEAN_NOTIFICATION, Boolean.toString(false));
		if( !Boolean.parseBoolean(gba400Clean) )
			return false; 
		return needCleanMessage;
	}

}
