package com.bluewatcher.app.call;

/**
 * @version $Revision$
 */
public class IncomingCallConfig {
	private boolean appEnabled;
	private boolean resolveContacts;
	
	/**
	 * @return the appEnabled
	 */
	public boolean isAppEnabled() {
		return appEnabled;
	}
	/**
	 * @param appEnabled the appEnabled to set
	 */
	public void setAppEnabled(boolean appEnabled) {
		this.appEnabled = appEnabled;
	}
	/**
	 * @return the resolveContacts
	 */
	public boolean isResolveContacts() {
		return resolveContacts;
	}
	/**
	 * @param resolveContacts the resolveContacts to set
	 */
	public void setResolveContacts(boolean resolveContacts) {
		this.resolveContacts = resolveContacts;
	}	
}
