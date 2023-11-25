package com.sasha.lampwiz.activity.main.ui.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sasha.lampwiz.R;
import com.sasha.lampwiz.room.entity.DeviceEntity;

import java.util.List;

public class ConnectionHistoryAdapter extends RecyclerView.Adapter<ConnectionHistoryAdapter.DeviceViewHolder> {

    private final List<DeviceEntity> deviceList;

    public ConnectionHistoryAdapter(List<DeviceEntity> deviceList) {
        this.deviceList = deviceList;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_device_item, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        DeviceEntity device = deviceList.get(position);

        holder.addressLabel.setText(device.macAddress);
        holder.nameLabel.setText(device.name);
        holder.dateLabel.setText(device.date);
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {
        // Define views in the item layout
        TextView addressLabel;
        TextView rssiLabel;
        TextView nameLabel;
        TextView dateLabel;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            addressLabel = itemView.findViewById(R.id.device_card_address_label);
            rssiLabel = itemView.findViewById(R.id.device_card_rssi_label);
            nameLabel = itemView.findViewById(R.id.device_card_name_label);
            dateLabel = itemView.findViewById(R.id.device_card_date);
        }
    }
}
