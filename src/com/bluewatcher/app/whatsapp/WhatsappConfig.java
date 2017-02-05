package com.bluewatcher.app.whatsapp;


/**
 * @version $Revision$
 */
public class WhatsappConfig {
	private boolean appEnabled;
	private String algorithm;
	private String delayTime;
	private boolean notifyGroups;
	private String filter;
	public boolean isAppEnabled() {
		return appEnabled;
	}
	public void setAppEnabled(boolean appEnabled) {
		this.appEnabled = appEnabled;
	}
	public String getAlgorithm() {
		return algorithm;
	}
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}
	public boolean isNotifyGroups() {
		return notifyGroups;
	}
	public void setNotifyGroups(boolean notifyGroups) {
		this.notifyGroups = notifyGroups;
	}
	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
	public String getDelayTime() {
		return delayTime;
	}
	public void setDelayTime(String delayTime) {
		this.delayTime = delayTime;
	}
	
	
	
}
