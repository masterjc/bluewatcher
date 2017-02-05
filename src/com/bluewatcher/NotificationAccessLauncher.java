package com.bluewatcher;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.provider.Settings;
import android.widget.Toast;

import com.bluewatcher.R;

/**
 * @version $Revision$
 */
public class NotificationAccessLauncher {
	private static final String NOTIFICATION_SETTINGS_ACTION = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";

	public static void requestAccess(Activity activity) {
		ContentResolver contentResolver = activity.getApplicationContext().getContentResolver();
		String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
		String packageName = activity.getApplicationContext().getPackageName();

		if (enabledNotificationListeners != null && enabledNotificationListeners.contains(packageName))
			return;

		openNotificationsConfig(activity);
	}
	
	public static void openNotificationsConfig(Activity activity) {
		Toast toast = Toast.makeText(activity.getApplicationContext(), R.string.security_add_notification_listener, Toast.LENGTH_LONG);
		toast.show();

		Intent intent = new Intent(NOTIFICATION_SETTINGS_ACTION);
		activity.startActivity(intent);
	}
}
