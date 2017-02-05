package com.bluewatcher.app.whatsapp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.bluewatcher.app.whatsapp.WhatsappNotification.SenderType;
import com.bluewatcher.ble.NotificationTypeConstants;
import com.bluewatcher.service.client.AlertService;

/**
 * @version $Revision$
 */
public class DelayedMessageAlgorithm implements WhatsappAlgorithm {
	private final static String TAG = DelayedMessageAlgorithm.class.getSimpleName();

	private int delayTimeMin;
	private Map<String, Date> registers = new HashMap<String, Date>();

	private AlertService alertService;

	public DelayedMessageAlgorithm(int delayTimeMin, AlertService alertService) {
		this.alertService = alertService;
		this.delayTimeMin = delayTimeMin;
	}
	
	public int getDelayTime() {
		return delayTimeMin;
	}

	@Override
	public void notify(WhatsappNotification notification) {
		String message = WhatsappMessageCreator.create(notification);
		String id = getCacheId(notification);
		synchronized (registers) {
			Date lastNotification = registers.get(id);
			Date now = new Date();
			Log.d(TAG,
					"DelayedMessageAlgorithm::notify - notificationId(" + notification.getId() + ") - sender|group(" + id + ") - now("
							+ now.getTime() + ") - lastNotification(" + lastNotification.getTime() + ") - delayTime(" + this.delayTimeMin + ")");
			if (lastNotification != null && now.getTime() < lastNotification.getTime() + (delayTimeMin * 60000)) {
				Log.d(TAG, "DelayedMessageAlgorithm::notify - Not reached delay time!");
				return;
			}
			registers.put(id, now);
		}
		Log.d(TAG, "DelayedMessageAlgorithm::notify - Message(" + message + ")");
		alertService.notifyWatch(NotificationTypeConstants.MAIL_NOTIFICATION_ID, message);
	}

	private String getCacheId(WhatsappNotification notification) {
		if (notification.getSenderType().equals(SenderType.CONTACT))
			return notification.getSenderId();
		return notification.getGroupId();
	}
}
