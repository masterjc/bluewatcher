package com.bluewatcher.service.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.util.Log;
import android.widget.Toast;

import com.bluewatcher.Device;
import com.bluewatcher.GattActionListener;
import com.bluewatcher.R;
import com.bluewatcher.activity.SettingsActivity;
import com.bluewatcher.ble.BluetoothClientManager;
import com.bluewatcher.ble.NotificationTypeConstants;
import com.bluewatcher.ble.ServerService;
import com.bluewatcher.config.ConfigurationManager;
import com.bluewatcher.control.Button;
import com.bluewatcher.control.ButtonSelector;
import com.bluewatcher.control.Command;
import com.bluewatcher.control.ControlOption;
import com.bluewatcher.control.PhoneControlMode;
import com.bluewatcher.control.PhoneControlModes;
import com.bluewatcher.control.PhoneControlModesManager;
import com.bluewatcher.service.client.AlertService;
import com.bluewatcher.service.client.DefaultAlertService;
import com.google.android.vending.licensing.util.Base64;

/**
 * @version $Revision$
 */
public class WatchCtrlService implements ServerService, GattActionListener {
	private final static String TAG = DefaultAlertService.class.getSimpleName();
	
	private static final String READY_MESSAGE = "READY";
	private static final String CONTROL_MODE_HEADER = "X:";

	private static final UUID WATCH_FEATURES_SERVICE_UUID = UUID.fromString("26eb000d-b012-49a8-b1f8-394fb2032b0f");
	private static final UUID WATCH_CTRL_SERVICE_UUID = UUID.fromString("26eb0018-b012-49a8-b1f8-394fb2032b0f");
	private static final UUID KEY_CONTAINER_CHARACTERISTIC_UUID = UUID.fromString("26eb0019-b012-49a8-b1f8-394fb2032b0f");
	private static final UUID NAME_OF_APP_CHARACTERISTIC_UUID = UUID.fromString("26eb001d-b012-49a8-b1f8-394fb2032b0f");
	private static final UUID CCC_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
	private static final UUID FUNCTION_SWITCH_CHARACTERISTIC = UUID.fromString("26eb001e-b012-49a8-b1f8-394fb2032b0f");

	private boolean serverAvailable = false;
	private boolean clientAvailable = false;
	private Activity activity;

	private BluetoothGattCharacteristic gattCharacteristic;
	private BluetoothClientManager bleService;
	private Device connectedDevice;

	private ButtonSelector buttonSelector;
	private AlertService alertService;
	
	private Iterator<PhoneControlMode> phoneControlModes;
	private PhoneControlMode currentMode;
	
	private List<ControlModelChangeListener> modeChangeListeners = new ArrayList<ControlModelChangeListener>();

	public WatchCtrlService(Activity activity, BluetoothClientManager bleService, AlertService alertService) {
		this.activity = activity;
		this.bleService = bleService;
		this.alertService = alertService;
		buttonSelector = new ButtonSelector();
	}
	
	public void registerListener( ControlModelChangeListener listener ) {
		modeChangeListeners.add(listener);
	}
	
	public synchronized void reloadPhoneControlModes() {
		PhoneControlModes modes = PhoneControlModesManager.load();
		phoneControlModes = modes.iterator();
		if( !phoneControlModes.hasNext() )
			return;
		currentMode = phoneControlModes.next();
		sendControlMode();
	}

	@Override
	public BluetoothGattService createService() {
		BluetoothGattService bluetoothgattservice = new BluetoothGattService(WATCH_CTRL_SERVICE_UUID, 0);
		BluetoothGattCharacteristic bluetoothgattcharacteristic = new BluetoothGattCharacteristic(KEY_CONTAINER_CHARACTERISTIC_UUID, 4, 16);
		bluetoothgattcharacteristic.setValue(new byte[0]);
		BluetoothGattCharacteristic bluetoothgattcharacteristic1 = new BluetoothGattCharacteristic(NAME_OF_APP_CHARACTERISTIC_UUID, 2, 3);
		bluetoothgattcharacteristic1.setValue(READY_MESSAGE.getBytes());
		BluetoothGattDescriptor bluetoothgattdescriptor = new BluetoothGattDescriptor(CCC_DESCRIPTOR_UUID, 17);
		bluetoothgattdescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
		bluetoothgattcharacteristic1.addDescriptor(bluetoothgattdescriptor);
		bluetoothgattservice.addCharacteristic(bluetoothgattcharacteristic);
		bluetoothgattservice.addCharacteristic(bluetoothgattcharacteristic1);
		return bluetoothgattservice;
	}

	@Override
	public byte[] processReadRequest(BluetoothGattCharacteristic characteristic) {
		Log.i(TAG, "WatchCtrlService.processReadRequest " + characteristic.getUuid().toString());
		if (characteristic.getUuid().equals(NAME_OF_APP_CHARACTERISTIC_UUID)) {
			serverAvailable = true;
			(new Thread() {
				public void run() {
					try {
						Thread.sleep(3000);
					}
					catch (InterruptedException e) {
					}
					sendControlMode();
				}
			}).start();
			
			return READY_MESSAGE.getBytes();
		}
		return null;
	}

