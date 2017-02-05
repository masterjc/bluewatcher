package com.bluewatcher.ble;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.bluewatcher.Device;

public class BluetoothClientService extends Service {
	
	private class GattContainer {
		private BluetoothGattCharacteristic charac;
		private BluetoothGattDescriptor desc;
		
		GattContainer(BluetoothGattCharacteristic charac ) {
			this.charac = charac;
		}
		
		GattContainer(BluetoothGattDescriptor desc ) {
			this.desc = desc;
		}

		public BluetoothGattCharacteristic getCharac() {
			return charac;
		}

		public BluetoothGattDescriptor getDesc() {
			return desc;
		}	
	}
	
	private final static String TAG = BluetoothClientService.class.getSimpleName();

	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothGatt mBluetoothGatt;
	private boolean connected = false; 

	public final static String ACTION_GATT_CLIENT_CONNECTED = "com.bluewatcher.ACTION_GATT_CLIENT_CONNECTED";
	public final static String ACTION_NOT_PAIRED = "com.bluewatcher.ACTION_NOT_PAIRED";
	public final static String ACTION_GATT_CLIENT_DISCONNECTED = "com.bluewatcher.ACTION_GATT_CLIENT_DISCONNECTED";
	public final static String ACTION_GATT_CLIENT_SERVICES_DISCOVERED = "com.bluewatcher.ACTION_GATT_CLIENT_SERVICES_DISCOVERED";
	public final static String ACTION_CHARACTERISTIC_CHANGED = "com.bluewatcher.ACTION_CHARACTERISTIC_CHANGED";
	public final static String ACTION_DATA_AVAILABLE = "com.bluewatcher.ACTION_DATA_AVAILABLE";
	public final static String CLIENT_EXTRA_DATA = "com.bluewatcher.EXTRA_DATA";
	public final static String EXTRA_CHARACTERISTIC_UUID = "com.bluewatcher.EXTRA_CHARACTERISTIC_UUID";
	
	private BluetoothDevice foundDevice;
	private boolean lookingFor = false;
	private LeScanCallback mLeScanCallback;
	
	private List<GattContainer> characteristics = new ArrayList<GattContainer>();
	private boolean running = true;
	
	private class BlueWatcherScanCallback implements LeScanCallback {
		private Device device;
		BlueWatcherScanCallback(Device device) {
			this.device = device;
		}
		
