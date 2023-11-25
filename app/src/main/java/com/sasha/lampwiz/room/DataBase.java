package com.sasha.lampwiz.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.sasha.lampwiz.room.dao.DeviceDao;
import com.sasha.lampwiz.room.entity.DeviceEntity;

@Database(entities = {DeviceEntity.class}, version = 1)
abstract public class DataBase extends RoomDatabase {
    private static final String dbName = "bluetoothDB";
    private static volatile DataBase databaseInstance;

    public static synchronized DataBase getInstance(Context context) {
        if (databaseInstance == null) {
            databaseInstance = Room.databaseBuilder(context.getApplicationContext(),
                            DataBase.class, dbName)
                    .build();
        }
        return databaseInstance;
    }

    public abstract DeviceDao deviceDao();
}
