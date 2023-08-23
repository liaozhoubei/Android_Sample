package com.example.example.studytouch;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.example.R;

/**
 * 学习android触摸事件
 * https://www.jianshu.com/p/7e310b01a174
 */
public class StudyTouchActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study_touch);
	}
	/**
     * onInterceptTouchEvent  拦截事件
     * dispatchTouchEvent 分派事件
     * onTouchEvent 处理事件
     */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		LogUtils.printLog(getClass(), "【开发经理】来一任务<%s>:需要{分派}给下一级", ev);
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean is=true;
		LogUtils.printLog(getClass(), "【开发经理】自己{处理}任务<%s>:连组长都不会，算了，这任务我还是自己来吧！"+is, event);
		return is;
	}
}
