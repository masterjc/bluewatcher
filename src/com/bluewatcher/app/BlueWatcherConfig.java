package com.bluewatcher.app;

import java.util.List;

import com.bluewatcher.app.call.IncomingCallConfig;
import com.bluewatcher.app.generic.GenericAppConfig;
import com.bluewatcher.app.whatsapp.WhatsappConfig;

/**
 * @version $Revision$
 */
public class BlueWatcherConfig {
	private IncomingCallConfig incomingCallConfig;
	private WhatsappConfig whatsappConfig;
	private List<GenericAppConfig> genericAppConfig;
	
	public BlueWatcherConfig(IncomingCallConfig incomingCallConfig, WhatsappConfig whatsappConfig, List<GenericAppConfig> genericAppConfig) {
		this.incomingCallConfig = incomingCallConfig;
		this.whatsappConfig = whatsappConfig;
		this.genericAppConfig = genericAppConfig;
	}
	
	public IncomingCallConfig getIncomingCallConfig() {
		return incomingCallConfig;
	}
	
	public WhatsappConfig getWhatsappConfig() {
		return whatsappConfig;
	}
	
	public List<GenericAppConfig> getGenericAppConfig() {
		return genericAppConfig;
	}
}
