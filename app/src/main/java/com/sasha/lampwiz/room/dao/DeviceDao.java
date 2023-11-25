package com.sasha.lampwiz.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.sasha.lampwiz.room.entity.DeviceEntity;

import java.util.List;

import io.reactivex.Observable;

@Dao
public interface DeviceDao {

    @Query("SELECT * FROM device_table")
    Observable<List<DeviceEntity>> getAll();

    @Insert
    void insert(DeviceEntity device);

}
