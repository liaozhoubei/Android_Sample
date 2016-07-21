package com.example.customviewdemo;

import com.example.customviewdemo.MyPopupwindow.MyPopupwindow;
import com.example.customviewdemo.MyViewpager.MyViewpager;
import com.example.customviewdemo.Refreshlist.Refreshlist;
import com.example.customviewdemo.Sliding.Sliding;
import com.example.customviewdemo.Toggleview.Toggleview;
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
		startActivity(new Intent(MainActivity.this, Toggleview.class));
	}
	
	public void Refreshlist(View v) {
		startActivity(new Intent(MainActivity.this, Refreshlist.class));
	}
	
	public void Sliding(View v) {
		startActivity(new Intent(MainActivity.this, Sliding.class));
	}
	
	public void MyPopupwindow(View v) {
		startActivity(new Intent(MainActivity.this, MyPopupwindow.class));
	}



}
