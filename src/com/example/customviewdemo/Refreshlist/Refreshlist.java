package com.example.customviewdemo.Refreshlist;

import com.example.customviewdemo.R;
import com.example.customviewdemo.R.layout;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
/**
 * 实现上拉和下拉都能刷新的视图
 * @author ASUS-H61M
 *
 */
public class Refreshlist extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_refreshlist);
	}
}
