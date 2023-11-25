package com.sasha.lampwiz.activity.main;

import android.bluetooth.BluetoothGattService;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.sasha.lampwiz.bluetooth.Connection;
import com.sasha.lampwiz.bluetooth.Device;
import com.sasha.lampwiz.bluetooth.gatt.GattCallback;
import com.sasha.lampwiz.bluetooth.gatt.characteristic.NeopixelAnima;
import com.sasha.lampwiz.bluetooth.gatt.characteristic.NeopixelColor;
import com.sasha.lampwiz.bluetooth.gatt.characteristic.NeopixelStrip;

public class MainViewModel extends ViewModel implements ServiceConnection {

    private static final boolean AUTO_CONNECT_DEFAULT = true;
    private static final String keyBooleanIsServiceBound = "com.sasha.lampwiz.activity.main.MainViewModel.isServiceBound";
    private static final String keyBooleanIsScanning = "com.sasha.lampwiz.activity.main.MainViewModel.isScanning";
    private static final String keyParcelConnection = "com.sasha.lampwiz.activity.main.MainViewModel.connection";
    private static final String keyBooleanAutoConnect = "com.sasha.lampwiz.activity.main.MainViewModel.autoConnect";
    private static final String keyParcelDevice = "com.sasha.lampwiz.activity.main.MainViewModel.device";
    private static final String keyParcelConnectedDevice = "com.sasha.lampwiz.activity.main.MainViewModel.connectedDevice";
    private static final String keyParcelNeopixelService = "com.sasha.lampwiz.activity.main.MainViewModel.neopixelService";
    private static final String keyParcelNeopixelColor = "com.sasha.lampwiz.activity.main.MainViewModel.neopixelColor";
    private static final String keyParcelNeopixelStrip = "com.sasha.lampwiz.activity.main.MainViewModel.neopixelStrip";
    private static final String keyParcelNeopixelAnima = "com.sasha.lampwiz.activity.main.MainViewModel.neopixelAnima";
    private static GattCallback gattCallback;
    private static ForegroundRunner foregroundRunner;
    private final SavedStateHandle state;

    public MainViewModel(SavedStateHandle state) {

        this.state = state;

        if (null == MainViewModel.gattCallback) {
            MainViewModel.gattCallback = new GattCallback(this);
        }

        if (!this.state.contains(keyBooleanIsServiceBound)) {
            this.state.set(keyBooleanIsServiceBound, false);
        }

        if (!this.state.contains(keyBooleanIsScanning)) {
            this.state.set(keyBooleanIsScanning, false);
        }

        if (!this.state.contains(keyParcelConnection)) {
            this.state.set(keyParcelConnection, null);
        }
        if (!this.state.contains(keyBooleanAutoConnect)) {
            this.state.set(keyBooleanAutoConnect, MainViewModel.AUTO_CONNECT_DEFAULT);
        }
        if (!this.state.contains(keyParcelDevice)) {
            this.state.set(keyParcelDevice, null);
        }
        if (!this.state.contains(keyParcelConnectedDevice)) {
            this.state.set(keyParcelConnectedDevice, null);
        }

        if (!this.state.contains(keyParcelNeopixelService)) {
            this.state.set(keyParcelNeopixelService, null);
        }
        if (!this.state.contains(keyParcelNeopixelColor)) {
            this.state.set(keyParcelNeopixelColor, null);
        }
        if (!this.state.contains(keyParcelNeopixelStrip)) {
            this.state.set(keyParcelNeopixelStrip, null);
        }
        if (!this.state.contains(keyParcelNeopixelAnima)) {
            this.state.set(keyParcelNeopixelAnima, null);
        }
    }

    public static void setForegroundRunner(@NonNull ForegroundRunner foregroundRunner) {

        MainViewModel.foregroundRunner = foregroundRunner;
    }

