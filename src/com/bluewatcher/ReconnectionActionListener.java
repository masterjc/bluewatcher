package com.bluewatcher;

import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.bluewatcher.activity.SettingsActivity;
import com.bluewatcher.ble.BluetoothClientManager;
import com.bluewatcher.config.ConfigurationManager;
import com.bluewatcher.util.BwToast;

/**
 * @version $Revision$
 */
public class ReconnectionActionListener implements GattActionListener {
	private Activity activity;
	private BluetoothClientManager connectionService;
	private boolean notPaired = false;

	public ReconnectionActionListener(Activity activity, BluetoothClientManager connectionService) {
		this.connectionService = connectionService;
		this.activity = activity;
	}

	@Override
	public void actionGattConnected(Device deviceName) {
		notPaired = false;
	}

	@Override
	public void actionGattDisconnected(Device device) {
		if (notPaired)
			return;
		boolean showMessage = Boolean.parseBoolean(ConfigurationManager.getInstance().load(SettingsActivity.RECONNECT_MESSAGE_CONFIG,
				Boolean.toString(true)));
		if (showMessage) {
			Context context = activity.getApplicationContext();
			BwToast.getInstance().longShow(context, context.getString(R.string.reconnecting) + " " + device.getName());
		}
		connectionService.reconnect();
	}

	@Override
	public void actionGattServicesDiscovered(Device deviceName) {
	}

	@Override
	public void notPairedDevice(Device deviceName) {
		notPaired = true;
	}
	
	@Override
	public void actionCharacteristicChanged(Device deviceName, UUID characteristic) {
	}

}
