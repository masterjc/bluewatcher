package com.bluewatcher;

import com.bluewatcher.ble.NotificationTypeConstants;

/**
 * @version $Revision$
 */
public enum AlertType {
	MAIL(NotificationTypeConstants.MAIL_NOTIFICATION_ID, "Mail"), SNS(NotificationTypeConstants.SNS_NOTIFICATION_ID, "SNS"), SMS(NotificationTypeConstants.SMS_NOTIFICATION_ID, "SMS"), CALENDAR(
			NotificationTypeConstants.CALENDAR_NOTIFICATION_ID, "Calendar"), CALL(NotificationTypeConstants.CALL_NOTIFICATION_ID, "Call");

	private int alertId;
	private String strId;

	private AlertType(int notificationId, String strId) {
		this.alertId = notificationId;
		this.strId = strId;
	}

	public int getAlertId() {
		return alertId;
	}
	
	public String getStringId() {
		return strId;
	}
	
	public static AlertType fromAlertId(int alertId) {
		AlertType[] types = AlertType.values();
		for( int i = 0; i < types.length; i++ ) {
			AlertType type = types[i];
			if( type.getAlertId() == alertId )
				return type;
		}
		return null;
	}
	
	public static AlertType fromStringId(String alertId) {
		AlertType[] types = AlertType.values();
		for( int i = 0; i < types.length; i++ ) {
			AlertType type = types[i];
			if( type.getStringId().equals(alertId) )
				return type;
		}
		return null;
	}
}
