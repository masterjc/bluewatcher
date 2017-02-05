package com.bluewatcher;

import java.util.HashSet;
import java.util.Set;

import com.bluewatcher.ble.Service;
import com.bluewatcher.config.ConfigurationOptionData;

/**
 * @version $Revision$
 */
public final class ServicesContainer implements ConfigurationOptionData {
	public static final String SERVICES_INFO_EXTRA = "com.bluewatcher.ServicesContainer.SERVICES_INFO_EXTRA";
	
	private static final String SERVICE_SEPARATOR = "/";
	private static final String PROPERTY_SEPARATOR = ":";
	
	Set<Service> services = new HashSet<Service>();
	
	private static class DummyServiceInfo implements Service {
		private boolean available;
		private int descResId;
		
		DummyServiceInfo(boolean available, int descResId) {
			this.available = available;
			this.descResId = descResId;
		}
		
		@Override
		public boolean isAvailable() {
			return available;
		}
		
		@Override
		public int getDescriptionResourceId() {
			return descResId;
		}
	}
	
	public void addService(Service service) {
		this.services.add(service);
	}
	
	public static ServicesContainer fromIntentData(String intentData) {
		ServicesContainer container = new ServicesContainer();
		if( intentData == null )
			return container;
		String[] parsedServices = intentData.split(SERVICE_SEPARATOR);
		if( parsedServices == null || parsedServices.length == 0)
			return container;
		for( int i = 0; i < parsedServices.length; i++ ) {
			String parsedService = parsedServices[i];
			String[] properties = parsedService.split(PROPERTY_SEPARATOR);
			if( properties == null || properties.length != 2 )
				continue;
			container.services.add(new DummyServiceInfo(Boolean.parseBoolean(properties[1]), Integer.parseInt(properties[0])));
		}
		return container;
	}
	
	public Set<Service> getServices() {
		return services;
	}

	@Override
	public String getIntentDataKey() {
		return SERVICES_INFO_EXTRA;
	}

	@Override
	public String getIntentDataValue() {
		StringBuffer buffer = new StringBuffer();
		boolean first = true;
		for(Service service : services) {
			if(first) {
				first = false;
			} else {
				buffer.append(SERVICE_SEPARATOR);
			}
			buffer.append(Integer.toString(service.getDescriptionResourceId()));
			buffer.append(PROPERTY_SEPARATOR);
			buffer.append(Boolean.toString(service.isAvailable()));
		}
		return buffer.toString();
	}

}
