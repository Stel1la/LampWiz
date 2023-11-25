package com.sasha.lampwiz.activity.main.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sasha.lampwiz.R;
import com.sasha.lampwiz.room.DataBase;
import com.sasha.lampwiz.room.dao.DeviceDao;

import io.reactivex.disposables.Disposable;

public class ConnectionHistoryFragment extends Fragment {
    ConnectionHistoryAdapter adapter;
    HistoryViewModel historyViewModel;
    private Disposable subscription;

    public ConnectionHistoryFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_connection_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        DataBase db = DataBase.getInstance(requireContext());
        DeviceDao deviceDao = db.deviceDao();

        historyViewModel = new HistoryViewModel(deviceDao);
//        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        historyViewModel.fetchDevicesList();
        historyViewModel.getDevicesList().observe(getViewLifecycleOwner(), hotelResponseObservable -> {
            if (hotelResponseObservable != null) {
                subscription = hotelResponseObservable.subscribe(
                        response -> {
                            if (response != null) {
                                RecyclerView recyclerView = view.findViewById(R.id.rvDevices);
                                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

                                adapter = new ConnectionHistoryAdapter(response);
                                recyclerView.setAdapter(adapter);
                            } else {
                                historyViewModel.getResponseError().observe(getViewLifecycleOwner(), this::handleError);
                            }
                        },
                        error -> handleError(error.getMessage())
                );
            }
        });

    }

    private void handleError(String s) {
        Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
    }
}