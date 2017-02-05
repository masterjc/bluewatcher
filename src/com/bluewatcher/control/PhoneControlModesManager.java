package com.bluewatcher.control;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.bluewatcher.Device;
import com.bluewatcher.config.ConfigurationManager;
import com.bluewatcher.config.DeviceConfiguration;
import com.bluewatcher.util.MusicController;

/**
 * @version $Revision$
 */
public class PhoneControlModesManager {
	/**
	 * Saved Data example: -------------------
	 * 
	 * TWO_BUTTON : 1
	 * 
	 * TWO_BUTTON_0_NAME : Music
	 * 
	 * TWO_BUTTON_0 : 2 *********************
	 * 
	 * TWO_BUTTON_0_BUTTON_0_CODE : BUTTON_A
	 * 
	 * TWO_BUTTON_0_BUTTON_0_CLASS : com.bluewatcher.MusicController
	 * 
	 * TWO_BUTTON_0_BUTTON_0_METHOD : togglePlayPause
	 * 
	 * TWO_BUTTON_0_BUTTON_1_CODE : BUTTON_B
	 * 
	 * TWO_BUTTON_0_BUTTON_1_CLASS : com.bluewatcher.MusicController
	 * 
	 * TWO_BUTTON_0_BUTTON_1_METHOD : next *********************
	 * 
	 * TWO_BUTTON_1_BUTTON_0_CODE : BUTTON_A
	 * 
	 * TWO_BUTTON_1_BUTTON_0_CLASS : com.bluewatcher.MusicController
	 * 
	 * TWO_BUTTON_1_BUTTON_0_METHOD : togglePlayPause
	 * 
	 * TWO_BUTTON_1_BUTTON_1_CODE : BUTTON_B
	 * 
	 * TWO_BUTTON_1_BUTTON_1_CLASS : com.bluewatcher.MusicController
	 * 
	 * TWO_BUTTON_1_BUTTON_1_METHOD : next
	 */

	private static final String DEFAULT_MUSIC_CONTROL_MODE = "Music";
	private static final String NULL_CONTENT = "NULL";
	private static final String SEPARATOR = "_";
	private static final String BUTTON_SEPARATOR = "BUTTON" + SEPARATOR;
	private static final String MODE_NAME = SEPARATOR + "NAME";
	private static final String BUTTON_CODE = SEPARATOR + "CODE";
	private static final String BUTTON_CLASS = SEPARATOR + "CLASS";
	private static final String BUTTON_METHOD = SEPARATOR + "METHOD";
	private static List<Object> controlObjects;

	public static synchronized void initialize(List<Object> objs) {
		controlObjects = objs;
	}

	public static void save(PhoneControlModes modes) {
		Device device = DeviceConfiguration.getCurrentDevice();
		if (device == null)
			return;

		ConfigurationManager.getInstance().clearAll(device.getControlConfiguration().name());

		if (modes.size() == 0)
			return;

		ConfigurationManager.getInstance().save(device.getControlConfiguration().name(), Integer.toString(modes.size()));

		int nMode = 0;
		for (PhoneControlMode mode : modes) {
			String prefix = getModePrefix(device, nMode++);

			ConfigurationManager.getInstance().save(prefix, Integer.toString(device.getControlConfiguration().getNumberButtons()));
			ConfigurationManager.getInstance().save(prefix + MODE_NAME, mode.getName());

			int nButton = 0;
			for (Button button : mode.getButtons()) {
				String buttonPrefix = prefix + SEPARATOR + BUTTON_SEPARATOR + nButton++;

				ConfigurationManager.getInstance().save(buttonPrefix + BUTTON_CODE, button.name());

				Command command = mode.getCommand(button);
				ConfigurationManager.getInstance().save(buttonPrefix + BUTTON_CLASS, command.getClazz());
				ConfigurationManager.getInstance().save(buttonPrefix + BUTTON_METHOD, command.getMethod());
			}
		}
	}

