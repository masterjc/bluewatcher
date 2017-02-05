package com.bluewatcher.ble;

import java.util.List;

import com.bluewatcher.StatusBarNotificationManager;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * @version $Revision$
 */
public class BluetoothServerManager {
	private BluetoothServerService mBluetoothLeService;
	private boolean initialized = false;
	private Activity activity;
	private List<ServerService> serverServices;
	private StatusBarNotificationManager appNotification;

	private final ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {
			mBluetoothLeService = ((BluetoothServerService.LocalBinder) service).getService();
			if(mBluetoothLeService.initialize(activity, serverServices)) {
				mBluetoothLeService.startForeground(appNotification.getNotificationId(), appNotification.getNotification());
				initialized = true;
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService.stopForeground(false);
		}
	};
	
	
	public BluetoothServerManager(Activity serviceActivity, StatusBarNotificationManager appNotification, List<ServerService> serverServices) {
		this.appNotification = appNotification;
		this.activity = serviceActivity;
		this.serverServices = serverServices;
    	Intent gattServiceIntent = new Intent(serviceActivity, BluetoothServerService.class);
    	serviceActivity.bindService(gattServiceIntent, serviceConnection, Activity.BIND_AUTO_CREATE);
	}
	
	public boolean isInitialized() {
		return initialized;
	}

	public void destroy() {
		activity.unbindService(serviceConnection);
		mBluetoothLeService.close();
		mBluetoothLeService = null;
	}
}
