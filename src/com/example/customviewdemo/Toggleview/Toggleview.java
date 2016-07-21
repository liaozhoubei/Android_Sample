package com.example.customviewdemo.Toggleview;

import com.example.customviewdemo.R;
import com.example.customviewdemo.R.layout;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
/**
 * 实现自定义开关按钮的视图
 * @author ASUS-H61M
 *
 */
public class Toggleview extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_toggleview);
	}
}