		@Override
		public void onLeScan(BluetoothDevice newDevice, int rssi, byte[] scanRecord) {
			Log.i(TAG, "FOUND " + newDevice.getAddress() + ". COMPARE WITH: " + device.getAddress());
			if( newDevice.getAddress().equals(device.getAddress())) {
				
				foundDevice = newDevice;
				mBluetoothAdapter.stopLeScan(this);
			}
		}
	}
	
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			String intentAction;
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				Log.i(TAG, "Connected to GATT server.");
				if( gatt.getDevice().getBondState() != BluetoothDevice.BOND_BONDED ) {
					broadcastUpdate(ACTION_NOT_PAIRED);
					gatt.disconnect();
					return;
				}
				Log.i(TAG, "Attempting to start service discovery" );
				mBluetoothGatt.discoverServices();
				connected = true;
				broadcastUpdate(ACTION_GATT_CLIENT_CONNECTED);
			}
			else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				intentAction = ACTION_GATT_CLIENT_DISCONNECTED;
				Log.i(TAG, "Disconnected from GATT server.");
				broadcastUpdate(intentAction);
				connected = false;
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			Log.i(TAG, "onServicesDiscovered called...");
			BluetoothDevice device = gatt.getDevice();
			int discoveredServices = gatt.getServices().size();
			if(discoveredServices == 0 && isConnected(device)) {
				Log.i(TAG, "Re-discovering services. No previous services and is connected");
				gatt.discoverServices();
				return;
			}
			broadcastUpdate(ACTION_GATT_CLIENT_SERVICES_DISCOVERED);
		}
		
		private boolean isConnected(BluetoothDevice device) {
			List<BluetoothDevice> connectedDevices = ((BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE)).getConnectedDevices(7);
			for(BluetoothDevice d : connectedDevices) {
				Log.d(TAG, "Connected device: " + d.getName());
			}
			return connectedDevices.contains(device);
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			Log.i(TAG, "onCharacteristicRead called...");
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
			Log.i(TAG, "onCharacteristicChanged called..." + characteristic.getUuid().toString());
			broadcastUpdate(ACTION_CHARACTERISTIC_CHANGED, characteristic);
		}
		
		public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			Log.i(TAG, "onCharacteristicWrite called..." + characteristic.getUuid().toString());
		};
		
		public void onDescriptorRead(BluetoothGatt gatt, android.bluetooth.BluetoothGattDescriptor descriptor, int status) {
			Log.i(TAG, "onDescriptorRead called..." + descriptor.getUuid().toString());
		};
		
		public void onDescriptorWrite(BluetoothGatt gatt, android.bluetooth.BluetoothGattDescriptor descriptor, int status) {
			Log.i(TAG, "onDescriptorWrite called..." + descriptor.getUuid().toString());
		};
	};
	
	public synchronized BluetoothDevice scan(Device device) {
		foundDevice = null;
		lookingFor = true;
		mLeScanCallback = new BlueWatcherScanCallback(device);
		mBluetoothAdapter.startLeScan(mLeScanCallback);
		while(foundDevice == null && lookingFor) {
			doSleep(1000);
		}
		mBluetoothAdapter.stopLeScan(mLeScanCallback);
		mLeScanCallback = null;
		lookingFor = false;
		BluetoothDevice toReturn = foundDevice;
		foundDevice = null;
		if(toReturn != null)
			return toReturn;
		return null;
	}
	
	public void stopScan() {
		lookingFor = false;
	}
	
	public void writeCharacteristic( BluetoothGattCharacteristic characteristic ) {
		synchronized (characteristics) {
			characteristics.add(new GattContainer(characteristic));
		}
	}
	
	public void writeDescriptor( BluetoothGattDescriptor desc ) {
		synchronized (characteristics) {
			characteristics.add(new GattContainer(desc));
		}
	}
	
	public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean active) {
		if(mBluetoothGatt == null)
			return;
		mBluetoothGatt.setCharacteristicNotification(characteristic, active);
	}
	
	class GattClientThread extends Thread {
    
        public void run() {
            while( running ) {
            	if( characteristics.size() > 0 ) {
            		synchronized (characteristics) {
            			GattContainer charac = characteristics.get(0);
            			doSleep(100);
            			if(mBluetoothGatt == null)
            				continue;
            			if( charac.getCharac() != null ) {
            				Log.i(TAG, "Writing Characteristic" + charac.getCharac().getUuid().toString());
            				mBluetoothGatt.writeCharacteristic(charac.getCharac());
            			} else {
            				Log.i(TAG, "Writing Descriptor" + charac.getDesc().getUuid().toString());
            				mBluetoothGatt.writeDescriptor(charac.getDesc());
            			}
            			characteristics.remove(0);
					}
            	} else {
            		doSleep(400);
            	}
            }
        }
    }
	

	private void broadcastUpdate(final String action) {
		final Intent intent = new Intent(action);
		sendBroadcast(intent);
	}

	public boolean isConnected() {
		return connected;
	}

	private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
		final Intent intent = new Intent(action);
		intent.putExtra(EXTRA_CHARACTERISTIC_UUID, characteristic.getUuid().toString());
		final byte[] data = characteristic.getValue();
		if (data != null && data.length > 0) {
			final StringBuilder stringBuilder = new StringBuilder(data.length);
			for (byte byteChar : data)
				stringBuilder.append(String.format("%02X ", byteChar));
			intent.putExtra(CLIENT_EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
		}
		sendBroadcast(intent);
	}

	public class LocalBinder extends Binder {
		public BluetoothClientService getService() {
			return BluetoothClientService.this;
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

	public boolean initialize() {
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				Log.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}
		
		new GattClientThread().start();

		return true;
	}

	public boolean connect(Device deviceToConnect) {
		if (mBluetoothAdapter == null || deviceToConnect == null) {
			Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
			return false;
		}
		
		disconnect();

		final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceToConnect.getAddress());
		if (device == null)
			return false;

		connect(device);
		return true;
	}
	
	public void connect(BluetoothDevice device) {
		Log.d(TAG, "Trying to create a new connection. " + device.getAddress().toString());
		mBluetoothGatt = device.connectGatt(this, true, mGattCallback);
	}


	public boolean reconnect() {
		if (mBluetoothGatt == null || mBluetoothAdapter == null) {
			Log.w(TAG, "Reconnection info is not set");
			return false;
		}
		
		return mBluetoothGatt.connect();
	}

	public void disconnect() {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.disconnect();
	}

	public void close() {
		running = false;
		if (mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}

	public List<BluetoothGattService> getSupportedGattServices() {
		if (mBluetoothGatt == null)
			return null;

		return mBluetoothGatt.getServices();
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
