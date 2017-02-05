package com.bluewatcher;


/**
 * @version $Revision$
 */
public class Device {
	
	public static enum ControlConfiguration {
		TWO_BUTTON(2), THREE_BUTTON(3), FIVE_BUTTON(5);
		
		private int nButtons;
		
		private ControlConfiguration(int nButtons) {
			this.nButtons = nButtons;
		}
		
		public int getNumberButtons() {
			return nButtons;
		}
	}
	
	public static final String GBA_400 = "GBA-400";
	public static final String STB_1000 = "STB-1000";
	public static final String GB_5600 = "GB-5600";
	private String address;
	private String name;
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param address
	 * @param name
	 */
	public Device(String address, String name) {
		super();
		this.address = address;
		this.name = name;
	}
	
	public ControlConfiguration getControlConfiguration() {
		if( isGBA400() || isSTB1000() )
			return ControlConfiguration.FIVE_BUTTON;
		
		if( isGB5600() )
			return ControlConfiguration.TWO_BUTTON;
		
		return ControlConfiguration.THREE_BUTTON;
	}
	
	public boolean isGBA400() {
		if(name == null)
			return false;
		return name.contains(GBA_400);
	}
	
	public boolean isSTB1000() {
		if(name == null)
			return false;
		return name.contains(STB_1000);
	}
	
	public boolean isGB5600() {
		if(name == null)
			return false;
		return name.contains(GB_5600);
	}
	
	@Override
	public boolean equals(Object o) {
		Device d = (Device)o;
		return d.getAddress().equals(address) && d.getName().equals(name);
		
	}
	
	
}	

