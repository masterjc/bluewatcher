package com.bluewatcher.control;

import com.bluewatcher.Device;

/**
 * @version $Revision$
 */
public class ButtonSelector {
	public Button getPressedButton(Device device, byte[] buttonCode) {
		ButtonConverter converter = null;
		if (device.isGBA400()) {
			converter = new Gba400ButtonConverter();
		}
		else if (device.isSTB1000()) {
			converter = new Stb1000ButtonConverter();
		}
		else if (device.isGB5600()) {
			converter = new Gb5600ButtonConverter();
		}
		else {
			converter = new DefaultButtonConverter();
		}
		return converter.getButton(buttonCode);
	}
}
