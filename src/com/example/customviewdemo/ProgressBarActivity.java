package com.example.customviewdemo;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.ProgressBar;

public class ProgressBarActivity extends Activity {
	private ProgressBar progressBar1;
	private int progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_progressbar);
		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
		progressBar1.setProgress(0);
	}

	public void startProgress(View v) {
		progress = 0;
		new Thread() {
			public void run() {
				for (int i = 0; i <= 100; i++) {
					SystemClock.sleep(100);
					// 设置进度条进度
					progress++;
					// 更新进度条
					progressBar1.setProgress(progress);
				}
			};
		}.start();

	}

}
