package com.bluewatcher.app;

import android.content.Context;
import android.content.Intent;

import com.bluewatcher.Notification;

/**
 * @version $Revision$
 */
public interface WatcherApp {
	enum StatusBarNotificationAction {
		ADDED, REMOVED;
	}
	String getName();
	String getAction();
	boolean isAvailable();
	void manage(Context context, Intent intent);
	void manage(Context context, StatusBarNotificationAction sbnAction, Notification sbn);

}
