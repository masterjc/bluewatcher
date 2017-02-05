package com.bluewatcher.app.finder;

/**
 * @version $Revision$
 */
public class PhoneFinderConfig {
	private boolean appEnabled;
	private int delayTime;
	private Integer volume;
	
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
	public int getDelayTime() {
		return delayTime;
	}
	public void setDelayTime(int delayTime) {
		this.delayTime = delayTime;
	}
	public Integer getVolume() {
		return volume;
	}
	public void setVolume(int volume) {
		this.volume = volume;
	}
	
}
