package com.bluewatcher.control;

import com.google.android.vending.licensing.util.Base64;

/**
 * @version $Revision$
 */
public class DefaultButtonConverter implements ButtonConverter {
	private static final String BUTTON_A_CODE = "GDI=";
	private static final String BUTTON_B_CODE = "GDM=";
	private static final String BUTTON_C_CODE = "GDE=";
	
	@Override
	public Button getButton(byte[] buttonId) {
		String pressedButton = Base64.encode(buttonId);
		if( pressedButton.equals(BUTTON_A_CODE))
			return Button.BUTTON_A;
		if( pressedButton.equals(BUTTON_B_CODE))
			return Button.BUTTON_B;
		if( pressedButton.equals(BUTTON_C_CODE))
			return Button.BUTTON_C;
		return null;
	}
}
