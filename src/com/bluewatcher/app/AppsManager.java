package com.bluewatcher.app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bluewatcher.Device;
import com.bluewatcher.GattActionListener;
import com.bluewatcher.GenericActionListener;
import com.bluewatcher.Notification;
import com.bluewatcher.NotificationActionListener;
import com.bluewatcher.app.WatcherApp.StatusBarNotificationAction;
import com.bluewatcher.app.generic.GenericApp;

/**
 * @version $Revision$
 */
public class AppsManager implements NotificationActionListener, GenericActionListener, GattActionListener {
	private final static String TAG = AppsManager.class.getSimpleName();
	private Map<String, WatcherApp> activeApplications = new ConcurrentHashMap<String, WatcherApp>();
	private List<GenericApp> genericApplications = new ArrayList<GenericApp>();
	private MissedNotifications missedNotifications;
	
	public AppsManager() {
	}
	
	public AppsManager(MissedNotifications missedNotifications) {
		this.missedNotifications = missedNotifications;
	}

	public void manage(Context context, Intent intent) {
		Log.d(TAG, "Trying to manage Intent: " + intent.getPackage());
		for (String key : activeApplications.keySet()) {
			WatcherApp app = activeApplications.get(key);
			if (app.getAction() != null && intent.getAction().equals(app.getAction())) {
				Log.d(TAG, "Managing Intent: " + intent.getPackage() + " -> " + app.getName());
				if( app.isAvailable() ) {
					app.manage(context, intent);
				} else {
					missedNotifications.setMissed(app, context, intent);
				}
				return;
			}
		}
		synchronized (genericApplications) {
			Iterator<GenericApp> it = genericApplications.iterator();
			while (it.hasNext()) {
				GenericApp app = it.next();
				if (app.getAction() != null && intent.getAction().equals(app.getAction())) {
					Log.d(TAG, "Managing Generic Intent: " + intent.getPackage() + " -> " + app.getName());
					if( app.isAvailable() ) {
						app.manage(context, intent);
					} else {
						missedNotifications.setMissed(app, context, intent);
					}
					return;
				}
			}
		}
	}

	public void manage(Context context, StatusBarNotificationAction sbnAction, Notification sbn) {
		synchronized (genericApplications) {
			Log.d(TAG, "Trying to manage StatusBarNotificationAction: " + sbnAction);
			String appName = sbn.getCreatorPackage();
			if (appName == null)
				return;
			WatcherApp app = activeApplications.get(appName);
			if (app == null) {
				Iterator<GenericApp> it = genericApplications.iterator();
				while (it.hasNext()) {
					GenericApp genApp = it.next();
					if (genApp.getName().equals(appName)) {
						app = genApp;
						break;
					}
				}
			}
			if (app == null)
				return;
			Log.d(TAG, "Managing StatusBarNotification: " + appName + " -> " + app.getName());
			if( app.isAvailable() ) {
				app.manage(context, sbnAction, sbn);
			} else {
				missedNotifications.setMissed(app, context, sbnAction, sbn);
			}
			
		}
	}

	public List<String> getRegisteredActions() {
		synchronized (genericApplications) {
			List<String> toRet = new ArrayList<String>();
			for (String key : activeApplications.keySet()) {
				WatcherApp app = activeApplications.get(key);
				if (app.getAction() != null) {
					toRet.add(app.getAction());
				}
			}
			Iterator<GenericApp> it = genericApplications.iterator();
			while (it.hasNext()) {
				GenericApp app = it.next();
				if (app.getAction() != null) {
					toRet.add(app.getAction());
				}
			}
			return toRet;
		}
	}

	public List<GenericApp> getGenericApplications() {
		return this.genericApplications;
	}

	public void register(WatcherApp app) {
		activeApplications.put(app.getName(), app);
	}

	public void registerGeneric(GenericApp app) {
		synchronized (genericApplications) {
			genericApplications.add(app);	
		}
	}

	public void unregister(String appName) {
		activeApplications.remove(appName);
	}

	public void unregister(WatcherApp app) {
		synchronized (genericApplications) {
			if (activeApplications.remove(app.getName()) == null) {
				Iterator<GenericApp> it = genericApplications.iterator();
				while (it.hasNext()) {
					GenericApp genApp = it.next();
					if (app.getName().equals(app.getName())) {
						genericApplications.remove(genApp);
					}
				}
			}
		}
	}

	public void clear() {
		activeApplications.clear();
		synchronized (genericApplications) {
			genericApplications.clear();
		}
	}

	public void clearGenericApplications() {
		synchronized (genericApplications) {
			genericApplications.clear();
		}
	}

	@Override
	public void genericAction(Context context, Intent intent) {
		manage(context, intent);
	}

	@Override
	public void newStatusBarNotification(Context context, Notification notification) {
		manage(context, WatcherApp.StatusBarNotificationAction.ADDED, notification);
	}

	@Override
	public void removedStatusBarNotification(Context context, Notification notification) {
		manage(context, WatcherApp.StatusBarNotificationAction.REMOVED, notification);
	}

	
	@Override
	public void actionGattConnected(Device deviceName) {
		missedNotifications.manageAll();
	}

	@Override
	public void actionGattDisconnected(Device deviceName) {
	}

	
	@Override
	public void actionGattServicesDiscovered(Device deviceName) {
	}
	
	@Override
	public void notPairedDevice(Device deviceName) {	
	}
	
	@Override
	public void actionCharacteristicChanged(Device deviceName, UUID characteristic) {
	}
}
