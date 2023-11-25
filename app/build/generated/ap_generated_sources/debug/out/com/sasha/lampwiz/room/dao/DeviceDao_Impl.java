package com.sasha.lampwiz.room.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.RxRoom;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.sasha.lampwiz.room.entity.DeviceEntity;
import io.reactivex.Observable;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({"unchecked", "deprecation"})
public final class DeviceDao_Impl implements DeviceDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DeviceEntity> __insertionAdapterOfDeviceEntity;

  public DeviceDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDeviceEntity = new EntityInsertionAdapter<DeviceEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `device_table` (`id`,`macAddress`,`name`,`date`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final DeviceEntity entity) {
        statement.bindLong(1, entity.id);
        if (entity.macAddress == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.macAddress);
        }
        if (entity.name == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.name);
        }
        if (entity.date == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.date);
        }
      }
    };
  }

  @Override
  public void insert(final DeviceEntity device) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfDeviceEntity.insert(device);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public Observable<List<DeviceEntity>> getAll() {
    final String _sql = "SELECT * FROM device_table";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return RxRoom.createObservable(__db, false, new String[] {"device_table"}, new Callable<List<DeviceEntity>>() {
      @Override
      public List<DeviceEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMacAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "macAddress");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final List<DeviceEntity> _result = new ArrayList<DeviceEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DeviceEntity _item;
            _item = new DeviceEntity();
            _item.id = _cursor.getInt(_cursorIndexOfId);
            if (_cursor.isNull(_cursorIndexOfMacAddress)) {
              _item.macAddress = null;
            } else {
              _item.macAddress = _cursor.getString(_cursorIndexOfMacAddress);
            }
            if (_cursor.isNull(_cursorIndexOfName)) {
              _item.name = null;
            } else {
              _item.name = _cursor.getString(_cursorIndexOfName);
            }
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _item.date = null;
            } else {
              _item.date = _cursor.getString(_cursorIndexOfDate);
            }
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
