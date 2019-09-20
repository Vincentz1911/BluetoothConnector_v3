package com.vincentz.bluetoothconnectorv3;

import android.bluetooth.BluetoothDevice;

public class BTDeviceModel<image> {

    private String name;
    private String address;
    private BluetoothDevice device;
    private boolean paired;
    private boolean connected;
    private int mImageDrawable;


    // Constructor that is used to create an instance of the Movie object
    public BTDeviceModel(String name, String address, BluetoothDevice device, boolean paired, boolean connected, int mImageDrawable) {
        this.name = name;
        this.address = address;
        this.device = device;
        this.paired = paired;
        this.connected = connected;
        this.mImageDrawable = mImageDrawable;
    }

    public String getName() {return name;}
    public String getAddress() {return address;}
    public BluetoothDevice getDevice() {return device;}
    public boolean getPaired() {return paired;}
    public boolean getConnected() {return connected;}

    public int getmImageDrawable() { return mImageDrawable; }
    public void setmImageDrawable(int mImageDrawable) { this.mImageDrawable = mImageDrawable; }
}
