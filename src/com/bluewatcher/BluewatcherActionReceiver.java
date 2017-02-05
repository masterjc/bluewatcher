package com.bluewatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.bluewatcher.ble.BluetoothClientService;

/**
 * @version $Revision$
 */
public class BluewatcherActionReceiver {
	private final static String TAG = BluewatcherActionReceiver.class.getSimpleName();

	private List<GattActionListener> gattListeners = new ArrayList<GattActionListener>();
	private List<NotificationActionListener> notificationListeners = new ArrayList<NotificationActionListener>();
	private List<GenericActionListener> genericListeners = new ArrayList<GenericActionListener>();
	private Activity activity;

	private Device device;
	
	private boolean disconnectReceivedOnce = false;

	public BluewatcherActionReceiver(Activity activity) {
		this.activity = activity;
	}

	public void registerGattActionListener(GattActionListener listener) {
		gattListeners.add(listener);
	}

	public void registerNotificationActionListener(NotificationActionListener listener) {
		notificationListeners.add(listener);
	}

	public void registerGenericActionListener(GenericActionListener listener) {
		genericListeners.add(listener);
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public void load(List<String> registeredActions) {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothClientService.ACTION_NOT_PAIRED);
		intentFilter.addAction(BluetoothClientService.ACTION_CHARACTERISTIC_CHANGED);
		intentFilter.addAction(BluetoothClientService.ACTION_GATT_CLIENT_CONNECTED);
		intentFilter.addAction(BluetoothClientService.ACTION_GATT_CLIENT_DISCONNECTED);
		intentFilter.addAction(BluetoothClientService.ACTION_GATT_CLIENT_SERVICES_DISCOVERED);
		intentFilter.addAction(WatchNotificationListener.NEW_STATUS_BAR_NOTIFICATION);
		intentFilter.addAction(WatchNotificationListener.REMOVED_STATUS_BAR_NOTIFICATION);
		if (registeredActions != null) {
			for (String action : registeredActions) {
				intentFilter.addAction(action);
			}
		}
		activity.registerReceiver(watcherBroadcastReceiver, intentFilter);
	}

	public void unload() {
		activity.unregisterReceiver(watcherBroadcastReceiver);
	}

	private final BroadcastReceiver watcherBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothClientService.ACTION_NOT_PAIRED.equals(action)) {
				Log.d(TAG, "Broadcast received: " + BluetoothClientService.ACTION_NOT_PAIRED + " DeviceName: " + device.getName());
				for (GattActionListener listener : gattListeners) {
					listener.notPairedDevice(device);
				}
			}
			else if (BluetoothClientService.ACTION_CHARACTERISTIC_CHANGED.equals(action)) {
				Log.d(TAG, "Broadcast received: " + BluetoothClientService.ACTION_CHARACTERISTIC_CHANGED + " DeviceName: " + device.getName());
				String characteristicId = intent.getStringExtra(BluetoothClientService.EXTRA_CHARACTERISTIC_UUID);
				if (characteristicId == null)
					return;
				UUID uuid = UUID.fromString(characteristicId);
				for (GattActionListener listener : gattListeners) {
					listener.actionCharacteristicChanged(device, uuid);
				}
			}
			else if (BluetoothClientService.ACTION_GATT_CLIENT_CONNECTED.equals(action)) {
				disconnectReceivedOnce = false;
				Log.d(TAG, "Broadcast received: " + BluetoothClientService.ACTION_GATT_CLIENT_CONNECTED + " DeviceName: " + device.getName());
				for (GattActionListener listener : gattListeners) {
					listener.actionGattConnected(device);
				}
			}
			else if (BluetoothClientService.ACTION_GATT_CLIENT_DISCONNECTED.equals(action)) {
				if( disconnectReceivedOnce )
					return;
				disconnectReceivedOnce = true;
				Log.d(TAG, "Broadcast received: " + BluetoothClientService.ACTION_GATT_CLIENT_DISCONNECTED + " DeviceName: " + device.getName());
				for (GattActionListener listener : gattListeners) {
					listener.actionGattDisconnected(device);
				}
			}
			else if (BluetoothClientService.ACTION_GATT_CLIENT_SERVICES_DISCOVERED.equals(action)) {
				Log.d(TAG,
						"Broadcast received: " + BluetoothClientService.ACTION_GATT_CLIENT_SERVICES_DISCOVERED + " DeviceName: " + device.getName());
				for (GattActionListener listener : gattListeners) {
					listener.actionGattServicesDiscovered(device);
				}
			}
			else if (WatchNotificationListener.NEW_STATUS_BAR_NOTIFICATION.equals(action)) {
				Log.d(TAG, "Broadcast received: " + WatchNotificationListener.NEW_STATUS_BAR_NOTIFICATION);
				Notification sbn = intent.getParcelableExtra(WatchNotificationListener.EXTRA_NOTIFICATION);
				if (sbn == null)
					return;
				for (NotificationActionListener listener : notificationListeners) {
					listener.newStatusBarNotification(context, sbn);
				}
			}
			else if (WatchNotificationListener.REMOVED_STATUS_BAR_NOTIFICATION.equals(action)) {
				Log.d(TAG, "Broadcast received: " + WatchNotificationListener.REMOVED_STATUS_BAR_NOTIFICATION);
				Notification sbn = intent.getParcelableExtra(WatchNotificationListener.EXTRA_NOTIFICATION);
				if (sbn == null)
					return;
				for (NotificationActionListener listener : notificationListeners) {
					listener.removedStatusBarNotification(context, sbn);
				}
			}
			else {
				Log.d(TAG, "GENERIC Broadcast received: ");
				for (GenericActionListener listener : genericListeners) {
					listener.genericAction(context, intent);
				}
			}
		}
	};
}
