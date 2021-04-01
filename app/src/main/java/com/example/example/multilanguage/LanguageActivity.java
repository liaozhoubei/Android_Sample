package com.example.example.multilanguage;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.example.R;

/**
 * 仅仅显示更换语言后是否会影响Activity
 */
public class LanguageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
    }
}