	@Override
	public byte[] processWriteRequest(BluetoothGattCharacteristic characteristic, int offset, byte[] writtenData) {
		
		Log.i(TAG, "WatchCtrlService.processWriteRequest -> " + characteristic.getUuid().toString() + " - " + Base64.encode(writtenData));
		Button button = buttonSelector.getPressedButton(connectedDevice, writtenData);
		if (button == null) {
			showUnknownKeyMessage(offset, writtenData);
			return null;
		}
		if( alertService.needCleanMessage() ) {
			cleanNotifications();
			return null;
		}
		Command command = currentMode.getCommand(button);
		if( command == null ) {
			showUnknownKeyMessage(offset, writtenData);
			return null;
		}
		if(!PhoneControlModesManager.call(command)) {
			showUnknownKeyMessage(offset, writtenData);
			return null;
		}
		return null;
	}

	private void showUnknownKeyMessage(int offset, byte[] key) {
		final byte[] data = key;
		final int off = offset;
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(
						activity.getApplicationContext(),
						activity.getApplicationContext().getString(R.string.unrecognized_button) + Base64.encode(data) + " - OFFSET:"
								+ Integer.toString(off), Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public Set<UUID> getKnownCharacteristics() {
		HashSet<UUID> uuids = new HashSet<UUID>();
		uuids.add(KEY_CONTAINER_CHARACTERISTIC_UUID);
		uuids.add(NAME_OF_APP_CHARACTERISTIC_UUID);
		return uuids;
	}

	@Override
	public boolean knowsCharacteristic(BluetoothGattCharacteristic characteristic) {
		for (UUID uuid : getKnownCharacteristics()) {
			if (uuid.equals(characteristic.getUuid()))
				return true;
		}
		return false;
	}

	@Override
	public boolean isAvailable() {
		if (connectedDevice == null)
			return false;
		if (connectedDevice.isGBA400() || connectedDevice.isSTB1000())
			return clientAvailable && serverAvailable;
		return serverAvailable;
	}

	@Override
	public int getDescriptionResourceId() {
		return com.bluewatcher.R.string.phone_control_service;
	}

	@Override
	public void serverDisconnected() {
		Log.i(TAG, "WatchCtrlService - Disconnected!");
	}

	@Override
	public void serverConnected() {
		Log.i(TAG, "WatchCtrlService - Connected!");
	}

	@Override
	public void actionGattConnected(Device deviceName) {
		Log.i(TAG, "WatchCtrlService - actionGattConnected!");
	}

	@Override
	public void actionGattDisconnected(Device deviceName) {
		Log.i(TAG, "WatchCtrlService - actionGattDisconnected!");
		gattCharacteristic = null;
		clientAvailable = false;
		serverAvailable = false;
		reloadPhoneControlModes();
	}

	@Override
	public void actionGattServicesDiscovered(Device deviceName) {
		if (bleService.getInternalBleService() == null)
			return;
		connectedDevice = deviceName;
		if (!deviceName.isGBA400() && !connectedDevice.isSTB1000())
			return;
		List<BluetoothGattService> services = bleService.getInternalBleService().getSupportedGattServices();
		for (BluetoothGattService service : services) {
			if (service.getUuid().equals(WATCH_FEATURES_SERVICE_UUID)) {
				BluetoothGattCharacteristic characteristic = service.getCharacteristic(FUNCTION_SWITCH_CHARACTERISTIC);
				if (characteristic != null) {
					BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CCC_DESCRIPTOR_UUID);
					descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
					bleService.getInternalBleService().writeDescriptor(descriptor);
					gattCharacteristic = characteristic;
					gattCharacteristic.setValue(READY_MESSAGE.getBytes());
					gattCharacteristic.setWriteType(2);
					bleService.getInternalBleService().writeCharacteristic(gattCharacteristic);
					clientAvailable = true;
					Log.i(TAG, "WatchCtrlService - Discovered!" + characteristic.getUuid().toString());
					reloadPhoneControlModes();
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
	}

	@ControlOption
	public synchronized void controlModeChanged() {
		if( !phoneControlModes.hasNext()) {
			reloadPhoneControlModes();
		} else {
			currentMode = phoneControlModes.next();
		}
		sendControlMode();
	}
	
	@ControlOption
	public void sendControlMode() {
		if( currentMode == null )
			return;

		for( ControlModelChangeListener listener : modeChangeListeners ) {
			listener.modeChanged(currentMode.getName());
		}
		
		boolean sendControlMode = Boolean.parseBoolean(ConfigurationManager.getInstance().load(SettingsActivity.SEND_CONTROL_MODE,
				Boolean.toString(true)));
		if(!sendControlMode)
			return;
		alertService.notifyWatch(NotificationTypeConstants.MAIL_NOTIFICATION_ID, CONTROL_MODE_HEADER + currentMode.getName());
	}
	
	@ControlOption
	public void cleanNotifications() {
		alertService.cleanMessage(READY_MESSAGE);
	}
}
