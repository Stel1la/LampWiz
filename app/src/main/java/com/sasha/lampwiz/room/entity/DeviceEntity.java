package com.sasha.lampwiz.room.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "device_table")
public class DeviceEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo
    public String macAddress;

    @ColumnInfo
    public String name;

    @ColumnInfo
    public String date;
}
