package com.bluewatcher.app.generic;

import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.bluewatcher.Notification;
import com.bluewatcher.app.WatcherApp;
import com.bluewatcher.service.client.AlertService;

/**
 * @version $Revision$
 */
public class GenericApp implements WatcherApp {
	private GenericAppConfig genericAppConfig;
	private AlertService alertService;

	public GenericApp(AlertService alertService) {
		this.alertService = alertService;
	}

	public void applyConfig(GenericAppConfig config) {
		synchronized (this) {
			this.genericAppConfig = config;
		}
	}

	@Override
	public String getName() {
		return genericAppConfig.getAppPackage();
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
		if(!isAvailable())
			return;
		synchronized (this) {
			if (sbnAction == StatusBarNotificationAction.REMOVED)
				return;

			if (genericAppConfig == null)
				return;

			StringBuffer message = new StringBuffer();
			if (genericAppConfig.getPrefix() != null && !genericAppConfig.getPrefix().isEmpty()) {
				message.append(genericAppConfig.getPrefix());
				message.append(":");
			}

			if (genericAppConfig.getLabel() != null && !genericAppConfig.getLabel().isEmpty()) {
				message.append(genericAppConfig.getLabel());
				alertService.notifyWatch(genericAppConfig.getAlertType().getAlertId(), message.toString());
				return;
			}

			String matchingFilter = null;
			List<String> filters = genericAppConfig.getMessageFilters();
			for (String filter : filters) {
				if (sbn.getTickerText().startsWith(filter)) {
					matchingFilter = filter;
					break;
				}
			}

			if (matchingFilter == null) {
				message.append(sbn.getTickerText());
			}
			else {
				message.append(sbn.getTickerText().replace(matchingFilter, ""));
			}

			alertService.notifyWatch(genericAppConfig.getAlertType().getAlertId(), message.toString());
		}
	}
	
	@Override
	public boolean isAvailable() {
		return alertService.isAvailable() && genericAppConfig != null;
	}

}
