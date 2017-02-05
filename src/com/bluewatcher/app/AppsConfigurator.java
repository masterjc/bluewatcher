package com.bluewatcher.app;

import java.util.List;

import com.bluewatcher.app.call.IncomingCallApp;
import com.bluewatcher.app.call.IncomingCallConfig;
import com.bluewatcher.app.generic.GenericApp;
import com.bluewatcher.app.generic.GenericAppConfig;
import com.bluewatcher.app.whatsapp.WhatsappApp;
import com.bluewatcher.app.whatsapp.WhatsappConfig;
import com.bluewatcher.service.client.AlertService;

/**
 * @version $Revision$
 */
public class AppsConfigurator {
	private IncomingCallApp incomingCallApp;
	private WhatsappApp whatsappApp;
	private AlertService alertService;
	private AppsManager appsManager;

	public AppsConfigurator(AppsManager appsManager, AlertService alertService) {
		this.alertService = alertService;
		incomingCallApp = new IncomingCallApp(alertService);
		whatsappApp = new WhatsappApp(alertService);
		this.appsManager = appsManager;
		appsManager.register(incomingCallApp);
		appsManager.register(whatsappApp);
	}

	public void applyConfig(BlueWatcherConfig blueWatcherConfig) {
		if( blueWatcherConfig == null )
			return;
		applyIncomingCallConfig(blueWatcherConfig.getIncomingCallConfig());
		applyWhatsappConfig(blueWatcherConfig.getWhatsappConfig());
		applyGenericAppsConfig(blueWatcherConfig.getGenericAppConfig());
	}

	private void applyIncomingCallConfig(IncomingCallConfig config) {
		if (!config.isAppEnabled())
			return;
		incomingCallApp.applyConfig(config);
	}

	private void applyWhatsappConfig(WhatsappConfig whatsappConfig) {
		if (!whatsappConfig.isAppEnabled())
			return;
		whatsappApp.applyConfig(whatsappConfig);
	}

	private void applyGenericAppsConfig(List<GenericAppConfig> customConfig) {
		appsManager.clearGenericApplications();
		for( GenericAppConfig config : customConfig ) {
			GenericApp app = new GenericApp(alertService);
			app.applyConfig(config);
			appsManager.registerGeneric(app);
		}
	}
}
