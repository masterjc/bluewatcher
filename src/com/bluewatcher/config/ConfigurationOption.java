package com.bluewatcher.config;


public class ConfigurationOption {
	private Class<?> activityClass;
	private String description;
	private ConfigurationOptionData data;
	
	public ConfigurationOption(Class<?> activityClass, String description) {
		super();
		this.activityClass = activityClass;
		this.description = description;
	}
	
	public ConfigurationOption(Class<?> activityClass, String description, ConfigurationOptionData data) {
		super();
		this.data = data;
		this.activityClass = activityClass;
		this.description = description;
	}
	
	/**
	 * @return the activityClass
	 */
	public Class<?> getActivityClass() {
		return activityClass;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	public ConfigurationOptionData getConfigurationOptionData() {
		return data;
	}
	
	@Override
	public String toString() {
		return getDescription();
	}
}
