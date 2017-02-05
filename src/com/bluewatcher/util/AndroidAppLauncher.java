package com.bluewatcher.util;

import android.app.Activity;
import android.content.Intent;

import com.bluewatcher.activity.BlueWatcherActivity;
import com.bluewatcher.camera.CameraController;
import com.bluewatcher.control.ControlOption;

/**
 * @version $Revision$
 */
public class AndroidAppLauncher {
	private Activity activity;
	
	public AndroidAppLauncher(Activity activity) {
		this.activity = activity;
	}	
	
	@ControlOption
	public void startCamera() {
		final Intent intent = new Intent(activity, BlueWatcherActivity.class);
		CameraController.setStartCamera();
		activity.startActivity(intent);
	}
}
