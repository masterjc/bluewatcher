package com.bluewatcher.app.whatsapp;

import com.bluewatcher.app.whatsapp.WhatsappNotification.SenderType;

/**
 * @version $Revision$
 */
public class WhatsappMessageCreator {
	public static String create(WhatsappNotification notification) {
		StringBuffer msg = new StringBuffer();
		if( notification.getSenderType().equals(SenderType.CONTACT)) {
			msg.append("W:");
		} else {
			msg.append("WG:");
		}
		msg.append(notification.getSenderId());
		return msg.toString();
	}
}
