package com.bluewatcher.app.whatsapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bluewatcher.Notification;
import com.bluewatcher.app.WatcherApp;
import com.bluewatcher.app.whatsapp.WhatsappNotification.SenderType;
import com.bluewatcher.service.client.AlertService;

/**
 * @version $Revision$
 */
public class WhatsappApp implements WatcherApp {
	private final static String TAG = WhatsappApp.class.getSimpleName();

	private AlertService alertService;
	private WhatsappConfig config;
	private WhatsappAlgorithm algorithm;

	public WhatsappApp(AlertService alertService) {
		this.alertService = alertService;
	}

	public void applyConfig(WhatsappConfig config) {
		synchronized (this) {
			this.config = config;
			if (config.getAlgorithm().equals(DelayedMessageAlgorithm.class.getName())) {
				algorithm = new DelayedMessageAlgorithm(Integer.parseInt(config.getDelayTime()), alertService);
			}
			else if (config.getAlgorithm().equals(AfterReadAlgorithm.class.getName())) {
				algorithm = new AfterReadAlgorithm(alertService);
			}
			else {
				algorithm = new NotifyAllAlgorithm(alertService);
			}
		}
	}

	@Override
	public String getName() {
		return "com.whatsapp";
	}

	@Override
	public String getAction() {
		return null;
	}

	@Override
	public void manage(Context context, Intent intent) {
	}

	@Override
	public void manage(Context context, StatusBarNotificationAction sbnAction, Notification sbn) {
		if (config == null)
			return;
		String text = sbn.getTickerText();
		if (text == null)
			return;

		Log.d(TAG, "manage::Notification Text(" + sbn.getTickerText() + ") - Id(" + sbn.getId() + ") - Action(" + sbnAction.name() + ") - Filter("
				+ config.getFilter() + ")");
		
		WhatsappNotification notification = new WhatsappNotification();

		if (config.getFilter() == null || config.getFilter().isEmpty() || !text.startsWith(config.getFilter())) {
			if(text.contains("@")) {
				notification.setSenderType(SenderType.GROUP);
			} else {
				notification.setSenderType(SenderType.CONTACT);
			}
			notification.setSenderId(text);
			notification.setId(sbn.getId());
			notification.setAction(sbnAction);
			algorithm.notify(notification);
			return;
		}
					
		if (text.contains("@")) {
			if (!config.isNotifyGroups())
				return;
			notification.setSenderType(SenderType.GROUP);

			int separatorPos = text.indexOf("@");
			String subject = text.substring(config.getFilter().length(), separatorPos);
			String group = text.substring(separatorPos, text.length() - 1);
			notification.setSenderId(subject.trim());
			notification.setGroupId(group.trim());
		}
		else {
			notification.setSenderType(SenderType.CONTACT);
			String subject = text.substring(config.getFilter().length(), text.length());
			notification.setSenderId(subject.trim());
		}

		notification.setId(sbn.getId());
		notification.setAction(sbnAction);

		algorithm.notify(notification);
	}
	
	@Override
	public boolean isAvailable() {
		return alertService.isAvailable() && config != null;
	}

}
