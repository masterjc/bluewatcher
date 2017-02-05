package com.bluewatcher.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.bluewatcher.ble.NotificationTypeConstants;
import com.bluewatcher.control.ControlOption;
import com.bluewatcher.service.client.AlertService;

/**
 * @version $Revision$
 */
public class BatteryController {
	private AlertService alertService;
	private int currentLevel = 0;

	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context ctxt, Intent intent) {
			int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
			int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
			currentLevel = level * 100 / scale;
		}
	};

	public BatteryController(Context context, AlertService alertService) {
		this.alertService = alertService;
		context.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	}

	@ControlOption
	public void sendPhoneBatteryLevel() {
		String percentage = "BAT:" + currentLevel + "%";
		alertService.notifyWatch(NotificationTypeConstants.MAIL_NOTIFICATION_ID, percentage);
	}

}
