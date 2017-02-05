package com.bluewatcher.control;

import com.google.android.vending.licensing.util.Base64;

/**
 * @version $Revision$
 */
public class Gb5600ButtonConverter implements ButtonConverter {
	private static final String BUTTON_A_CODE = "EDE=";
	private static final String BUTTON_B_CODE = "EDI=";
	
	@Override
	public Button getButton(byte[] buttonId) {
		String pressedButton = Base64.encode(buttonId);
		if( pressedButton.equals(BUTTON_A_CODE))
			return Button.BUTTON_A;
		if( pressedButton.equals(BUTTON_B_CODE))
			return Button.BUTTON_B;
		return null;
	}
}
