package com.bluewatcher.control;

import java.io.Serializable;

/**
 * @version $Revision$
 */
@SuppressWarnings("serial")
public class Command implements Serializable {
	private String clazz;
	private String method;
	
	public Command(String clazz, String method) {
		super();
		this.clazz = clazz;
		this.method = method;
	}
	
	public String getClazz() {
		return clazz;
	}
	
	public String getMethod() {
		return method;
	}
	
	public void reset( Command newCommand ) {
		this.clazz = newCommand.getClazz();
		this.method = newCommand.getMethod();
	}
}
