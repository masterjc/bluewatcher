package com.bluewatcher.view;

import java.util.UUID;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.TextView;

import com.bluewatcher.Device;
import com.bluewatcher.GattActionListener;
import com.bluewatcher.R;
import com.bluewatcher.service.server.ControlModelChangeListener;

public class ConnectionStatusView implements GattActionListener, ControlModelChangeListener {
	private static final int RED_COLOR = Color.rgb(200, 0, 0);
	private static final int GREEN_COLOR = Color.rgb(0, 200, 0);
	private static final int BLUE_COLOR = Color.rgb(0, 0, 200);

	private TextView connectionState;
	private TextView device;
	private TextView controlMode;
	private Activity activity;
	
	public ConnectionStatusView(Activity activity) {
		this.activity = activity;
		connectionState = (TextView) activity.findViewById(R.id.watch_connection_status);
		connectionState.setPaintFlags(Paint.FAKE_BOLD_TEXT_FLAG);
		device = ((TextView) activity.findViewById(R.id.watch_name_content));
		controlMode = ((TextView) activity.findViewById(R.id.phone_control_mode));
		reset();
	}

	public void connected(String deviceName) {
		connectionState.setText(R.string.connected);
		connectionState.setTextColor(GREEN_COLOR);
		if(deviceName == null ) {
			deviceName = activity.getApplicationContext().getString(R.string.unknown_device);
		}
		device.setText(deviceName);
		controlMode.setTextColor(GREEN_COLOR);
	}
	
	public void connecting(String deviceName) {
		connectionState.setText(R.string.connecting);
		connectionState.setTextColor(BLUE_COLOR);
		if(deviceName == null ) {
			deviceName = activity.getApplicationContext().getString(R.string.unknown_device);
		}
		device.setText(deviceName);
		controlMode.setTextColor(BLUE_COLOR);
	}
	
	public void disconnected() {
		if( connectionState.getText() != null && connectionState.getText().equals(activity.getApplicationContext().getString(R.string.not_paired)))
			return;
		connectionState.setText(R.string.disconnected);
		connectionState.setTextColor(RED_COLOR);
		controlMode.setTextColor(RED_COLOR);
	}
	
	public void controlModeChanged(String mode) {
		controlMode.setText(mode);
	}
	
	public void notPaired() {
		connectionState.setText(R.string.not_paired);
		connectionState.setTextColor(RED_COLOR);
		controlMode.setTextColor(RED_COLOR);
	}
	
	public void reset() {
		device.setText(R.string.not_available);
		controlMode.setText(R.string.not_available);
		disconnected();
	}

	@Override
	public void actionGattConnected(Device deviceName) {
		final Device device = deviceName; 
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				connected(device.getName());
			}
		});
	}
	
	@Override
	public void actionGattDisconnected(Device deviceName) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				disconnected();
			}
		});
	}
	
	@Override
	public void actionGattServicesDiscovered(Device deviceName) {
	}
	
	@Override
	public void notPairedDevice(Device deviceName) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				notPaired();
			}
		});
	}
	
	@Override
	public void actionCharacteristicChanged(Device deviceName, UUID characteristic) {
	}

	@Override
	public void modeChanged(String mode) {
		final String myMode = mode;
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				controlModeChanged(myMode);
			}
		});
		
	}

}
