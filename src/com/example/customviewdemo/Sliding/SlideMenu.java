package com.example.customviewdemo.Sliding;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class SlideMenu extends ViewGroup {
	private View leftMenu;
	private View mainContent;
	private float downX;
	private float moveX;
	private int cuttrenState;
	private int MAIN_CONTENT = 0;
	private int LEFT_MENU = 1;
	private Scroller scroller;
	
	public SlideMenu(Context context) {
		super(context);
		init();
	}

	public SlideMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		scroller = new Scroller(getContext());
	}



	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		 leftMenu = getChildAt(0);
		 leftMenu.measure(leftMenu.getLayoutParams().width,
		 heightMeasureSpec);
		
		 mainContent = getChildAt(1);
		 mainContent.measure(widthMeasureSpec, heightMeasureSpec);

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		 leftMenu = getChildAt(0);
		 leftMenu.layout(-leftMenu.getMeasuredWidth(), 0, 0, b);
		
		 mainContent = getChildAt(1);
		 mainContent.layout(l, t, r, b);

	}
	
	 @Override
	 public boolean onTouchEvent(MotionEvent event) {
	 switch (event.getAction()) {
	 case MotionEvent.ACTION_DOWN:
	 downX = event.getX();
	 break;
	
	 case MotionEvent.ACTION_MOVE:
	 moveX = event.getX();
	 int scorllX = (int) (downX - moveX);
	 int newpositionX = getScrollX() + scorllX;
	
	 if (newpositionX < -leftMenu.getMeasuredWidth()){
	 scrollTo(-leftMenu.getMeasuredWidth(), 0);
	 } else if (newpositionX > 0) {
	 scrollTo(0, 0);
	 } else {
	 scrollBy(scorllX, 0);
	 }
	 downX = moveX;
	 break;
	
	 case MotionEvent.ACTION_UP:
		 cuttrenState = 0;
		 int leftCenter = (-leftMenu.getMeasuredWidth() / 2);
		 if (getScrollX() <= leftCenter){
			 cuttrenState = LEFT_MENU;
			 updateCurrentContent();
		 } else if (getScrollX() > leftCenter){
			 cuttrenState = MAIN_CONTENT;
			 updateCurrentContent();
		 }
		 
	 break;
	
	 default:
	 break;
	 }
	
	 return true;
	 }

	private void updateCurrentContent() {
		int startX = getScrollX();
		int dx = 0;
		if (cuttrenState == LEFT_MENU){
//			scrollTo(-leftMenu.getMeasuredWidth(), 0);
			dx = - leftMenu.getMeasuredWidth() - startX;
		} else if (cuttrenState == MAIN_CONTENT){
//			scrollTo(0, 0);
			dx = 0 - startX;
		}
		int duration = Math.abs(dx * 2);
		scroller.startScroll(startX, 0, dx, 0, duration);
		invalidate();
	}
	
	@Override
	public void computeScroll() {
		super.computeScroll();
		if (scroller.computeScrollOffset()) {
			int currX = scroller.getCurrX();
			scrollTo(currX, 0);
			invalidate();
		}
	}
}
