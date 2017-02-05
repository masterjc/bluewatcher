package com.bluewatcher.ble;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

import com.bluewatcher.Device;
import com.bluewatcher.R;
import com.bluewatcher.StatusBarNotificationManager;
import com.bluewatcher.config.DeviceConfiguration;

/**
 * @version $Revision$
 */
public class BluetoothClientManager {
	private Activity serviceActivity;
	private BluetoothClientService clientService;
	private boolean initialized = false;
	private StatusBarNotificationManager appNotification;

	private final ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {
			clientService = ((BluetoothClientService.LocalBinder) service).getService();
			if (clientService.initialize()) {
				clientService.startForeground(appNotification.getNotificationId(), appNotification.getNotification());
				initialized = true;
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			clientService.stopForeground(false);
		}
	};

	private class AutoConnectThread extends Thread {
		private Device device;

		public AutoConnectThread(Device device) {
			this.device = device;
		}

		@Override
		public void run() {
			while (!initialized) {
				doSleep(1000);
			}
			if(clientService == null)
				return;
			BluetoothDevice myDevice = clientService.scan(device);
			if (myDevice == null)
				return;
			clientService.connect(myDevice);
		}
	}

	public Device autoWatchConnect() {
		final Device device = DeviceConfiguration.getCurrentDevice();
		if (device == null)
			return null;

		Context context = serviceActivity.getApplicationContext();
		Toast.makeText(context, context.getString(R.string.autoconnect_start) + " " + device.getName(), Toast.LENGTH_LONG).show();
		new AutoConnectThread(device).start();
		return device;
	}

	public void stopAutoWatchConnect() {
		if( clientService == null )
			return;
		clientService.stopScan();
	}

	public BluetoothClientManager(Activity serviceActivity, StatusBarNotificationManager appNotification) {
		this.appNotification = appNotification;
		this.serviceActivity = serviceActivity;
		Intent gattServiceIntent = new Intent(serviceActivity, BluetoothClientService.class);
		serviceActivity.bindService(gattServiceIntent, serviceConnection, Activity.BIND_AUTO_CREATE);
	}

	public void connect(Device device) {
		if (device == null || !initialized)
			return;
		clientService.connect(device);
	}

	public void reconnect() {
		if (!initialized)
			return;

		clientService.reconnect();
	}

	public void disconnect() {
		if (clientService != null && initialized) {
			clientService.disconnect();
		}
	}

	public boolean isConnected() {
		if (clientService == null)
			return false;
		return clientService.isConnected();
	}

	public BluetoothClientService getInternalBleService() {
		return clientService;
	}

	public void destroy() {
		serviceActivity.unbindService(serviceConnection);
		clientService.close();
		clientService = null;
	}

	private void doSleep(int milliseconds) {
		try {
			Thread.sleep(500);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
