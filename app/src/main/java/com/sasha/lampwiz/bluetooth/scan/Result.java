package com.sasha.lampwiz.bluetooth.scan;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanRecord;

import androidx.annotation.NonNull;

import com.sasha.lampwiz.bluetooth.Device;

public class Result {

    private final android.bluetooth.le.ScanResult scanResult;

    public Result(@NonNull android.bluetooth.le.ScanResult scanResult) {

        this.scanResult = scanResult;
    }

    public android.bluetooth.le.ScanResult content() {

        return this.scanResult;
    }

    public BluetoothDevice device() {

        return this.scanResult.getDevice();
    }

    public ScanRecord scanRecord() {

        return this.scanResult.getScanRecord();
    }

    public String name() {

        BluetoothDevice device = this.scanResult.getDevice();
        String name = null;

        if (null != device) {
            name = device.getName();
            if ((null == name) || (0 == name.trim().length())) {
                ScanRecord scanRecord = this.scanResult.getScanRecord();
                if (null != scanRecord) {
                    name = this.scanResult.getScanRecord().getDeviceName();
                }
            }
        }

        if (null == name) {
            name = Device.INVALID_NAME;
        }

        return name;
    }

    public String address() {

        BluetoothDevice device = this.scanResult.getDevice();
        String address = null;

        if (null != device) {
            address = device.getAddress();
        }

        if (null == address) {
            address = Device.INVALID_ADDRESS;
        }

        return address;
    }

    public int rssi() {

        return this.scanResult.getRssi();
    }
}
