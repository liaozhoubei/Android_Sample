package com.example.customviewdemo.Toggleview;

import com.example.customviewdemo.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ToggleView extends View {
	private Bitmap mSlideButtonBitmap;
	private Bitmap mSwitchBackgroundBitmap; // 背景图片
	private Paint mPaint;
	private boolean mSwitchState = false; // 开关状态, 默认false
	private float mCurrentX; // 滑动的位置
	private boolean isTouchMode = false;
	private OnSwitchStateUpdateListener onSwitchStateUpdateListener;

	/**
	 * 用于代码创建控件
	 * @param context
	 */
	public ToggleView(Context context) {
		super(context);
		init();
	}
	/**
	 * 用于在xml里使用, 可指定自定义属性
	 * @param context
	 * @param attrs
	 */
	public ToggleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		
		// 第一种在获取配置的自定义属性，官方推荐
		TypedArray attrsArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ToggleView, 0,0); 
		// R.styleable.ToggleView是在attrs.xml中给定属性的名字，后两个为默认值，0代表不寻找默认值
		// 获取在XML中设置的布尔值
		mSwitchState = attrsArray.getBoolean(R.styleable.ToggleView_switch_state, false);
		// 获取从xml中得到的图片资源ID
		int switch_background = attrsArray.getResourceId(R.styleable.ToggleView_switch_background, -1);
		int slide_button = attrsArray.getResourceId(R.styleable.ToggleView_slide_button, -1);
		
		// 第二种获取配置的自定义属性
//		String namespace = "http://schemas.android.com/apk/res/com.example.customviewdemo";
//		int switch_background = attrs.getAttributeResourceValue(namespace, "switch_background", -1);
//		int slide_button = attrs.getAttributeResourceValue(namespace , "slide_button", -1);
//		mSwitchState = attrs.getAttributeBooleanValue(namespace, "switch_state", false);	
		
		setSwitchBackgroundResource(switch_background);
		setSlideButtonResource(slide_button);
		setSwitchState(mSwitchState);
	}

	/**
	 * 用于在xml里使用, 可指定自定义属性, 如果指定了样式, 则走此构造函数
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
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
	// Canvas 画布, 画板. 在上边绘制的内容都会显示到界面上.
	@Override
	protected void onDraw(Canvas canvas) {
		// 1. 绘制背景
		canvas.drawBitmap(mSwitchBackgroundBitmap, 0, 0, mPaint );
		// 2. 绘制滑块
		if (isTouchMode) {
	// 根据当前用户触摸到的位置画滑块
			
			// 让滑块向左移动自身一半大小的位置
			float newPositon = mCurrentX - mSlideButtonBitmap.getWidth() / 2.0f;
			float maxWidth = mSwitchBackgroundBitmap.getWidth() - mSlideButtonBitmap.getWidth();
			// 限定滑块范围
			if (newPositon < 0) {
				newPositon = 0; // 左边范围
			}
			if (newPositon > maxWidth) {
				newPositon = maxWidth; // 右边范围
			}
			
			canvas.drawBitmap(mSlideButtonBitmap, newPositon, 0, mPaint);
		} else {
			// 根据开关状态boolean, 直接设置图片位置
			if (mSwitchState){ // 开
				float maxWidth = mSwitchBackgroundBitmap.getWidth() - mSlideButtonBitmap.getWidth();
				canvas.drawBitmap(mSlideButtonBitmap, maxWidth, 0, mPaint);
			} else { // 关
				canvas.drawBitmap(mSlideButtonBitmap, 0, 0, mPaint);
			}
		}
		
	}
	
	// 重写触摸事件, 响应用户的触摸.
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
			// 根据当前按下的位置, 和控件中心的位置进行比较. 
			boolean state = mCurrentX > center;
			
			// 如果开关状态变化了, 通知界面. 里边开关状态更新了.
			if (state != mSwitchState && onSwitchStateUpdateListener != null){
				// 把最新的boolean, 状态传出去了
				onSwitchStateUpdateListener.onStateUpdate(state);
			}
			
			mSwitchState = state;
			break;

		default:
			break;
		}
		// 重绘界面
		invalidate(); // 会引发onDraw()被调用, 里边的变量会重新生效.界面会更新
		return true; // 消费了用户的触摸事件, 才可以收到其他的事件.
	}

	/**
	 * 设置背景图
	 * @param switchBackground
	 */
	public void setSwitchBackgroundResource(int switchBackground) {
		mSwitchBackgroundBitmap = BitmapFactory.decodeResource(getResources(), switchBackground);
	}
	/**
	 * 设置滑块图片资源
	 * @param slideButton
	 */
	public void setSlideButtonResource(int slideButton) {
		mSlideButtonBitmap = BitmapFactory.decodeResource(getResources(), slideButton);
	}
	public void setSwitchState(boolean mSwitchState) {
		this.mSwitchState = mSwitchState;
		
	}
	/**
	 * 设置开关状态
	 * @param b
	 */
	public interface OnSwitchStateUpdateListener{
		// 状态回调, 把当前状态传出去
		void onStateUpdate(boolean state);
	}
	
	public void setOnSwitchStateUpdateListener(OnSwitchStateUpdateListener onSwitchStateUpdateListener){
		this.onSwitchStateUpdateListener = onSwitchStateUpdateListener;
	}

}