	public static PhoneControlModes load() {
		PhoneControlModes modes = new PhoneControlModes();

		Device device = DeviceConfiguration.getCurrentDevice();
		if (device == null)
			return modes;

		String nModes = ConfigurationManager.getInstance().load(device.getControlConfiguration().name(), NULL_CONTENT);

		if (nModes.equals(NULL_CONTENT)) {
			save(getDefaultPhoneControlModes(device));
			nModes = ConfigurationManager.getInstance().load(device.getControlConfiguration().name(), NULL_CONTENT);
		}

		int numberModes = Integer.parseInt(nModes);
		for (int nMode = 0; nMode < numberModes; nMode++) {
			String prefix = device.getControlConfiguration().name() + SEPARATOR + nMode;

			String modeName = ConfigurationManager.getInstance().load(prefix + MODE_NAME, NULL_CONTENT);
			PhoneControlMode mode = new PhoneControlMode(modeName);

			String nButtons = ConfigurationManager.getInstance().load(prefix, "0");
			int numberButtons = Integer.parseInt(nButtons);
			for (int nBut = 0; nBut < numberButtons; nBut++) {
				String buttonPrefix = prefix + SEPARATOR + BUTTON_SEPARATOR + nBut;
				String buttonCode = ConfigurationManager.getInstance().load(buttonPrefix + BUTTON_CODE, NULL_CONTENT);
				String buttonClass = ConfigurationManager.getInstance().load(buttonPrefix + BUTTON_CLASS, NULL_CONTENT);
				String buttonMethod = ConfigurationManager.getInstance().load(buttonPrefix + BUTTON_METHOD, NULL_CONTENT);

				if (buttonCode.equals(NULL_CONTENT) || buttonClass.equals(NULL_CONTENT) || buttonMethod.equals(NULL_CONTENT))
					continue;

				mode.add(Button.valueOf(buttonCode), new Command(buttonClass, buttonMethod));
			}

			modes.add(mode);
		}

		return modes;
	}

	public static PhoneControlModes getDefaultPhoneControlModes(Device device) {
		PhoneControlModes modes = new PhoneControlModes();

		PhoneControlMode mode = new PhoneControlMode(DEFAULT_MUSIC_CONTROL_MODE);
		if (device.isGBA400()) {
			mode.add(Button.BUTTON_A, new Command(MusicController.class.getName(), "togglePlayPause"));
			mode.add(Button.BUTTON_B, new Command(MusicController.class.getName(), "playNext"));
			mode.add(Button.BUTTON_C, new Command(MusicController.class.getName(), "playPrevious"));
			mode.add(Button.WHEEL_UP, new Command(MusicController.class.getName(), "volumeUp"));
			mode.add(Button.WHEEL_DOWN, new Command(MusicController.class.getName(), "volumeDown"));
		}
		else if (device.isSTB1000()) {
			mode.add(Button.BUTTON_A, new Command(MusicController.class.getName(), "volumeDown"));
			mode.add(Button.BUTTON_B, new Command(MusicController.class.getName(), "playNext"));
			mode.add(Button.BUTTON_C, new Command(MusicController.class.getName(), "volumeUp"));
			mode.add(Button.BUTTON_D, new Command(MusicController.class.getName(), "playPrevious"));
			mode.add(Button.BUTTON_E, new Command(MusicController.class.getName(), "togglePlayPause"));
		}
		else if (device.isGB5600()) {
			mode.add(Button.BUTTON_A, new Command(MusicController.class.getName(), "togglePlayPause"));
			mode.add(Button.BUTTON_B, new Command(MusicController.class.getName(), "playNext"));
		}
		else {
			mode.add(Button.BUTTON_A, new Command(MusicController.class.getName(), "togglePlayPause"));
			mode.add(Button.BUTTON_B, new Command(MusicController.class.getName(), "volumeUp"));
			mode.add(Button.BUTTON_C, new Command(MusicController.class.getName(), "volumeDown"));
		}
		modes.add(mode);
		return modes;
	}

	public static boolean call(Command command) {
		for (Object object : controlObjects) {
			if (object.getClass().getName().equals(command.getClazz())) {
				List<Method> methods = getSelectableMethods(object);
				for (Method method : methods) {
					if (method.getName().equals(command.getMethod())) {
						try {
							method.invoke(object, new Object[] {});
							return true;
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return false;
	}
	
	public static Command getCommand(String methodName) {
		for (Object object : controlObjects) {
			List<Method> methods = getSelectableMethods(object);
			for (Method method : methods) {
				if( method.getName().equals(methodName)) {
					return new Command(object.getClass().getName(), method.getName());
				}
			}
		}
		return null;
	}
	
	public static List<String> getCallableCommandNames() {
		List<String> commands = new ArrayList<String>();
		for (Object object : controlObjects) {
			List<Method> methods = getSelectableMethods(object);
			for (Method method : methods) {
				commands.add(method.getName());
			}
		}
		return commands;
	}

	public static List<Method> getSelectableMethods(Object object) {
		List<Method> meths = new ArrayList<Method>();
		Method[] methods = object.getClass().getDeclaredMethods();
		for (int m = 0; m < methods.length; m++) {
			if (methods[m].getAnnotation(ControlOption.class) != null) {
				meths.add(methods[m]);
			}
		}
		return meths;
	}

	private static String getModePrefix(Device device, int mode) {
		return device.getControlConfiguration().name() + SEPARATOR + Integer.toString(mode);
	}
}
