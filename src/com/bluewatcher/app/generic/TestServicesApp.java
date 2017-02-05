package com.bluewatcher.app.generic;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.bluewatcher.R;
import com.bluewatcher.ble.NotificationTypeConstants;
import com.bluewatcher.config.ConfigurationManager;
import com.bluewatcher.service.client.AlertService;
import com.bluewatcher.service.client.TimeService;

/**
 * @version $Revision$
 */
public class TestServicesApp {

	private Activity activity;
	private AlertService alertService;
	private TimeService timeService;

	public TestServicesApp(Activity activity, AlertService alertService, TimeService timeService) {
		this.activity = activity;
		this.alertService = alertService;
		this.timeService = timeService;
	}

	public void sendTestNotifications() {
		Context appContext = activity.getApplicationContext();
		Toast.makeText(appContext, appContext.getString(R.string.test_alert), Toast.LENGTH_LONG).show();
		testNotifications(appContext);
		testTimeService(appContext);
	}

	private void testNotifications(Context appContext) {
		if (!alertService.isAvailable()) {
			Toast.makeText(appContext, appContext.getString(R.string.watch_not_connected_alert), Toast.LENGTH_LONG).show();
		}
		else {
			String info = appContext.getString(R.string.app_name) + " " + appContext.getString(R.string.app_version);
			alertService.notifyWatch(NotificationTypeConstants.CALL_NOTIFICATION_ID, info);
		}
	}

	private void testTimeService(Context appContext) {
		if (!timeService.isReady()) {
			Toast.makeText(appContext, appContext.getString(R.string.watch_not_connected_alert), Toast.LENGTH_LONG).show();
		}
		else {
			try {
				timeService.sendTime();
			}
			catch (Exception e) {
				ConfigurationManager.showConfigurationError(appContext);
			}
		}
	}
}
