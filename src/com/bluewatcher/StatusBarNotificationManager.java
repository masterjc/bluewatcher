package com.bluewatcher;

import java.util.UUID;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.bluewatcher.activity.BlueWatcherActivity;
import com.bluewatcher.service.server.ControlModelChangeListener;

/**
 * @version $Revision$
 */
public class StatusBarNotificationManager implements GattActionListener, ControlModelChangeListener {
	private static final int NOTIFICATION_APP = 123421;

	public enum Status {
		Connected, Disconnected;
	}

	private Activity activity;
	private NotificationManager notificationManager;
	private android.app.Notification notification;
	
	private Status currentStatus;
	private String currentControlMode;

	public StatusBarNotificationManager(Activity parentActivity) {
		this.activity = parentActivity;
		notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
		currentStatus = Status.Disconnected;
		currentControlMode = activity.getApplicationContext().getString(R.string.not_available);
		update();
	}

	public void update() {
		int status = 0;
		int icon = 0;
		if (currentStatus == Status.Connected) {
			status = R.string.connected;
			icon = R.drawable.kk_on;
		}
		else {
			status = R.string.disconnected;
			icon = R.drawable.kk_off;
		}

		Context context = activity.getApplicationContext();
		Intent notificationIntent = new Intent(context, BlueWatcherActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(activity, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		String contentText = context.getString(R.string.connection_status) + " " + context.getString(status) + "  " + context.getString(R.string.control_mode) + " " + currentControlMode ;
		notification = new android.app.Notification.Builder(context)
				.setContentTitle(context.getText(R.string.app_name) + " " + context.getText(R.string.app_version)).setContentText(contentText)
				.setSmallIcon(icon).setContentIntent(contentIntent).setOngoing(true).build();
		notificationManager.notify(NOTIFICATION_APP, notification);
	}
	
	public android.app.Notification getNotification() {
		return notification;
	}
	
	public int getNotificationId() {
		return NOTIFICATION_APP;
	}

	public void destroy() {
		notificationManager.cancel(NOTIFICATION_APP);
	}

	@Override
	public void actionGattConnected(Device deviceName) {
		this.currentStatus = Status.Connected;
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				update();
			}
		});
	}

	@Override
	public void actionGattDisconnected(Device deviceName) {
		this.currentStatus = Status.Disconnected;
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				update();
			}
		});
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

	@Override
	public void modeChanged(String mode) {
		this.currentControlMode = mode;
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				update();
			}
		});
		
	}
}
