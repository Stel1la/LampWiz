package com.sasha.lampwiz.bluetooth.gatt.characteristic;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.UUID;

public abstract class Neopixel implements Parcelable {

    public static final UUID rgbLedServiceUuid = ParcelUuid.fromString("3f1d00c0-632f-4e53-9a14-437dd54bcccb").getUuid();

    private final BluetoothGattService service;
    private final BluetoothGattCharacteristic characteristic;

    public Neopixel() {

        this.service = null;
        this.characteristic = null;
    }

    public Neopixel(@NonNull BluetoothGattService service) {

        this.service = service;
        this.characteristic = this.service.getCharacteristic(this.uuid());
    }

    public Neopixel(@NonNull BluetoothGattService service, byte[] data) {

        this(service);

        this.unpack(data);
    }

    public Neopixel(Parcel in) {

        this.service = in.readParcelable(BluetoothGattService.class.getClassLoader());
        this.characteristic = in.readParcelable(BluetoothGattCharacteristic.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeParcelable(this.service, flags);
        dest.writeParcelable(this.characteristic, flags);
    }

    private static boolean isValid(BluetoothGattService service, BluetoothGattCharacteristic characteristic) {

        return (null != service) && (null != characteristic);
    }

    public boolean isValid() {

        return Neopixel.isValid(this.service, this.characteristic);
    }

    public void transmit(BluetoothGatt gatt) {

        if (this.isValid()) {
            this.characteristic.setValue(this.pack());
            gatt.writeCharacteristic(this.characteristic);
        }
    }

    public void request(BluetoothGatt gatt) {

        if (this.isValid()) {
            gatt.readCharacteristic(this.characteristic);
        }
    }

    public abstract UUID uuid();
    public abstract int size();
    public abstract byte[] pack();
    public abstract void unpack(byte[] data);
}
