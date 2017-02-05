package com.bluewatcher.app;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.bluewatcher.Notification;
import com.bluewatcher.app.WatcherApp.StatusBarNotificationAction;
import com.bluewatcher.ble.NotificationTypeConstants;
import com.bluewatcher.service.client.AlertService;

public class MissedNotifications {
	private static final int MILLISECONDS_BETWEEN_NOTIFICATIONS = 6000;
	private static final int MILLISECONDS_BEFORE_NOTIFYING = 10000;

	private class IntentContainer {
		WatcherApp watcherApp;
		Context context;
		Intent intent;

		IntentContainer(WatcherApp watcherApp, Context context, Intent intent) {
			this.context = context;
			this.intent = intent;
			this.watcherApp = watcherApp;
		}
	}

	private class SbnContainer {
		WatcherApp watcherApp;
		Context context;
		StatusBarNotificationAction sbnAction;
		Notification sbn;

		SbnContainer(WatcherApp watcherApp, Context context, StatusBarNotificationAction sbnAction, Notification sbn) {
			this.context = context;
			this.sbn = sbn;
			this.sbnAction = sbnAction;
			this.watcherApp = watcherApp;
		}
	}

	private Map<String, IntentContainer> intentEntries = new ConcurrentHashMap<String, IntentContainer>();
	private Map<String, SbnContainer> sbnEntries = new ConcurrentHashMap<String, SbnContainer>();
	private Activity activity;
	private AlertService alertService;
	
	public MissedNotifications(AlertService alertService, Activity activity) {
		this.activity = activity;
		this.alertService = alertService;
	}

	public void setMissed(WatcherApp watcherApp, Context context, Intent intent) {
		intentEntries.put(watcherApp.getName(), new IntentContainer(watcherApp, context, intent));
	}

	public void setMissed(WatcherApp watcherApp, Context context, StatusBarNotificationAction sbnAction, Notification sbn) {
		sbnEntries.put(watcherApp.getName(), new SbnContainer(watcherApp, context, sbnAction, sbn));
	}

	public void manageAll() {
		(new Thread() {
			public void run() {
				if (intentEntries.size() == 0 && sbnEntries.size() == 0)
					return;
				snooze(MILLISECONDS_BEFORE_NOTIFYING);
				if( alertService.isAvailable()) {
					alertService.notifyWatch(NotificationTypeConstants.SNS_NOTIFICATION_ID, activity.getApplicationContext().getString(com.bluewatcher.R.string.missed_notifications));
					intentEntries.clear();
					sbnEntries.clear();
				}
				//manageIntentContainer();
				//manageSbnContainer();
			}
		}).start();
	}

	@SuppressWarnings("unused")
	private void manageIntentContainer() {
		boolean first = true;
		for (IntentContainer entry : intentEntries.values()) {
			if (!entry.watcherApp.isAvailable())
				continue;
			if (first) {
				first = false;
			}
			else {
				snooze(MILLISECONDS_BETWEEN_NOTIFICATIONS);
			}
			intentEntries.remove(entry.watcherApp.getName());
			entry.watcherApp.manage(entry.context, entry.intent);
		}
	}

	@SuppressWarnings("unused")
	private void manageSbnContainer() {
		boolean first = true;
		for (SbnContainer entry : sbnEntries.values()) {
			if (!entry.watcherApp.isAvailable())
				continue;
			if (first) {
				first = false;
			}
			else {
				snooze(MILLISECONDS_BETWEEN_NOTIFICATIONS);
			}
			intentEntries.remove(entry.watcherApp.getName());
			entry.watcherApp.manage(entry.context, entry.sbnAction, entry.sbn);
		}
	}

	private void snooze(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
