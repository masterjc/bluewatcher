package com.bluewatcher.control;

import com.google.android.vending.licensing.util.Base64;

/**
 * @version $Revision$
 */
public class Gba400ButtonConverter implements ButtonConverter {
	private static final String BUTTON_A_CODE = "CDE=";
	private static final String BUTTON_B_CODE = "CDI=";
	private static final String BUTTON_C_CODE = "CDM=";
	private static final String WHEEL_UP_CODE = "CDQ=";
	private static final String WHEEL_DOWN_CODE = "CDU=";
	
	@Override
	public Button getButton(byte[] buttonId) {
		String pressedButton = Base64.encode(buttonId);
		if( pressedButton.equals(BUTTON_A_CODE))
			return Button.BUTTON_A;
		if( pressedButton.equals(BUTTON_B_CODE))
			return Button.BUTTON_B;
		if( pressedButton.equals(BUTTON_C_CODE))
			return Button.BUTTON_C;
		if( pressedButton.equals(WHEEL_UP_CODE))
			return Button.WHEEL_UP;
		if( pressedButton.equals(WHEEL_DOWN_CODE))
			return Button.WHEEL_DOWN;
		return null;
	}
}