    public void runInForeground(@NonNull Runnable action) {

        if (null != MainViewModel.foregroundRunner) {
            MainViewModel.foregroundRunner.runInForeground(action);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

        this.state.set(keyBooleanIsServiceBound, true);

        this.state.set(keyParcelConnection, ((Connection.ServiceBinder) service).getService());
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

        this.state.set(keyBooleanIsServiceBound, true);
    }

    @Override
    public void onBindingDied(ComponentName name) {

        this.state.set(keyBooleanIsServiceBound, true);
    }

    @Override
    public void onNullBinding(ComponentName name) {

        this.state.set(keyBooleanIsServiceBound, true);
    }

    public LiveData<Boolean> isScanning() {

        return this.state.getLiveData(keyBooleanIsScanning);
    }

    public void setIsScanning(boolean isScanning) {

        this.state.set(keyBooleanIsScanning, isScanning);
    }

    public Connection getConnection() {

        return this.state.get(keyParcelConnection);
    }

    public boolean getAutoConnect() {

        Boolean autoConnect = this.state.get(keyBooleanAutoConnect);
        if (null == autoConnect) {
            autoConnect = MainViewModel.AUTO_CONNECT_DEFAULT;
        }
        return autoConnect;
    }

    public LiveData<Device> device() {

        return this.state.getLiveData(keyParcelDevice);
    }

    public Device getDevice() {

        return this.state.get(keyParcelDevice);
    }

    public void setDevice(Device device) {

        this.state.set(keyParcelDevice, device);

        if (null != device) {
            MainViewModel.gattCallback.connect(device);
        } else {
            MainViewModel.gattCallback.disconnect(false);
        }
    }

    public LiveData<Device> connectedDevice() {

        return this.state.getLiveData(keyParcelConnectedDevice);
    }

    public void setConnectedDevice(Device device) {

        this.state.set(keyParcelConnectedDevice, device);
    }

    public BluetoothGattService getNeopixelService() {

        return this.state.get(keyParcelNeopixelService);
    }

    public void setNeopixelService(@NonNull BluetoothGattService neopixelService) {

        this.state.set(keyParcelNeopixelService, neopixelService);
    }

    public LiveData<NeopixelStrip> neopixelStrip() {

        return this.state.getLiveData(keyParcelNeopixelStrip);
    }

    public NeopixelStrip getNeopixelStrip() {

        return this.state.get(keyParcelNeopixelStrip);
    }

    public void setNeopixelStrip(@NonNull NeopixelStrip neopixelStrip) {

        this.state.set(keyParcelNeopixelStrip, neopixelStrip);
    }

    public NeopixelColor getNeopixelColor() {

        return this.state.get(keyParcelNeopixelColor);
    }

    public void setNeopixelColor(@NonNull NeopixelColor neopixelColor) {

        this.state.set(keyParcelNeopixelColor, neopixelColor);
    }

    public NeopixelAnima getNeopixelAnima() {

        return this.state.get(keyParcelNeopixelAnima);
    }

    public void setNeopixelAnima(@NonNull NeopixelAnima neopixelAnima) {

        this.state.set(keyParcelNeopixelAnima, neopixelAnima);
    }

    public void updateNeopixelColor(final int start, final int length, final int color, final int alpha, final int bright) {

        final NeopixelColor neopixelColor = this.getNeopixelColor();
        if ((null != neopixelColor) && neopixelColor.isValid()) {
            neopixelColor.setData(start, length, color, alpha, bright);
            MainViewModel.gattCallback.transmit(neopixelColor);
        }
    }

    public void updateNeopixelColor(final int length, final int color, final int alpha, final int bright) {

        final int start = NeopixelColor.DEFAULT_START;

        this.updateNeopixelColor(start, length, color, alpha, bright);
    }

    public void updateNeopixelColor(final int color, final int alpha, final int bright) {

        int length = 0;

        final NeopixelStrip neopixelStrip = this.getNeopixelStrip();
        if ((null != neopixelStrip) && neopixelStrip.isValid()) {
            length = neopixelStrip.count();
        }

        this.updateNeopixelColor(length, color, alpha, bright);
    }

    public interface ForegroundRunner {
        void runInForeground(@NonNull Runnable action);
    }
}
