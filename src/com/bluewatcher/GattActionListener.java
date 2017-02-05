package com.bluewatcher;

import java.util.UUID;


/**
 * @version $Revision$
 */
public interface GattActionListener {
	void notPairedDevice(Device deviceName);
	void actionGattConnected(Device deviceName);
	void actionGattDisconnected(Device deviceName);
	void actionGattServicesDiscovered(Device deviceName);
	void actionCharacteristicChanged(Device deviceName, UUID characteristic);
}
