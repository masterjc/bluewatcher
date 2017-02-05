package com.bluewatcher;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @version $Revision$
 */
public class Notification implements Parcelable {
	private int id;
	private String tickerText;
	private String creatorPackage;

	public Notification(int id) {
		this.id = id;
	}
	
	public static final Parcelable.Creator<Notification> CREATOR = new Parcelable.Creator<Notification>() {
		public Notification createFromParcel(Parcel in) {
			return new Notification(in);
		}

		public Notification[] newArray(int size) {
			return new Notification[size];
		}
	};
	
	private Notification(Parcel in) {
		id = in.readInt();
		tickerText = in.readString();
		creatorPackage = in.readString();
	}

	public String getTickerText() {
		return tickerText;
	}

	public void setTickerText(String tickerText) {
		this.tickerText = tickerText;
	}

	public int getId() {
		return id;
	}
	
	public String getCreatorPackage() {
		return creatorPackage;
	}

	public void setCreatorPackage(String creatorPackage) {
		this.creatorPackage = creatorPackage;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeInt(id);
		arg0.writeString(tickerText);
		arg0.writeString(creatorPackage);
		
	}
}
