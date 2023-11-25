package com.sasha.lampwiz.bluetooth.uuid;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.sasha.lampwiz.Utility;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import static android.content.res.AssetManager.ACCESS_BUFFER;

public class Manufacturer {

    @SuppressWarnings("FieldCanBeLocal")
    private final Properties properties;
    private final HashMap<Integer, String> manufacturerMap;

    @SuppressWarnings("SameParameterValue")
    public Manufacturer(@NonNull Context context, String properties, String keyPrefix) {

        AssetManager assetManager = context.getAssets();

        this.properties = new Properties();
        this.manufacturerMap = new HashMap<>();

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

                    if (Utility.isValidUint(uintKey)) {
                        this.manufacturerMap.put(uintKey, this.properties.getProperty(key));
                    }
                }
            }
        } catch (Exception ex) {
            Log.d(this.getClass().getCanonicalName(), String.format("failed to load properties asset: \"%s\"", properties), ex);
        }
    }

    @SuppressWarnings("unused")
    HashMap<Integer, String> manufacturerMap() {

        return this.manufacturerMap;
    }

    public String manufacturer(Integer key) {

        if (!this.manufacturerMap.containsKey(key)) {
            return null;
        }

        String attr = this.manufacturerMap.get(key);

        if (null == attr) {
            return "";
        }

        return attr;
    }
}
