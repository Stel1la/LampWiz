package com.sasha.lampwiz.activity.main.ui.history;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sasha.lampwiz.room.dao.DeviceDao;
import com.sasha.lampwiz.room.entity.DeviceEntity;

import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HistoryViewModel extends ViewModel {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final MutableLiveData<String> responseError = new MutableLiveData<>();
    public MutableLiveData<Observable<List<DeviceEntity>>> devicesList = new MutableLiveData<>();
    DeviceDao deviceDao;
    HistoryViewModel(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    public MutableLiveData<Observable<List<DeviceEntity>>> getDevicesList() {
        return devicesList;
    }

    public MutableLiveData<String> getResponseError() {
        return responseError;
    }

    void fetchDevicesList() {
        Disposable subscription = deviceDao.
                getAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        response -> {
                            if (response != null) {
                                devicesList.postValue(Observable.just(response));
                            }
                        },
                        error -> {
                            Exception httpException = (Exception) error;
                            String errorResponse = Objects.requireNonNull(Objects.requireNonNull(httpException.getMessage()));
                            Log.d("hotelsList", errorResponse);
                            responseError.postValue(errorResponse);
                        }
                );

        compositeDisposable.add(subscription);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }

}
