package com.example.customviewdemo.MyPopupwindow;

import com.example.customviewdemo.R;
import com.example.customviewdemo.R.layout;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
/**
 * 实现下拉选择框的视图
 * @author ASUS-H61M
 *
 */
public class MyPopupwindow extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mypopupwindow);
	}
}
