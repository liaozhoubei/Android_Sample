package com.example.customviewdemo.Refreshlist;

import com.example.customviewdemo.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

public class RefreshListView extends ListView {

	private View mHeaderView;
	private int measuredHeight;
	private float downY;
	private float moveY;
	public static final int PULL_TO_REFRESH = 0;// 下拉刷新
	public static final int RELEASE_REFRESH = 1;// 释放刷新
	public static final int REFRESHING = 2; // 刷新中
	private int currentState = PULL_TO_REFRESH; // 当前刷新模式

	public RefreshListView(Context context) {
		super(context);
		init();
	}

	public RefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		initHeaderView();
		initAnimation();
	}

	private void initAnimation() {
		// TODO Auto-generated method stub
		
	}

	private void initHeaderView() {
		mHeaderView = View.inflate(getContext(), R.layout.layout_header_list, null);
		mHeaderView.measure(0, 0);
		measuredHeight = mHeaderView.getMeasuredHeight();
		System.out.println(measuredHeight + "");
		mHeaderView.setPadding(0, -measuredHeight, 0, 0);
		addHeaderView(mHeaderView);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downY = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			moveY = event.getY();
			float offset = moveY - downY;
			int paddingTop = (int) (-measuredHeight + offset);
			
			if (offset > 0 && getFirstVisiblePosition() == 0) {
				mHeaderView.setPadding(0, paddingTop, 0, 0);
			}
			if (paddingTop >= 0 && currentState != PULL_TO_REFRESH) {
				currentState = PULL_TO_REFRESH;
				updateHeaderView();
			} else if (paddingTop < 0 && currentState != RELEASE_REFRESH) {
				currentState = RELEASE_REFRESH;
				updateHeaderView();
			}
			break;
		case MotionEvent.ACTION_UP:
			mHeaderView.setPadding(0, - measuredHeight, 0, 0);
			break;

		default:
			break;
		}

		return super.onTouchEvent(event);
	}

	private void updateHeaderView() {
		// TODO Auto-generated method stub
		
	}

}
