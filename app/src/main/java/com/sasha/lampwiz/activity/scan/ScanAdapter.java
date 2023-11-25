package com.sasha.lampwiz.activity.scan;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sasha.lampwiz.R;
import com.sasha.lampwiz.Utility;
import com.sasha.lampwiz.bluetooth.Device;
import com.sasha.lampwiz.bluetooth.scan.Result;
import com.sasha.lampwiz.room.dao.DeviceDao;
import com.sasha.lampwiz.room.entity.DeviceEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ScanAdapter extends RecyclerView.Adapter<ScanAdapter.ViewHolder> {

    private final List<Device> device;
    private final ConcurrentHashMap<String, Device> deviceMap; // keyed on device address
    private final ConnectClickListener connectClickListener;
    private final DeviceDao deviceDao;

    ScanAdapter(ConnectClickListener connectClickListener, DeviceDao deviceDao) {

        this.device = new ArrayList<>();
        this.deviceMap = new ConcurrentHashMap<>();
        this.connectClickListener = connectClickListener;
        this.deviceDao = deviceDao;
        Log.d("DeviceDao", deviceDao.toString());
    }

    @NonNull
    @Override
    public ScanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.view_device_scan, parent, false);

        return new ScanAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ScanAdapter.ViewHolder holder, int position) {

        Device d;
        try {
            synchronized (this) {
                d = this.device.get(position);
            }
        } catch (Exception ex) {
            d = new Device();
        }
        final Device device = d;

        holder.addressLabel.setText(device.address());

        if ((null != device.name()) && (device.name().trim().length() > 0)) {
            holder.nameLabel.setText(Utility.format("%s", device.name()));
            holder.nameLabel.setVisibility(View.VISIBLE);
        } else {
            holder.nameLabel.setText("");
            holder.nameLabel.setVisibility(View.GONE);
        }

        holder.rssiLabel.setText(Utility.format("%d dBm", device.rssi()));

//        holder.setIsConnectable(device.isConnectable() && device.hasLampWizServices());

        holder.connectButton.setOnClickListener(v -> {
            this.connectClickListener.onConnectClick(device);
            Device curDevice = this.device.get(position);

            // Save the device info to the database when connect button is clicked
            saveDeviceToDatabase(curDevice);
            // Call the connectClickListener callback
            connectClickListener.onConnectClick(curDevice);
        });

        if ((null != device.manufacturer()) && (device.manufacturer().trim().length() > 0)) {
            holder.manufacturerLabel.setText(device.manufacturer());
            holder.manufacturerLabel.setVisibility(View.VISIBLE);
        } else {
            holder.manufacturerLabel.setText("");
            holder.manufacturerLabel.setVisibility(View.GONE);
        }
    }

    private void saveDeviceToDatabase(Device device) {
        DeviceEntity deviceEntity = new DeviceEntity();
        deviceEntity.macAddress = device.address();
        deviceEntity.name = device.name();
        deviceEntity.date = Utility.getCurrentDateAsString("yyyy-MM-dd HH:mm:ss");
        Log.d("DeviceDao", "Done");
        if (deviceDao != null) {
            new Thread(() -> deviceDao.insert(deviceEntity)).start();
        }
    }

    @Override
    public int getItemCount() {

        return this.count();
    }

    public int count() {

        return this.deviceMap.size();
    }

    public Device get(String address) {

        return this.deviceMap.get(address);
    }

    public void add(@NonNull Result result) {

        int rssi = result.rssi();

        Device periph = this.get(result.address());
        if (null == periph || rssi > periph.rssi()) {
            Device insert;
            synchronized (this) {

                int id = 0;
                while (id < this.device.size() && this.device.get(id).rssi() >= rssi) {
                    ++id;
                }

                insert = new Device(id, result);

                if (null != periph) {
                    this.device.remove(periph.id());
                    this.device.add(id, insert);
                    this.notifyItemMoved(periph.id(), id);
                    this.notifyItemChanged(id);
                } else {
                    this.device.add(id, insert);
                    this.notifyItemInserted(id);
                }

                for (int i = id + 1; i < this.device.size(); ++i) {
                    this.device.get(i).setId(i);
                }
            }
            this.deviceMap.put(insert.address(), insert);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clear() {

        this.deviceMap.clear();
        synchronized (this) {
            this.device.clear();
            this.notifyDataSetChanged();
        }
    }

    public interface ConnectClickListener {

        void onConnectClick(@NonNull Device device);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        final View parent;
        final TextView addressLabel;
        final TextView rssiLabel;
        final ImageView bluetoothImage;
        final TextView nameLabel;
        final TextView manufacturerLabel;
        final Button connectButton;

        ViewHolder(@NonNull View itemView) {

            super(itemView);

            this.parent = itemView;
            this.addressLabel = itemView.findViewById(R.id.device_card_address_label);
            this.rssiLabel = itemView.findViewById(R.id.device_card_rssi_label);
            this.bluetoothImage = itemView.findViewById(R.id.device_card_bluetooth_image);
            this.nameLabel = itemView.findViewById(R.id.device_card_name_label);
            this.manufacturerLabel = itemView.findViewById(R.id.device_card_manufacturer_label);
            this.connectButton = itemView.findViewById(R.id.device_card_connect_button);

//            this.setIsConnectable(true);
        }

//        private void setIsConnectable(boolean isConnectable) {
//
//            this.connectButton.setEnabled(isConnectable);
//        }
    }
}
