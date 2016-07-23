package com.example.customviewdemo.Toggleview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class ToggleView extends View {
	private Bitmap mSlideButtonBitmap;
	private Bitmap mSwitchBackgroundBitmap;
	private Paint mPaint;
	private boolean mSwitchState = false;
	private float mCurrentX;
	private boolean isTouchMode = false;


	public ToggleView(Context context) {
		super(context);
		init();
	}
	
	public ToggleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	
	public ToggleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	
	private void init() {
		mPaint = new Paint();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(mSwitchBackgroundBitmap.getWidth(), mSwitchBackgroundBitmap.getHeight());
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(mSwitchBackgroundBitmap, 0, 0, mPaint );
		if (isTouchMode) {
			float newPositon = mCurrentX - mSlideButtonBitmap.getWidth() / 2.0f;
			float maxWidth = mSwitchBackgroundBitmap.getWidth() - mSlideButtonBitmap.getWidth();
			if (newPositon < 0) {
				newPositon = 0;
			}
			if (newPositon > maxWidth) {
				newPositon = maxWidth;
			}
			
			canvas.drawBitmap(mSlideButtonBitmap, newPositon, 0, mPaint);
		} else {
			if (mSwitchState){
				float maxWidth = mSwitchBackgroundBitmap.getWidth() - mSlideButtonBitmap.getWidth();
				canvas.drawBitmap(mSlideButtonBitmap, maxWidth, 0, mPaint);
			} else {
				canvas.drawBitmap(mSlideButtonBitmap, 0, 0, mPaint);
			}
		}
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isTouchMode = true;
			mCurrentX = event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			mCurrentX = event.getX();
			break;
		case MotionEvent.ACTION_UP:
			isTouchMode =false;
			mCurrentX = event.getX();
			
			float center = mSwitchBackgroundBitmap.getWidth() / 2;
			boolean state = mCurrentX > center;
			mSwitchState = state;
			break;

		default:
			break;
		}
		invalidate();
		return true;
	}

	
	public void setSwitchBackgroundResource(int switchBackground) {
		mSwitchBackgroundBitmap = BitmapFactory.decodeResource(getResources(), switchBackground);
	}

	public void setSlideButtonResource(int slideButton) {
		mSlideButtonBitmap = BitmapFactory.decodeResource(getResources(), slideButton);
	}
	public void setSwitchState(boolean mSwitchState) {
		this.mSwitchState = mSwitchState;
		
	}
	
//	private Bitmap switchBackgroupBitmap; // 背景图片
//	private Bitmap slideButtonBitmap; // 滑块图片
//	private Paint paint; // 画笔
//	private float currentX;
//	private boolean mSwitchState = false;
//
//
//	public ToggleView(Context context) {
//		super(context);
//		init();
//	}
//
//	
//	public ToggleView(Context context, AttributeSet attrs) {
//		super(context, attrs);
//		init();
//
//	}
//
//
//	public ToggleView(Context context, AttributeSet attrs, int defStyle) {
//		super(context, attrs, defStyle);
//		init();
//	}
//	
//	private void init() {
//		paint = new Paint();
//	}
//
//	
//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		setMeasuredDimension(switchBackgroupBitmap.getWidth(), switchBackgroupBitmap.getHeight());
//	}
//
//	@Override
//	protected void onDraw(Canvas canvas) {
//		canvas.drawBitmap(switchBackgroupBitmap, 0, 0, paint);
//		
//		
//		if(isTouchMode){
//			float newLeft = currentX;
//			
//
//			
//			canvas.drawBitmap(slideButtonBitmap, newLeft, 0, paint);
//		}else {
//			if(mSwitchState){
//				int newLeft = switchBackgroupBitmap.getWidth() - slideButtonBitmap.getWidth();
//				canvas.drawBitmap(slideButtonBitmap, newLeft, 0, paint);
//			}else {
//				canvas.drawBitmap(slideButtonBitmap, 0, 0, paint);
//			}
//		}
//		
//	}
//	
//	boolean isTouchMode = false;
//	
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			isTouchMode = true;
//			currentX = event.getX();
//			break;
//		case MotionEvent.ACTION_MOVE:
//			currentX = event.getX();
//			break;
//		case MotionEvent.ACTION_UP:
//			isTouchMode = false;
//			currentX = event.getX();
//
//			break;
//
//		default:
//			break;
//		}
//
//		invalidate(); 
//		
//		return true; 
//	}
//
//	public void setSwitchBackgroundResource(int switchBackground) {
//		switchBackgroupBitmap = BitmapFactory.decodeResource(getResources(), switchBackground);
//	}
//
//	public void setSlideButtonResource(int slideButton) {
//		slideButtonBitmap = BitmapFactory.decodeResource(getResources(), slideButton);
//	}
//	public void setSwitchState(boolean mSwitchState) {
//		this.mSwitchState = mSwitchState;
//		
//	}


}
