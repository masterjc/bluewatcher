package com.bluewatcher.config;

import com.bluewatcher.Device;

/**
 * @version $Revision$
 */
public class DeviceConfiguration {
	private static final String DEVICE_ADDRESS = "DEVICE_ADDRESS";
	private static final String DEVICE_NAME = "DEVICE_NAME";
	public static Device getCurrentDevice() {
		String address = ConfigurationManager.getInstance().load(DEVICE_ADDRESS, null);
		String name = ConfigurationManager.getInstance().load(DEVICE_NAME, null);
		if( address == null || name == null )
			return null;
		return new Device(address, name);
	}
	
	public static void saveCurrentDevice(Device device) {
		ConfigurationManager.getInstance().save(DEVICE_ADDRESS, device.getAddress());
		ConfigurationManager.getInstance().save(DEVICE_NAME, device.getName());
	}
}
