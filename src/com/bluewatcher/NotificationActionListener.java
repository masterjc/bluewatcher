package com.bluewatcher;

import android.content.Context;

/**
 * @version $Revision$
 */
public interface NotificationActionListener {
	void newStatusBarNotification(Context context, Notification notification);
	void removedStatusBarNotification(Context context, Notification notification);
}
