package com.bluewatcher.app.whatsapp;

import com.bluewatcher.app.WatcherApp.StatusBarNotificationAction;

/**
 * @version $Revision$
 */
public class WhatsappNotification {
	public enum SenderType {
		GROUP, CONTACT;
	}
	
	private String senderId;
	private String groupId;
	private int id;
	private StatusBarNotificationAction action;
	private SenderType senderType;
	
	public String getSenderId() {
		return senderId;
	}
	
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public StatusBarNotificationAction getAction() {
		return action;
	}
	
	public void setAction(StatusBarNotificationAction action) {
		this.action = action;
	}
	
	public SenderType getSenderType() {
		return senderType;
	}
	
	public void setSenderType(SenderType senderType) {
		this.senderType = senderType;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
}
