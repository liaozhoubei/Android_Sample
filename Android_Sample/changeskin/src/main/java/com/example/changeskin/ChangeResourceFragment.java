package com.example.changeskin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class ChangeResourceFragment extends Fragment {


    private TextView textView;
    private String TAG = "ChangeResourceFragment";
    private FrameLayout mFlSkinLayout;
    private LayoutEnum layoutEnum = LayoutEnum.layout_none;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_resource, container, false);
        textView = view.findViewById(R.id.tv_test);
        mFlSkinLayout = view.findViewById(R.id.fl_skin_layout);
        return view;
    }

    public void setTextView(String str){
        textView.setText(str);
    }

    public void onLayout(String themeName) {
        Log.d(TAG, "onLayout: preLayout is " + layoutEnum.getOrientation());
        if ("SkinDay".equals(themeName) && layoutEnum != LayoutEnum.Layout_one) {
            mFlSkinLayout.removeAllViews();

            ViewLayoutOne viewLayoutOne = new ViewLayoutOne(getContext());
            mFlSkinLayout.addView(viewLayoutOne);
            layoutEnum = LayoutEnum.Layout_one;
            Log.e(TAG, "onLayout: change layout");
        } else if ("SkinNight".equals(themeName) && layoutEnum != LayoutEnum.Layout_two) {
            mFlSkinLayout.removeAllViews();

            ViewLayoutTwo viewLayoutTwo = new ViewLayoutTwo(getContext());
            mFlSkinLayout.addView(viewLayoutTwo);
            layoutEnum = LayoutEnum.Layout_two;
            Log.e(TAG, "onLayout: change layout");
        }
    }
}