package com.example.example.widget;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;


public interface Indicator extends ViewPager.OnPageChangeListener {
    @NonNull
    View getIndicatorView();

    IndicatorConfig getIndicatorConfig();

    void onPageChanged(int count, int currentPosition);

}
