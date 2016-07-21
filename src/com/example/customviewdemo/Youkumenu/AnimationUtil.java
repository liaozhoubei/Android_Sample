package com.example.customviewdemo.Youkumenu;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;

public class AnimationUtil {
	// 数值大小在动画运行时会变化，大于0表示动画开始了，等于或小于0位动画结束了
	public static int runningAnimationCount = 0;

	public static void RotateAnimationOut(RelativeLayout layout, long delay) {
		// 获得layout视图中子控件的个数
		int childCount = layout.getChildCount();
		for (int i = 0; i < childCount; i ++) {
			// 设置layout子控件为不可点击
			layout.getChildAt(i).setEnabled(false);
		}
		// 设置基于自身的选择动画
		RotateAnimation rotateAnimation = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1.0f);
		rotateAnimation.setDuration(500);
		// 设置动画的延时时间
		rotateAnimation.setStartOffset(delay);
		// 设置动画停留在结束位置
		rotateAnimation.setFillAfter(true);
		// 设置动画的监听
		rotateAnimation.setAnimationListener(new MyAnimationLisenter());
		layout.startAnimation(rotateAnimation);
	}

	public static void RotateAnimationIn(RelativeLayout layout, long delay) {
		// 获得layout视图中子控件的个数
		int childCount = layout.getChildCount();
		for (int i = 0; i < childCount; i ++) {
			// 设置layout子控件为不可点击
			layout.getChildAt(i).setEnabled(true);
		}
		RotateAnimation rotateAnimation = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1.0f);
		rotateAnimation.setDuration(500);
		rotateAnimation.setStartOffset(delay);
		rotateAnimation.setFillAfter(true);
		layout.startAnimation(rotateAnimation);		
	}

	private static class MyAnimationLisenter implements AnimationListener{

		@Override
		public void onAnimationStart(Animation animation) {
			runningAnimationCount++;			
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			runningAnimationCount--;			
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			
		}
		
	}
}
