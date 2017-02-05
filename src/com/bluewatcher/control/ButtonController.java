package com.bluewatcher.control;

/**
 * @version $Revision$
 */
public class ButtonController {
	public static void buttonPressed(ControlSchema controlSchema, Button button) {
		if (button.equals(Button.BUTTON_A)) {
			controlSchema.buttonA();
		}
		else if (button.equals(Button.BUTTON_B)) {
			controlSchema.buttonB();
		}
		else if (button.equals(Button.BUTTON_C)) {
			controlSchema.buttonC();
		}
		else if (button.equals(Button.BUTTON_D)) {
			controlSchema.buttonD();
		}
		else if (button.equals(Button.BUTTON_E)) {
			controlSchema.buttonE();
		}
		else if (button.equals(Button.WHEEL_UP)) {
			controlSchema.wheelUp();
		}
		else if (button.equals(Button.WHEEL_DOWN)) {
			controlSchema.wheelDown();
		}
	}
}
