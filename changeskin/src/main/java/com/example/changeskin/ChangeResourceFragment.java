package com.example.changeskin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ChangeResourceFragment extends Fragment {


    private TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_resource, container, false);
        textView = view.findViewById(R.id.tv_test);
        return view;
    }

    public void setTextView(String str){
        textView.setText(str);
    }
}