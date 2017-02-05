package com.bluewatcher.app.call;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.TelephonyManager;

import com.bluewatcher.Notification;
import com.bluewatcher.app.WatcherApp;
import com.bluewatcher.ble.NotificationTypeConstants;
import com.bluewatcher.service.client.AlertService;

/**
 * @version $Revision$
 */
public class IncomingCallApp implements WatcherApp {
	private AlertService alertService;
	private IncomingCallConfig incomingCallConfig;
	
	public IncomingCallApp(AlertService alertService) {
		this.alertService = alertService;
	}

	public void applyConfig(IncomingCallConfig config) {
		synchronized (this) {
			this.incomingCallConfig = config;
		}
	}

	@Override
	public String getName() {
		return IncomingCallApp.class.getName();
	}

	@Override
	public String getAction() {
		return TelephonyManager.ACTION_PHONE_STATE_CHANGED;
	}

	@Override
	public void manage(Context context, Intent intent) {
		synchronized (this) {
			if( !alertService.isAvailable() || incomingCallConfig == null ) 
				return;
			
			String callStatus = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
			if (callStatus == null || !callStatus.equals(TelephonyManager.EXTRA_STATE_RINGING))
				return;

			String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
			if (!incomingCallConfig.isResolveContacts()) {
				alertService.notifyWatch(NotificationTypeConstants.CALL_NOTIFICATION_ID, incomingNumber);
				return;
			}
			ContentResolver cr = context.getContentResolver();
		    Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(incomingNumber));
		    Cursor cursor = cr.query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);
		    if (cursor == null) {
		    	alertService.notifyWatch(NotificationTypeConstants.CALL_NOTIFICATION_ID, incomingNumber);
		    	return;
		    }
		       
		    String contactName = null;
		    if(cursor.moveToFirst()) {
		        contactName = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));
		    } else {
		    	contactName = incomingNumber;
		    }

		    if(cursor != null && !cursor.isClosed()) {
		        cursor.close();
		    }

		    alertService.notifyWatch(NotificationTypeConstants.CALL_NOTIFICATION_ID, contactName);
		}
	}

	@Override
	public void manage(Context context, StatusBarNotificationAction sbnAction, Notification sbn) {
	}
	
	@Override
	public boolean isAvailable() {
		return alertService.isAvailable() && incomingCallConfig != null;
	}
}
