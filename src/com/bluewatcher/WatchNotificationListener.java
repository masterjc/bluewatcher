package com.bluewatcher;

import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

/**
 * @version $Revision$
 */
public class WatchNotificationListener extends NotificationListenerService {
	private final static String TAG = WatchNotificationListener.class.getSimpleName();

	public static final String NEW_STATUS_BAR_NOTIFICATION = "com.bluewatcher.action.NEW_STATUS_BAR_NOTIFICATION";
	public static final String REMOVED_STATUS_BAR_NOTIFICATION = "com.bluewatcher.action.REMOVED_STATUS_BAR_NOTIFICATION";
	public static final String EXTRA_NOTIFICATION = "notification";

	@Override
	public void onNotificationPosted(StatusBarNotification arg0) {
		Log.d(TAG, "onNotificationPosted: " + arg0.getPackageName());
		Intent intent = new Intent(NEW_STATUS_BAR_NOTIFICATION);
		Notification notification = createParcelableNotification(arg0);
		if (notification == null)
			return;
		intent.putExtra(EXTRA_NOTIFICATION, notification);
		sendBroadcast(intent);
	}

	@Override
	public void onNotificationRemoved(StatusBarNotification arg0) {
		Log.d(TAG, "onNotificationRemoved: " + arg0.getPackageName());
		Intent intent = new Intent(REMOVED_STATUS_BAR_NOTIFICATION);
		Notification notification = createParcelableNotification(arg0);
		if (notification == null)
			return;
		intent.putExtra(EXTRA_NOTIFICATION, notification);
		sendBroadcast(intent);
	}

	private Notification createParcelableNotification(StatusBarNotification sbn) {
		Notification notification = new Notification(sbn.getId());
		if (sbn.getNotification().tickerText == null || sbn.getNotification().tickerText.toString().isEmpty()) {
			Log.d(TAG, "ignoring null notification");
			return null;
		}
		if (sbn.getNotification().tickerText != null)
			notification.setTickerText(sbn.getNotification().tickerText.toString());
		if (sbn.getNotification().contentIntent.getCreatorPackage() != null)
			notification.setCreatorPackage(sbn.getNotification().contentIntent.getCreatorPackage());
		return notification;
	}
}
