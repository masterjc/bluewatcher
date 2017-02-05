package com.bluewatcher.app.whatsapp;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.bluewatcher.app.WatcherApp.StatusBarNotificationAction;
import com.bluewatcher.app.whatsapp.WhatsappNotification.SenderType;
import com.bluewatcher.ble.NotificationTypeConstants;
import com.bluewatcher.service.client.AlertService;

/**
 * @version $Revision$
 */
public class AfterReadAlgorithm implements WhatsappAlgorithm {
	private final static String TAG = AfterReadAlgorithm.class.getSimpleName();

	private Map<String, Integer> registers = new HashMap<String, Integer>();

	private AlertService alertService;

	public AfterReadAlgorithm(AlertService alertService) {
		this.alertService = alertService;
	}

	@Override
	public void notify(WhatsappNotification notification) {
		if (notification.getAction().equals(StatusBarNotificationAction.REMOVED)) {
			notificationRead(notification);
		}
		else {
			newNotification(notification);
		}
	}

	private void notificationRead(WhatsappNotification notification) {
		Integer lastNotification = null;
		String id = getCacheId(notification);
		synchronized (registers) {
			lastNotification = registers.get(id);
			Log.d(TAG, "AfterReadAlgorithm::notificationRead - notificationId(" + notification.getId() + ") - sender|group(" + id
					+ ") - lastNotification(" + lastNotification + ")");
			if (lastNotification == null)
				return;
			if (lastNotification.intValue() != notification.getId())
				return;
			registers.remove(id);
		}
	}

	private void newNotification(WhatsappNotification notification) {
		String message = WhatsappMessageCreator.create(notification);
		String id = getCacheId(notification);
		synchronized (registers) {
			Integer lastNotification = registers.get(id);
			Log.d(TAG, "AfterReadAlgorithm::newNotification - notificationId(" + notification.getId() + ") - sender|group(" + id
					+ ") - lastNotification(" + lastNotification + ")");
			if (lastNotification != null)
				return;
			registers.put(id, Integer.valueOf(notification.getId()));
		}
		Log.d(TAG, "AfterReadAlgorithm::newNotification - Message(" + message + ")");
		alertService.notifyWatch(NotificationTypeConstants.MAIL_NOTIFICATION_ID, message);
	}

	private String getCacheId(WhatsappNotification notification) {
		if (notification.getSenderType().equals(SenderType.CONTACT))
			return notification.getSenderId();
		return notification.getGroupId();
	}

}
