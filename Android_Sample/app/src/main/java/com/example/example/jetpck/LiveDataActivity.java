package com.example.example.jetpck;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.example.R;

public class LiveDataActivity extends AppCompatActivity {


    private TextView mTvLivedata;
    private LiveDataTestModel liveDataTestModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_data);
        initView();
        liveDataTestModel = ViewModelProviders.of(this).get(LiveDataTestModel.class);
        subscribe();
    }

    private void subscribe() {
        liveDataTestModel.getmElapsedTime().observe(this, new Observer<Long>() {
            @Override
            public void onChanged(@Nullable Long aLong) {
                mTvLivedata.setText(aLong + "");
                Log.d("ChronoActivity3", "Updating timer");
            }
        });
    }

    private void initView() {
        mTvLivedata = (TextView) findViewById(R.id.tv_livedata);
    }
}
