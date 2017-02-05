package com.bluewatcher.ble;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.bluewatcher.R;

/**
 * @version $Revision$
 */
public class BleDeviceListAdapter extends BaseAdapter {
    private ArrayList<BluetoothDevice> bleDevices;
    private LayoutInflater inflator;

    public BleDeviceListAdapter(LayoutInflater inflator) {
        super();
        this.inflator = inflator;
        bleDevices = new ArrayList<BluetoothDevice>();
    }

    public void addDevice(BluetoothDevice device) {
        if(!bleDevices.contains(device)) {
        	bleDevices.add(device);
        }
    }

    public BluetoothDevice getDevice(int position) {
        return bleDevices.get(position);
    }

    public void clear() {
    	bleDevices.clear();
    }

    @Override
    public int getCount() {
        return bleDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return bleDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("InflateParams")
	@Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if (view == null) {
            view = inflator.inflate(R.layout.listitem_device, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
            viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        BluetoothDevice device = bleDevices.get(i);
        final String deviceName = device.getName();
        if (deviceName != null && deviceName.length() > 0)
            viewHolder.deviceName.setText(deviceName);
        else
            viewHolder.deviceName.setText(R.string.unknown_device);
        viewHolder.deviceAddress.setText(device.getAddress());

        return view;
    }
    
    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
}
