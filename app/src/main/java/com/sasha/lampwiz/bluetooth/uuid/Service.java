package com.sasha.lampwiz.bluetooth.uuid;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.annotation.NonNull;

import com.sasha.lampwiz.Utility;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import static android.content.res.AssetManager.ACCESS_BUFFER;

public class Service {

    private static final String BLUETOOTH_SIG_BASE_UUID = "0000-1000-8000-00805F9B34FB";

    private static String SigUuidFrom16Bit(int uuid) {

        if (Utility.isValidUint(uuid) && (uuid <= 0xFFFF)) {
            return Utility.format("0000%04X-%s", uuid, Service.BLUETOOTH_SIG_BASE_UUID);
        } else {
            return null;
        }
    }

    @SuppressWarnings("FieldCanBeLocal")
    private final Properties properties;
    private final HashMap<ParcelUuid, String> serviceMap;

    @SuppressWarnings("SameParameterValue")
    public Service(@NonNull Context context, String properties, String keyPrefix) {

        AssetManager assetManager = context.getAssets();

        this.properties = new Properties();
        this.serviceMap = new HashMap<>();

        try (InputStream inputStream = assetManager.open(properties, ACCESS_BUFFER)) {

            this.properties.load(inputStream);

            if (!keyPrefix.endsWith(".")) {
                keyPrefix += ".";
            }

            for (String key : this.properties.stringPropertyNames()) {

                String normKey = Utility.lowerCase(key);

                if (normKey.startsWith(Utility.lowerCase(keyPrefix))) {

                    String subkey = normKey.substring(keyPrefix.length());
                    Integer uintKey = Utility.parseUint(subkey);
                    ParcelUuid uuid = null;

                    if (Utility.isValidUint(uintKey)) {
                        String sigUuid = Service.SigUuidFrom16Bit(uintKey);
                        if (null != sigUuid) {

                            try {
                                uuid = ParcelUuid.fromString(sigUuid);
                            } catch (Exception ignored) {
                            }
                        }
                    } else {
                        uuid = ParcelUuid.fromString(subkey);
                    }

                    if (null != uuid) {
                        this.serviceMap.put(uuid, this.properties.getProperty(key));
                    }
                }
            }
        } catch (Exception ex) {
            Log.d(this.getClass().getCanonicalName(), String.format("failed to load properties asset: \"%s\"", properties), ex);
        }
    }

    @SuppressWarnings("unused")
    public HashMap<ParcelUuid, String> serviceMap() {

        return this.serviceMap;
    }

    public String service(ParcelUuid key) {

        if (!this.serviceMap.containsKey(key)) {
            return null;
        }

        String attr = this.serviceMap.get(key);

        if (null == attr) {
            return "";
        }

        return attr;
    }
}
