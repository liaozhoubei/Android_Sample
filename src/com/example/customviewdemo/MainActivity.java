package com.example.customviewdemo;

import com.example.customviewdemo.MyPopupwindow.MyPopupwindow;
import com.example.customviewdemo.MyViewpager.MyViewpager;
import com.example.customviewdemo.Refreshlist.RefreshlistActivity;
import com.example.customviewdemo.Sliding.SlidingActivity;
import com.example.customviewdemo.Toggleview.ToggleActivity;
import com.example.customviewdemo.Youkumenu.Youkumenu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	
	public void Youkumenu(View v) {
		startActivity(new Intent(MainActivity.this, Youkumenu.class));
	}
	
	public void MyViewpager(View v) {
		startActivity(new Intent(MainActivity.this, MyViewpager.class));
	}
	
	public void Toggleview(View v) {
		startActivity(new Intent(MainActivity.this, ToggleActivity.class));
	}
	
	public void Refreshlist(View v) {
		startActivity(new Intent(MainActivity.this, RefreshlistActivity.class));
	}
	
	public void Sliding(View v) {
		startActivity(new Intent(MainActivity.this, SlidingActivity.class));
	}
	
	public void MyPopupwindow(View v) {
		startActivity(new Intent(MainActivity.this, MyPopupwindow.class));
	}

	public void ProgressBar(View v) {
		startActivity(new Intent(MainActivity.this, ProgressBarActivity.class));
	}


}
