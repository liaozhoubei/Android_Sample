package com.example.example.jetpck;

import android.os.SystemClock;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LiveDataTestModel extends ViewModel {

    private static final int ONE_SECOND = 1000;
    //新建一个LiveData实例
    private MutableLiveData<Long> mElapsedTime = new MutableLiveData<>();

    private long mInitialTime;

    public LiveDataTestModel() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    final long newValue =
                            (SystemClock.elapsedRealtime() - mInitialTime) / 1000;
                    mElapsedTime.postValue(newValue);
                }

            }
        }).start();
    }

    public MutableLiveData<Long> getmElapsedTime() {
        return mElapsedTime;
    }
}
