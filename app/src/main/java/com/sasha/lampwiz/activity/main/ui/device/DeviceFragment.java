package com.sasha.lampwiz.activity.main.ui.device;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.sasha.lampwiz.R;
import com.sasha.lampwiz.Utility;
import com.sasha.lampwiz.activity.main.MainViewModel;

public class DeviceFragment extends PreferenceFragmentCompat {

    public static final String CAT_DEVICE_KEY = "com.sasha.lampwiz.preferences.category.device";
    public static final String PREF_DEVICE_KEY = "com.sasha.lampwiz.preferences.preference.device";
    public static final String PREF_AUTO_CONNECT_KEY = "com.sasha.lampwiz.preferences.preference.device_auto_connect";
    public static final String CAT_STRIP_KEY = "com.sasha.lampwiz.preferences.category.strip";
    public static final String PREF_STRIP_TYPE_KEY = "com.sasha.lampwiz.preferences.preference.strip_type";
    public static final String PREF_COLOR_ORDER_KEY = "com.sasha.lampwiz.preferences.preference.color_order";
    public static final String PREF_STRIP_LENGTH_KEY = "com.sasha.lampwiz.preferences.preference.strip_length";
    public static final String PREF_AUTO_SEND_KEY = "com.sasha.lampwiz.preferences.preference.device_auto_send";

    private MainViewModel mainViewModel;

    private boolean isInitialized = false;

    @SuppressWarnings("unused")
    public static DeviceFragment newInstance() {

        return new DeviceFragment();
    }

    public static void setPreference(@NonNull SharedPreferences sharedPreferences, @NonNull String key, Object value) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        switch (key) {
            case DeviceFragment.PREF_DEVICE_KEY:
                editor.putString(key, (String) value);
                break;
            case DeviceFragment.PREF_AUTO_CONNECT_KEY:
                if (value instanceof Boolean) {
                    editor.putString(key, value.toString());
                } else if (value instanceof String) {
                    editor.putString(key, (String) value);
                }
                break;
            case DeviceFragment.PREF_STRIP_TYPE_KEY:
            case DeviceFragment.PREF_COLOR_ORDER_KEY:
            case DeviceFragment.PREF_STRIP_LENGTH_KEY:
                if (value instanceof Integer) {
                    editor.putString(key, Utility.format("%d", value));
                } else if (value instanceof String) {
                    editor.putString(key, (String) value);
                }
                break;
        }
        editor.apply();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        this.setPreferencesFromResource(R.xml.device_config, rootKey);

        if (!this.isInitialized) {

            if (null == this.mainViewModel) {
                final FragmentActivity activity = this.getActivity();
                this.mainViewModel = new ViewModelProvider((null != activity) ? activity : this).get(MainViewModel.class);
            }

            PreferenceManager preferenceManager = this.getPreferenceManager();
            if (null != preferenceManager) {
                preferenceManager.getSharedPreferences();
            }

            this.findPreference(DeviceFragment.CAT_DEVICE_KEY);
            this.findPreference(DeviceFragment.PREF_DEVICE_KEY);
            this.findPreference(DeviceFragment.PREF_AUTO_CONNECT_KEY);
            this.findPreference(DeviceFragment.CAT_STRIP_KEY);
            this.findPreference(DeviceFragment.PREF_STRIP_TYPE_KEY);
            this.findPreference(DeviceFragment.PREF_COLOR_ORDER_KEY);
            this.findPreference(DeviceFragment.PREF_STRIP_LENGTH_KEY);
            this.findPreference(DeviceFragment.PREF_AUTO_SEND_KEY);
            this.isInitialized = true;
        }
    }
}
