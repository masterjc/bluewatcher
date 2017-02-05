package com.bluewatcher.app.generic;

import java.util.List;

/**
 * @version $Revision$
 */
public interface GenericNotificationConfig {
	boolean isGenericAppsEnabled();
	List<GenericAppConfig> getGenericAppConfigurations();
}
