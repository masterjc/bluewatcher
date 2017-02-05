package com.bluewatcher.service.client;

import java.util.List;
import java.util.UUID;

import org.json.JSONException;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.bluewatcher.Device;
import com.bluewatcher.GattActionListener;
import com.bluewatcher.R;
import com.bluewatcher.app.finder.PhoneFinderConfig;
import com.bluewatcher.app.finder.PhoneFinderConfigManager;
import com.bluewatcher.ble.BluetoothClientManager;

/**
 * @version $Revision$
 */
public class CasioServiceActivator implements GattActionListener {
	
	private static final UUID CASIO_VIRTUAL_SERVER_SERVICE = UUID.fromString("26eb0007-b012-49a8-b1f8-394fb2032b0f");
	
	private static final UUID CASIO_VIRTUAL_SERVER_FEATURES = UUID.fromString("26eb0008-b012-49a8-b1f8-394fb2032b0f");
	
	private static final UUID CASIO_A_NOT_W_REQ_NOT = UUID.fromString( "26eb0009-b012-49a8-b1f8-394fb2032b0f");
	private static final UUID CASIO_A_NOT_COM_SET_NOT = UUID.fromString( "26eb000a-b012-49a8-b1f8-394fb2032b0f");
    
	private static final UUID CCC_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
	
	private final static String TAG = CasioServiceActivator.class.getSimpleName();

	private BluetoothClientManager bleService;
	private Context context;
	
	public CasioServiceActivator(Context context, BluetoothClientManager bleService) {
		this.bleService = bleService;
		this.context = context;
	}
	
	@Override
	public void notPairedDevice(Device deviceName) {
	}
	
	@Override
	public void actionGattConnected(Device deviceName) {
	}
	
	@Override
	public void actionGattDisconnected(Device deviceName) {
	}

	@Override
	public void actionGattServicesDiscovered(Device deviceName) {
		if( deviceName.isGBA400() )
			return;
		
		if(!isPhoneFindingEnabled())
			return;
		
		if (bleService.getInternalBleService() == null) {
			Toast.makeText(context, context.getString(R.string.error_no_ble_service), Toast.LENGTH_SHORT).show();
			return;
		}
		List<BluetoothGattService> services = bleService.getInternalBleService().getSupportedGattServices();
		for (BluetoothGattService service : services) {
			if( service.getUuid().equals(CASIO_VIRTUAL_SERVER_SERVICE)) {
				BluetoothGattCharacteristic charact1 = service.getCharacteristic(CASIO_A_NOT_COM_SET_NOT);
				BluetoothGattCharacteristic charact2 = service.getCharacteristic(CASIO_A_NOT_W_REQ_NOT);
				BluetoothGattCharacteristic casioVirtualServices = service.getCharacteristic(CASIO_VIRTUAL_SERVER_FEATURES);
				
				if( charact1 != null ) {
					bleService.getInternalBleService().setCharacteristicNotification(charact1, true);
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {}
					BluetoothGattDescriptor descriptor = charact1.getDescriptor(CCC_DESCRIPTOR_UUID);
					
					if(descriptor == null)
						return;
					descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
					bleService.getInternalBleService().writeDescriptor(descriptor);
					Log.i(TAG, "Enabled CASIO_A_NOT_COM_SET_NOT");
				}
				
				if( charact2 != null ) {
					bleService.getInternalBleService().setCharacteristicNotification(charact2, true);
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {}
					BluetoothGattDescriptor descriptor = charact2.getDescriptor(CCC_DESCRIPTOR_UUID);
					if(descriptor == null)
						return;
					descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
					bleService.getInternalBleService().writeDescriptor(descriptor);
					Log.i(TAG, "Enabled CASIO_A_NOT_W_REQ_NOT");
				}
				
				if( casioVirtualServices != null ) {
					sleep(1);
					byte byte0 = (byte)1;
					byte0 |= 2;
					byte0 |= 4;
					byte0 |= 8;
					casioVirtualServices.setValue(new byte[] {
				            byte0
			        });
					casioVirtualServices.setWriteType(2);
					bleService.getInternalBleService().writeCharacteristic(casioVirtualServices);
				}
			}
		}
	}
	
	private boolean isPhoneFindingEnabled() {
		PhoneFinderConfig config = null;
		try {
			config = PhoneFinderConfigManager.load(0);
		}
		catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}
		return config.isAppEnabled();
	}
	
	private void sleep(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void actionCharacteristicChanged(Device deviceName, UUID characteristic) {
	}
}
