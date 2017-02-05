package com.bluewatcher.control;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @version $Revision$
 */
@SuppressWarnings("serial")
public class PhoneControlMode implements Serializable {
	private String name;
	private Map<Button, Command> commands;
	
	public PhoneControlMode(String name) {
		this.name = name;
		this.commands = new HashMap<Button, Command>();
	}
	
	public PhoneControlMode() {
		this.commands = new HashMap<Button, Command>();
	}
	
	public void add( Button b, Command command ) {
		commands.put(b, command);
	}
	
	public String getName() {
		return name;
	}
	
	public Command getCommand(Button but) {
		return commands.get(but);
	}
	
	public Set<Button> getButtons() {
		return commands.keySet();
	}

	public void setName(String name) {
		this.name = name;
	}
}
