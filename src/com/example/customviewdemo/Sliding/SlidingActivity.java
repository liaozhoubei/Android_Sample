package com.example.customviewdemo.Sliding;

import com.example.customviewdemo.R;
import com.example.customviewdemo.R.layout;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
/**
 * 实现策划面板的视图
 * @author ASUS-H61M
 *
 */
public class SlidingActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_sliding);
	}
}
