package com.bluewatcher.app.whatsapp;

import android.util.Log;

import com.bluewatcher.app.WatcherApp.StatusBarNotificationAction;
import com.bluewatcher.ble.NotificationTypeConstants;
import com.bluewatcher.service.client.AlertService;

/**
 * @version $Revision$
 */
public class NotifyAllAlgorithm implements WhatsappAlgorithm {
	private final static String TAG = NotifyAllAlgorithm.class.getSimpleName();

	private AlertService alertService;

	public NotifyAllAlgorithm(AlertService alertService) {
		this.alertService = alertService;
	}

	@Override
	public void notify(WhatsappNotification notification) {
		if (!notification.getAction().equals(StatusBarNotificationAction.ADDED))
			return;
		String message = WhatsappMessageCreator.create(notification);
		Log.d(TAG, "NotifyAllAlgorithm::notify - Message(" + message + ")");

		alertService.notifyWatch(NotificationTypeConstants.MAIL_NOTIFICATION_ID, message);
	}

}
