package com.bluewatcher.service.client;

import java.util.ArrayList;
import java.util.List;

/**
 * @version $Revision$
 */
public class AlertServiceWrapper implements AlertService {
	
	private List<AlertService> alertServices = new ArrayList<AlertService>();
	
	public void add(AlertService alertService) {
		this.alertServices.add(alertService);
	}
	
	@Override
	public boolean isAvailable() {
		for( AlertService service : alertServices ) {
			if( service.isAvailable() )
				return true;
		}
		return false;
	}

	
	@Override
	public int getDescriptionResourceId() {
		return com.bluewatcher.R.string.notifications_service;
	}

	@Override
	public void notifyWatch(int notificationId, String message) {
		for( AlertService service : alertServices ) {
			if( service.isAvailable() ) {
				service.notifyWatch(notificationId, message);
			}
		}
	}

	@Override
	public void cleanMessage(String message) {
		for( AlertService service : alertServices ) {
			if( service.isAvailable() ) {
				service.cleanMessage(message);
			}
		}
	}

	@Override
	public boolean needCleanMessage() {
		for( AlertService service : alertServices ) {
			if( service.isAvailable() ) {
				return service.needCleanMessage();
			}
		}
		return false;
	}
	
	

}
