/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bluewatcher.ble;

import java.util.List;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Service for managing connection and data communication with a GATT server hosted on a given
 * Bluetooth LE device.
 */
public class BluetoothServerService extends Service {
	private final static String TAG = BluetoothServerService.class.getSimpleName();

	private BluetoothManager mBluetoothManager;
	private BluetoothGattServer server;
	private List<ServerService> serverServices;

	private final BluetoothGattServerCallback mBluetoothGattServerCallback = new BluetoothGattServerCallback() {
		@Override
		public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
			Log.i("BLEMainActivity:BluetoothGattServerCallback:onConnectionStateChange", "device : " + device + " status : " + status
					+ " new state : " + newState);
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				for (ServerService service : serverServices) {
					service.serverConnected();
				}
			}
			else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				for (ServerService service : serverServices) {
					service.serverDisconnected();
				}
			}
		}

		@Override
		public void onServiceAdded(int status, BluetoothGattService service) {
			Log.i("BLEMainActivity:BluetoothGattServerCallback:onServiceAdded", "service : " + service.getUuid() + " status = " + status);
		}

		@Override
		public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
			Log.i("BLEMainActivity:BluetoothGattServerCallback:onCharacteristicReadRequest", "device : " + device.getAddress() + " request = "
					+ requestId + " offset = " + offset + " characteristic = " + characteristic.getUuid());
			for (ServerService service : serverServices) {
				if (service.knowsCharacteristic(characteristic)) {
					byte[] response = service.processReadRequest(characteristic);
					server.sendResponse(device, requestId, 0, 0, response);
				}
			}
		}

		@Override
		public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic,
				boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
			super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
			Log.i("BLEMainActivity:BluetoothGattServerCallback:onCharacteristicWriteRequest", "device : " + device.getAddress()
					+ " characteristic : " + characteristic.getUuid() + "Value = " + value.toString());
			for (ServerService service : serverServices) {
				if (service.knowsCharacteristic(characteristic)) {
					service.processWriteRequest(characteristic, offset, value);
				}
			}
		}

		@Override
		public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
			Log.i("BLEMainActivity:BluetoothGattServerCallback:onDescriptorReadRequest", "device : " + device.getAddress() + " request = "
					+ requestId + " offset = " + offset + " descriptor = " + descriptor.getUuid());
		}

		@Override
		public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite,
				boolean responseNeeded, int offset, byte[] value) {
			Log.i("BLEMainActivity:BluetoothGattServerCallback:onDescriptorWriteRequest", "device : " + device.getAddress() + " \n descriptor : "
					+ descriptor.getUuid());
		}

		@Override
		public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
			Log.i("BLEMainActivity:BluetoothGattServerCallback:onExecuteWrite", "device : " + device.getAddress() + " request = " + requestId
					+ " execute = " + true);
		}
	};

	public class LocalBinder extends Binder {
		public BluetoothServerService getService() {
			return BluetoothServerService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		close();
		return super.onUnbind(intent);
	}

	private final IBinder mBinder = new LocalBinder();

	public boolean initialize(Activity activity, List<ServerService> serverServices) {
		this.serverServices = serverServices;
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				Log.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
		}

		close();
		server = mBluetoothManager.openGattServer(activity.getApplicationContext(), mBluetoothGattServerCallback);
		if( server == null )
			return false;
		for (ServerService service : serverServices) {
			BluetoothGattService serviceToAdd = service.createService();
			if (serviceToAdd != null) {
				server.addService(serviceToAdd);
			}
		}
		return true;
	}

	/**
	 * After using a given BLE device, the app must call this method to ensure resources are
	 * released properly.
	 */
	public void close() {
		if (server == null) {
			return;
		}
		server.clearServices();
		server.close();
		server = null;
	}
}
