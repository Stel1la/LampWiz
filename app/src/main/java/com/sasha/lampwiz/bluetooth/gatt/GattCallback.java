package com.sasha.lampwiz.bluetooth.gatt;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;

import androidx.annotation.NonNull;

import com.sasha.lampwiz.Utility;
import com.sasha.lampwiz.activity.main.MainViewModel;
import com.sasha.lampwiz.bluetooth.Connection;
import com.sasha.lampwiz.bluetooth.Device;
import com.sasha.lampwiz.bluetooth.gatt.characteristic.Neopixel;
import com.sasha.lampwiz.bluetooth.gatt.characteristic.NeopixelAnima;
import com.sasha.lampwiz.bluetooth.gatt.characteristic.NeopixelColor;
import com.sasha.lampwiz.bluetooth.gatt.characteristic.NeopixelStrip;

import java.util.UUID;

public class GattCallback extends BluetoothGattCallback {

    private final MainViewModel mainViewModel;
    private Device device;
    private BluetoothGatt bluetoothGatt;
    private Runnable reconnect;

    public GattCallback(MainViewModel mainViewModel) {

        this.mainViewModel = mainViewModel;

        this.device = null;
        this.bluetoothGatt = null;
        this.reconnect = null;
    }

    public void connect(@NonNull final Device device) {

        Connection connection = this.mainViewModel.getConnection();
        if (null != connection) {

            this.device = device;

            Runnable connect = () -> {
                this.bluetoothGatt = device.connect(
                    connection.getApplicationContext(), this.mainViewModel.getAutoConnect(), this
                );
            };

            if (this.isConnectedToDevice()) {
                this.reconnect = connect;
                this.disconnect(true);
            } else {
                if (this.mainViewModel.getAutoConnect()) {
                    this.reconnect = connect;
                }
                connection.run(connect);
            }
        }
    }

    public void disconnect(boolean reconnect) {

        if (!reconnect) {
            this.reconnect = null;
        }

        Connection connection = this.mainViewModel.getConnection();
        if ((null != connection) && (null != this.bluetoothGatt)) {
            connection.run(() -> this.bluetoothGatt.disconnect());
        }
    }

    public boolean isConnectedToDevice() {

        return (null != this.bluetoothGatt) && (null != this.device) && this.device.isConnected();
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

        super.onConnectionStateChange(gatt, status, newState);

        if (newState == BluetoothProfile.STATE_CONNECTED) {

            this.device.setIsConnected(true);

            this.mainViewModel.runInForeground(() -> this.mainViewModel.setConnectedDevice(this.device));

            Connection connection = this.mainViewModel.getConnection();
            if ((null != connection) && (null != this.bluetoothGatt)) {
                connection.run(() -> this.bluetoothGatt.discoverServices());
            }

        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {

            this.device.setIsConnected(false);
            this.mainViewModel.runInForeground(() -> this.mainViewModel.setConnectedDevice(null));

            if (null != this.reconnect) {
                Connection connection = this.mainViewModel.getConnection();
                if (null != connection) {
                    connection.run(this.reconnect);
                }
            }
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {

        super.onServicesDiscovered(gatt, status);

        if (status == BluetoothGatt.GATT_SUCCESS) {

            final BluetoothGattService service = gatt.getService(Neopixel.rgbLedServiceUuid);
            final NeopixelStrip strip = new NeopixelStrip(service);
            final NeopixelColor color = new NeopixelColor(service);
            final NeopixelAnima anima = new NeopixelAnima(service);

            this.mainViewModel.runInForeground(() -> {
                this.mainViewModel.setNeopixelService(service);
                this.mainViewModel.setNeopixelStrip(strip);
                this.mainViewModel.setNeopixelColor(color);
                this.mainViewModel.setNeopixelAnima(anima);
            });

            this.request(strip);
            this.request(color);
            this.request(anima);
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

        super.onCharacteristicRead(gatt, characteristic, status);

        if ((null != characteristic) && (BluetoothGatt.GATT_SUCCESS == status)) {
            UUID uuid = characteristic.getUuid();
            if (null != uuid) {

                final BluetoothGattService neopixelService = this.mainViewModel.getNeopixelService();
                final byte[] value = characteristic.getValue();

                Runnable action = null;

                if (uuid.equals(this.mainViewModel.getNeopixelStrip().uuid())) {
                    action = () -> this.mainViewModel.setNeopixelStrip(new NeopixelStrip(neopixelService, value));
                } else if (uuid.equals(this.mainViewModel.getNeopixelColor().uuid())) {
                    action = () -> this.mainViewModel.setNeopixelColor(new NeopixelColor(neopixelService, value));
                } else if (uuid.equals(this.mainViewModel.getNeopixelAnima().uuid())) {
                    action = () -> this.mainViewModel.setNeopixelAnima(new NeopixelAnima(neopixelService, value));
                }

                if (null != action) {
                    this.mainViewModel.runInForeground(action);
                }
            }
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

        super.onCharacteristicWrite(gatt, characteristic, status);
    }

    public void transmit(@NonNull final Neopixel ...neopixel) {


        Connection connection = this.mainViewModel.getConnection();
        if ((null != connection) && this.isConnectedToDevice()) {
            Utility.RunList runList = new Utility.RunList();
            for (final Neopixel pixel : neopixel) {
                runList.add(() -> pixel.transmit(this.bluetoothGatt));
            }
            if (runList.size() > 0) {
                connection.run(runList);
            }
        }
    }

    public void request(@NonNull final Neopixel ...neopixel) {

        Connection connection = this.mainViewModel.getConnection();
        if ((null != connection) && this.isConnectedToDevice()) {
            Utility.RunList runList = new Utility.RunList();
            for (final Neopixel pixel : neopixel) {
                runList.add(() -> pixel.request(this.bluetoothGatt));
            }
            if (runList.size() > 0) {
                connection.run(runList);
            }
        }
    }
}
