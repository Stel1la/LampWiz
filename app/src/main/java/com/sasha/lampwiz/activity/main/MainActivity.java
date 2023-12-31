package com.sasha.lampwiz.activity.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.google.android.material.navigation.NavigationView;
import com.sasha.lampwiz.R;
import com.sasha.lampwiz.Utility;
import com.sasha.lampwiz.activity.scan.ScanActivity;
import com.sasha.lampwiz.bluetooth.Connection;
import com.sasha.lampwiz.bluetooth.Device;

public class MainActivity extends AppCompatActivity implements MainViewModel.ForegroundRunner {

    private static final long SNACKBAR_NOTIFY_DURATION = 5000;

    private boolean isInitialized = false;
    private boolean isResumed = false;

    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

    private MainViewModel mainViewModel;

    private boolean isConnectedToDevice = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.layout_main);

        if (!this.isInitialized) {

            this.mainViewModel = this.initViewModel();

            Toolbar toolbar = this.findViewById(R.id.main_toolbar);
            this.setSupportActionBar(toolbar);

            DrawerLayout drawer = this.findViewById(R.id.main_drawer_layout);
            NavigationView navigationView = this.findViewById(R.id.main_nav_view);
            CoordinatorLayout mainLayout = this.findViewById(R.id.main_layout);

            this.mainViewModel.connectedDevice().observe(this, device -> {

                MenuItem deviceGroupMenuItem = navigationView.getMenu().findItem(R.id.nav_group_device);
                MenuItem deviceMenuItem = navigationView.getMenu().findItem(R.id.nav_device);

                final boolean isConnectedToDevice = null != device;
                if (this.isConnectedToDevice != isConnectedToDevice) {
                    this.isConnectedToDevice = isConnectedToDevice;
                    if (this.isResumed) {
                        if (this.isConnectedToDevice) {
                            deviceMenuItem.setTitle(device.displayName());
                            Utility.makeSnackBar(mainLayout, (int) MainActivity.SNACKBAR_NOTIFY_DURATION,
                                    Utility.format(this.getString(R.string.snackbar_connected_format), device.shortDescription())).show();
                        } else {
                            deviceMenuItem.setTitle(this.getString(R.string.menu_device));
                            Utility.makeSnackBar(mainLayout, (int) MainActivity.SNACKBAR_NOTIFY_DURATION,
                                    Utility.format(this.getString(R.string.snackbar_disconnected_text))).show();
                        }
                    }
                }
                deviceGroupMenuItem.setVisible(this.isConnectedToDevice);
            });

            this.mainViewModel.isScanning().observe(this, isScanning -> {

                if (isScanning) {
                    this.startActivityForResult(
                            new Intent(this, ScanActivity.class), ScanActivity.REQUEST_DEVICE_SCAN
                    );
                    this.mainViewModel.setIsScanning(false);
                }
            });

            this.appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home, R.id.nav_color, R.id.nav_history, R.id.nav_device)
                    .setDrawerLayout(drawer)
                    .build();

            Fragment navHostFragment = this.getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            if (navHostFragment instanceof NavHostFragment) {
                this.navController = ((NavHostFragment) navHostFragment).getNavController();
                NavigationUI.setupActionBarWithNavController(this, this.navController, this.appBarConfiguration);
                NavigationUI.setupWithNavController(navigationView, this.navController);
            }

            PreferenceManager.setDefaultValues(this, R.xml.device_config, false);
            PreferenceManager.getDefaultSharedPreferences(this);

            this.isInitialized = true;
        }
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {

        super.onAttachFragment(fragment);
    }

    @Override
    protected void onPause() {

        super.onPause();

        this.isResumed = false;
    }

    @Override
    protected void onResume() {

        super.onResume();

        this.isResumed = true;

        MainViewModel.setForegroundRunner(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        View focusedView = this.getCurrentFocus();
        if (null != focusedView) {
            Utility.dismissKeyboard(this, focusedView);
            focusedView.clearFocus();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {

        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {

            case ScanActivity.REQUEST_DEVICE_SCAN:

                switch (resultCode) {

                    case ScanActivity.SCAN_RESULT_CONNECT:
                        if (null != intent) {
                            Device device = Connection.ServiceIntent.deviceFor(intent);
                            if (null != device) {
                                this.mainViewModel.setDevice(device);
                            }
                        }
                        break;

                    case ScanActivity.SCAN_RESULT_OK:
                    case ScanActivity.SCAN_RESULT_ERROR:
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem disconnectMenuItem = menu.findItem(R.id.disconnect_menu_item);
        disconnectMenuItem.setVisible(this.isConnectedToDevice);

        return super.onPrepareOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.scan_menu_item:
                this.mainViewModel.setIsScanning(true);
                return true;

            case R.id.disconnect_menu_item: {
                if (this.isConnectedToDevice) {
                    this.mainViewModel.setDevice(null);
                    navController.navigate(R.id.nav_home);
                }
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {

        if (null != this.navController) {
            return NavigationUI.navigateUp(this.navController, this.appBarConfiguration) || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }

    @Override
    public void runInForeground(@NonNull Runnable action) {

        this.runOnUiThread(action);
    }

    private MainViewModel initViewModel() {

        MainViewModel mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        Intent startServiceIntent = Connection.ServiceIntent.start(this);
        this.startService(startServiceIntent);
        this.bindService(startServiceIntent, mainViewModel, Context.BIND_IMPORTANT);

        return mainViewModel;
    }

}
