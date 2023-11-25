package com.sasha.lampwiz.bluetooth;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Process;

import androidx.annotation.NonNull;

public class Connection extends Service implements Parcelable {

    public class ServiceBinder extends Binder {

        public Connection getService() {

            return Connection.this;
        }
    }

    public static final class ServiceIntent {

        private static final String DEVICE_TO_CONNECT = "com.sasha.lampwiz.bluetooth.Connection.ServiceIntent.device";

        public static Intent start(@NonNull Context context) {

            return new Intent(context, Connection.class);
        }

        public static Intent connect(@NonNull Context context, @NonNull Device device) {

            return new Intent(context, Connection.class).putExtra(ServiceIntent.DEVICE_TO_CONNECT, device);
        }

        public static Device deviceFor(@NonNull Intent intent) {

            Parcelable parcelable = intent.getParcelableExtra(ServiceIntent.DEVICE_TO_CONNECT);
            if (parcelable instanceof Device) {
                return (Device)parcelable;
            } else {
                return null;
            }
        }
    }

    private Handler handler;

    public Connection() {
    }

    protected Connection(Parcel in) {

    }

    public static final Creator<Connection> CREATOR = new Creator<Connection>() {

        @Override
        public Connection createFromParcel(Parcel in) {

            return new Connection(in);
        }

        @Override
        public Connection[] newArray(int size) {

            return new Connection[size];
        }
    };

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        /* TBD: no stateful data to persist? */
    }

    @Override
    public void onCreate() {

        super.onCreate();

        HandlerThread handlerThread = new HandlerThread(this.getClass().getName(), Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();

        this.handler = new Handler(handlerThread.getLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);

        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {

        return new ServiceBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {

        return false;
    }

    public boolean run(@NonNull Runnable runnable) {

        return this.handler.post(runnable);
    }
}
