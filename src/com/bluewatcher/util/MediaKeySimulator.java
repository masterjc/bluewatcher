package com.bluewatcher.util;

import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;

/**
 * @version $Revision$
 */
public class MediaKeySimulator {
	public static void simulateMediaKey(Activity activity, int key) {
		KeyEvent keyDown = new KeyEvent(KeyEvent.ACTION_DOWN, key);
        Intent intentDown = new Intent(Intent.ACTION_MEDIA_BUTTON);
        intentDown.putExtra(Intent.EXTRA_KEY_EVENT, keyDown);
		activity.sendOrderedBroadcast(intentDown, null);
		
		KeyEvent keyUp = new KeyEvent(KeyEvent.ACTION_UP, key);
        Intent intentUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
        intentUp.putExtra(Intent.EXTRA_KEY_EVENT, keyUp);
		activity.sendOrderedBroadcast(intentUp, null);
	}
}
