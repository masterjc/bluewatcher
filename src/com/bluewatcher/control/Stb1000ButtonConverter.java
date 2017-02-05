package com.bluewatcher.control;

import com.google.android.vending.licensing.util.Base64;

/**
 * @version $Revision$
 */
public class Stb1000ButtonConverter implements ButtonConverter {
	private static final String BUTTON_A_CODE = "CDE=";
	private static final String BUTTON_B_CODE = "CDI=";
	private static final String BUTTON_C_CODE = "CDM=";
	private static final String BUTTON_D_CODE = "CDQ=";
	private static final String POWER_BUTTON_CODE = "CDU=";
	
	@Override
	public Button getButton(byte[] buttonId) {
		String pressedButton = Base64.encode(buttonId);
		if( pressedButton.equals(BUTTON_A_CODE))
			return Button.BUTTON_A;
		if( pressedButton.equals(BUTTON_B_CODE))
			return Button.BUTTON_B;
		if( pressedButton.equals(BUTTON_C_CODE))
			return Button.BUTTON_C;
		if( pressedButton.equals(BUTTON_D_CODE))
			return Button.BUTTON_D;
		if( pressedButton.equals(POWER_BUTTON_CODE))
			return Button.BUTTON_E;
		return null;
	}
}
