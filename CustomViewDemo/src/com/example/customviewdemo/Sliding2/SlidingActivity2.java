package com.example.customviewdemo.Sliding2;

import com.example.customviewdemo.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
/**
 * 第二种设置实现侧滑面板的视图的方式
 * @author ASUS-H61M
 *
 */
public class SlidingActivity2 extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_sliding2);
	}

}
