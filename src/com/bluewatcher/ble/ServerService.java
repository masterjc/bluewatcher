package com.bluewatcher.ble;

import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

/**
 * @version $Revision$
 */
public interface ServerService extends Service {
	void serverConnected();
	void serverDisconnected();
	Set<UUID> getKnownCharacteristics();
	boolean knowsCharacteristic(BluetoothGattCharacteristic characteristic);
	BluetoothGattService createService();
	byte[] processReadRequest(BluetoothGattCharacteristic characteristic);
	byte[] processWriteRequest(BluetoothGattCharacteristic characteristic, int offset, byte[] writtenData);
}
