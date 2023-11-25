package com.sasha.lampwiz.activity.scan;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sasha.lampwiz.R;
import com.sasha.lampwiz.Utility;
import com.sasha.lampwiz.activity.scan.ui.ScanLayoutManager;
import com.sasha.lampwiz.bluetooth.Connection;
import com.sasha.lampwiz.bluetooth.Device;
import com.sasha.lampwiz.bluetooth.Scanner;
import com.sasha.lampwiz.bluetooth.scan.Result;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.sasha.lampwiz.room.DataBase;
import com.sasha.lampwiz.room.dao.DeviceDao;

public class ScanActivity extends AppCompatActivity implements Scanner.ScanListener, ScanAdapter.ConnectClickListener {

    public static final int REQUEST_BLUETOOTH_ENABLE = 0xB00E;
    public static final int REQUEST_BLUETOOTH_PERMISSION = 0xB00B;

    public static final int REQUEST_DEVICE_SCAN = 0xD00D;
    public static final int SCAN_RESULT_OK = 0xB0;
    public static final int SCAN_RESULT_ERROR = 0xB1;
    public static final int SCAN_RESULT_CONNECT = 0xB2;

    private SwipeRefreshLayout swipeRefreshLayout = null;
    private RecyclerView scanRecyclerView = null;
    private SearchView searchBar = null;
    private ScanAdapter scanAdapter = null;
    private FloatingActionButton refreshButton = null;
    private Snackbar scanningSnackBar = null;
    private Scanner scanner = null;
    private boolean didSwipeForRefresh = false;

    @Override
    protected void onStart() {

        super.onStart();

        if (null != this.scanner) {
            this.scanner.enableBluetooth();
        }
    }

    @Override
    protected void onStop() {

        super.onStop();

        if (null != this.scanner) {
            this.scanner.setIsScanning(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.layout_scan);

        Toolbar toolBar = this.findViewById(R.id.scan_toolbar);
        this.setSupportActionBar(toolBar);
        ActionBar actionBar = this.getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        this.searchBar = this.findViewById(R.id.scan_filter_search_view);
        this.searchBar.setOnClickListener(v -> this.searchBar.onActionViewExpanded());
        this.searchBar.setOnFocusChangeListener(
                (v, hasFocus) -> {
                    if (!hasFocus) {
                        Utility.dismissKeyboard(ScanActivity.this, v);
                    }
                }
        );

        this.swipeRefreshLayout = this.initSwipeLayout();

        DataBase dataBase = DataBase.getInstance(this);
        DeviceDao deviceDao = dataBase.deviceDao();
        Log.d("DeviceDao", deviceDao.toString());
        this.scanAdapter = new ScanAdapter(this, deviceDao);

        this.scanRecyclerView = this.findViewById(R.id.scan_recycler_view);
        this.scanRecyclerView.setLayoutManager(new ScanLayoutManager(this));
        this.scanRecyclerView.setAdapter(this.scanAdapter);

        this.refreshButton = this.findViewById(R.id.scan_refresh_fab);
        this.refreshButton.setOnClickListener(v -> this.scanner.setIsScanning(true));

        this.scanningSnackBar = Utility.makeSnackBar(this.refreshButton, (int)Scanner.SCAN_DURATION_MS,
                R.string.snackbar_scan_begin_text, R.string.snackbar_scan_stop_button, v -> this.scanner.setIsScanning(false));

        try {
            this.scanner = new Scanner(this);
        } catch (Exception ex) {
            this.displayFatalAlert("Failed to initialize", ex.getMessage());
        }

        this.setResult(ScanActivity.SCAN_RESULT_OK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        //noinspection SwitchStatementWithTooFewBranches
        switch (requestCode) {

            case ScanActivity.REQUEST_BLUETOOTH_ENABLE:
                if (!this.scanner.onEnableBluetoothActivityResult(resultCode)) {
                    displayFatalAlert("Failed to initialize", this.getString(R.string.fatal_alert_bluetooth_not_enabled));
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //noinspection SwitchStatementWithTooFewBranches
        switch (requestCode) {

            case ScanActivity.REQUEST_BLUETOOTH_PERMISSION:
                if (!this.scanner.onPermitBluetoothScanPermissionsResult(grantResults)) {
                    displayFatalAlert("Permission denied", this.getString(R.string.fatal_alert_bluetooth_scan_not_permitted));
                }
                break;
        }
    }

    private SwipeRefreshLayout initSwipeLayout() {

        SwipeRefreshLayout layout = this.findViewById(R.id.scan_swipe_refresh_layout);

        layout.setOnRefreshListener(() -> {
            this.didSwipeForRefresh = true;
            this.scanner.setIsScanning(true);
        });

        layout.setProgressBackgroundColorSchemeResource(
                R.color.color_swipe_progress_background
        );

        layout.setColorSchemeResources(
                R.color.color_swipe_progress_foreground1,
                R.color.color_swipe_progress_foreground2,
                R.color.color_swipe_progress_foreground3,
                R.color.color_swipe_progress_foreground4
        );

        return layout;
    }

    public AppCompatActivity scanActivity() {

        return this;
    }

    public void onScanReady() {

        this.scanner.setIsScanning(true);
    }

    public void onScanStart() {

        if (!this.didSwipeForRefresh) {
            this.swipeRefreshLayout.setRefreshing(true);
        }
        this.scanAdapter.clear();
        this.refreshButton.setVisibility(View.GONE);
        this.scanningSnackBar.show();
    }

    public void onScanStop() {

        this.swipeRefreshLayout.setRefreshing(false);
        this.didSwipeForRefresh = false;
        this.refreshButton.setVisibility(View.VISIBLE);
        this.scanningSnackBar.dismiss();
    }

    public void onScanResult(int callbackType, @NonNull Result result) {

        this.scanAdapter.add(result);

        if (1 == this.scanAdapter.getItemCount()) {
            this.scanRecyclerView.smoothScrollToPosition(0);
        }
    }

    public void onConnectClick(@NonNull Device device) {

        this.scanner.setIsScanning(false);

        Intent connectionIntent = Connection.ServiceIntent.connect(this.getApplicationContext(), device);
        this.setResult(ScanActivity.SCAN_RESULT_CONNECT, connectionIntent);
        this.finishAndRemoveTask();
    }

    private void displayFatalAlert(String title, String message) {

        this.displayFatalAlert(title, message, ScanActivity.SCAN_RESULT_ERROR);
    }

    @SuppressWarnings("SameParameterValue")
    private void displayFatalAlert(String title, String message, int result) {

        this.setResult(result);

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setNeutralButton(
                        this.getString(R.string.fatal_alert_button_text),
                        new FatalEventListener(this)
                )
                .create().show();
    }

    static class FatalEventListener implements DialogInterface.OnClickListener {

        private final ScanActivity activity;

        FatalEventListener(@NonNull ScanActivity activity) {

            this.activity = activity;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {

            this.activity.scanner.setIsScanning(false);
            this.activity.finishAndRemoveTask();
        }
    }
}
