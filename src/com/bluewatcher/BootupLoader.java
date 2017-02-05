package com.bluewatcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bluewatcher.activity.BlueWatcherActivity;

public class BootupLoader extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context, BlueWatcherActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}
}
