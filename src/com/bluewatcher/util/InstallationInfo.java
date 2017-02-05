package com.bluewatcher.util;

import android.app.Activity;

import com.bluewatcher.config.ConfigurationManager;

/**
 * @version $Revision$
 */
public class InstallationInfo {
	private final static String FIRST_EXECUTION_DONE = "FIRST_CONNECTION_DONE";
	private final static String EXECUTING_VERSION = "EXECUTING_VERSION";
	private final static String CONSENT_LOLLIPOP_PROBLEMS = "CONSENT_LOLLIPOP_PROBLEMS";
	
	public static boolean isFirstConnection() {
		String firstDone = ConfigurationManager.getInstance().load(FIRST_EXECUTION_DONE, "false");
		return firstDone.equals("false");
	}
	
	public static boolean isVersionFirstExecution(Activity activity) {
		String installedVersion = ConfigurationManager.getInstance().load(EXECUTING_VERSION, "1.0");
		String version = activity.getString(com.bluewatcher.R.string.app_version);
		return !version.equals(installedVersion);
	}
	
	public static void setFirstConnectionDone() {
		ConfigurationManager.getInstance().save(FIRST_EXECUTION_DONE, "true");
	}
	
	public static Boolean consentLollipopProblems() {
		String consent = ConfigurationManager.getInstance().load(CONSENT_LOLLIPOP_PROBLEMS, "false");
		return consent.equals("true");
	}
	
	public static void setLollipopProblemsConsent() {
		ConfigurationManager.getInstance().save(CONSENT_LOLLIPOP_PROBLEMS, "true");
	}
	
	public static void updateVersion(Activity activity) {
		String version = activity.getString(com.bluewatcher.R.string.app_version);
		ConfigurationManager.getInstance().save(EXECUTING_VERSION, version);
	}
}
