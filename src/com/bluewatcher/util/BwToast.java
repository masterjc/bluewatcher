package com.bluewatcher.util;

import android.content.Context;
import android.widget.Toast;

public class BwToast {
	private final static BwToast bWToast = new BwToast();

	public static BwToast getInstance() {
		return bWToast;
	}

	private Toast toast;

	public void longShow(Context context, String text) {
		if (toast != null) {
			cancel();
		}
		toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
		toast.show();
	}

	public void cancel() {
		if( toast == null )
			return;
		toast.cancel();
	}
}
