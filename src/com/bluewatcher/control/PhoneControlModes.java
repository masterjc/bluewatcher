package com.bluewatcher.control;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @version $Revision$
 */
@SuppressWarnings("serial")
public class PhoneControlModes extends ArrayList<PhoneControlMode> implements Serializable {
	public List<String> getControlModesNames() {
		List<String> modes = new ArrayList<String>();
		for( PhoneControlMode mode : this ) {
			modes.add(mode.getName());
		}
		return modes;
	}
}