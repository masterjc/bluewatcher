package com.bluewatcher.app.generic;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluewatcher.AlertType;
import com.bluewatcher.ble.NotificationTypeConstants;

/**
 * @version $Revision$
 */
public class GenericAppConfig implements Parcelable {
	private String appPackage;
	private String appDisplayName;
	
	private String label;
	private String prefix;
	private int alertType;
	private List<String> messageFilters;

	public static final Parcelable.Creator<GenericAppConfig> CREATOR = new Parcelable.Creator<GenericAppConfig>() {
		public GenericAppConfig createFromParcel(Parcel in) {
			return new GenericAppConfig(in);
		}

		public GenericAppConfig[] newArray(int size) {
			return new GenericAppConfig[size];
		}
	};
	
	public GenericAppConfig(String appPackage, String appDisplayName) {
		this.appDisplayName = appDisplayName;
		this.appPackage = appPackage;
		this.alertType = NotificationTypeConstants.MAIL_NOTIFICATION_ID;
		messageFilters = new ArrayList<String>();
	}

	private GenericAppConfig(Parcel in) {
		messageFilters = new ArrayList<String>();
		appPackage = in.readString();
		appDisplayName = in.readString();
		label = in.readString();
		prefix = in.readString();
		alertType = in.readInt();
		in.readStringList(messageFilters);
	}

	public String getPrefix() {
		return prefix;
	}

	public void setAppPackage(String pack) {
		this.appPackage = pack;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public AlertType getAlertType() {
		return AlertType.fromAlertId(alertType);
	}

	public void setAlertType(AlertType alertType) {
		this.alertType = alertType.getAlertId();
	}

	public List<String> getMessageFilters() {
		return messageFilters;
	}

	public void setMessageFilters(List<String> messageFilters) {
		this.messageFilters = messageFilters;
	}

	public String getAppDisplayName() {
		return appDisplayName;
	}

	public void setAppDisplayName(String appDisplayName) {
		this.appDisplayName = appDisplayName;
	}

	public String getAppPackage() {
		return appPackage;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(appPackage);
		dest.writeString(appDisplayName);
		dest.writeString(label);
		dest.writeString(prefix);
		dest.writeInt(alertType);
		dest.writeStringList(messageFilters);
	}
	
	@Override
	public boolean equals(Object o) {
		GenericAppConfig genericAppConfig = (GenericAppConfig)o;
		return genericAppConfig.appPackage.equals(appPackage);
	}

}
