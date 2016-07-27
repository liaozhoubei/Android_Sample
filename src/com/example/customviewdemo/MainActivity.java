package com.example.customviewdemo;

import com.example.customviewdemo.Animationmenu.AnimationMenuActivity;
import com.example.customviewdemo.MyPopupwindow.MyPopupwindowActivity;
import com.example.customviewdemo.MyViewpager.MyViewpagerActivity;
import com.example.customviewdemo.Refreshlist.RefreshlistActivity;
import com.example.customviewdemo.Sliding.SlidingActivity;
import com.example.customviewdemo.Toggleview.ToggleActivity;

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
	
	
	public void AnimationMenu(View v) {
		startActivity(new Intent(MainActivity.this, AnimationMenuActivity.class));
	}
	
	public void MyViewpager(View v) {
		startActivity(new Intent(MainActivity.this, MyViewpagerActivity.class));
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
		startActivity(new Intent(MainActivity.this, MyPopupwindowActivity.class));
	}

	public void ProgressBar(View v) {
		startActivity(new Intent(MainActivity.this, ProgressBarActivity.class));
	}


}
