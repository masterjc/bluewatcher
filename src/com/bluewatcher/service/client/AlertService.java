package com.bluewatcher.service.client;

import com.bluewatcher.ble.Service;

/**
 * @version $Revision$
 */
public interface AlertService extends Service {
	void notifyWatch(int notificationId, String message);
	void cleanMessage( String message );
	boolean needCleanMessage();
}
