package com.example.example.studytouch;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class FirstFrameLayout extends FrameLayout {
	
	
	public FirstFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		LogUtils.printLog(getClass(), "【开发组长】来一任务<%s>:需要{分派}给下一级", ev);
		return super.dispatchTouchEvent(ev);
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		
		boolean is=false;
		LogUtils.printLog(getClass(), "【开发组长】自己分派一任务<%s>:需要{拦截}吗？"+is, ev);
		return is;
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean is=true;
		LogUtils.printLog(getClass(), "【开发组长】自己{处理}任务<%s>:这任务怎么这么难，底下人都不会，还是自己干吧。可是任务能解决嘛？"+is, event);
		return is;
	}
	
}
