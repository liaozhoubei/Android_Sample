package com.example.customviewdemo.MyViewpager;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.example.customviewdemo.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * 实现轮播图的视图
 * 
 * @author ASUS-H61M
 *
 */
public class MyViewpager extends Activity {
	private int[] imageResIds;
	private String[] contentDescs;
	private ViewPager mViewPager;
	private TextView mTv_desc;
	private LinearLayout mLl_point_container;
	private ArrayList<ImageView> mImageViewList;
	private int previousSelectedPosition = 0;
	private Timer timer;
	private MyPagerListener myPagerListener;
	private boolean isRunning = true; // 判断Activity是否仍在运行，以便在Activity销毁时结束自动循环
	private long delay = 3 * 1000; // 设置轮播图延时时间

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_myviewpager);
		initView();
		initData();
		initAdapter();
		
		 // 第一种实现viewpager自动循环的方法
//		new Thread() {
//			public void run() {
//				// isRunning为true的时候执行循环动画
//				while (isRunning) {
//					SystemClock.sleep(delay);
//
//					runOnUiThread(new Runnable() {
//
//						@Override
//						public void run() {
//							mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
//						}
//					});
//				}
//
//			};
//		}.start();
		
		// 第二种实现viewpager自动循环的方法
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
					}
				});

			}
		}, 2000, delay); // 第一次仔细在两秒之后，然后每个3秒执行一次
	}

	private void initView() {
		// 初始化视图
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mTv_desc = (TextView) findViewById(R.id.tv_desc);
		mLl_point_container = (LinearLayout) findViewById(R.id.ll_point_container);
		myPagerListener = new MyPagerListener();
		mViewPager.addOnPageChangeListener(myPagerListener);
	}

	private void initData() {
		// 初始化要显示的数据

		// 图片资源id数组
		imageResIds = new int[] { R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e };

		// 文本描述
		contentDescs = new String[] { "巩俐不低俗，我就不能低俗", "扑树又回来啦！再唱经典老歌引万人大合唱", "揭秘北京电影如何升级", "乐视网TV版大派送", "热血屌丝的反杀" };
		
		mImageViewList = new ArrayList<ImageView>();
		ImageView imageView;
		View pointView;
		for(int i = 0; i < imageResIds.length; i++) {
			imageView = new ImageView(getApplicationContext());
			// 设置viewpager的图像
			imageView.setBackgroundResource(imageResIds[i]);
			mImageViewList.add(imageView);
			// 创建小圆点，当可用的时候为白色，不可用的时候为灰色
			pointView = new View(getApplicationContext());
			// 设置小圆点的图像
			pointView.setBackgroundResource(R.drawable.selector_bg_point);
			// 设置小圆点的大小
			LayoutParams params = new LayoutParams(5, 5);
			if (i != 0) {
				// 当小圆点不是处于第一位的时候每个相隔10像素
				params.leftMargin = 10;
			}
			// 设置小圆点为不可以，当前状态为灰色
			pointView.setEnabled(false);
			mLl_point_container.addView(pointView, params);
			
		}
		
	}
	
	private void initAdapter() {
		// 设置小圆点小白点第一个位置的为白色
		mLl_point_container.getChildAt(0).setEnabled(true);
		// 设置开始时的文字标题
		mTv_desc.setText(contentDescs[0]);
		// 初始化上一次记录位置为0
		previousSelectedPosition = 0;
		mViewPager.setAdapter(new MyPagerAdapter());
		// 使用Integer.MAX_VALUE可能会产生BUG，因此可以直接使用500000
		int position = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2 % mImageViewList.size());
		// 设置viewpager当前的条目位置，设置大数值充当无限循环的角色
		mViewPager.setCurrentItem(5000000);
	}
	
	private class MyPagerAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			// 固定写法
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// 设置但position大于mImageViewList.size()的时候重新从0开始，避免数组越界
			int newPosition = position % mImageViewList.size();
			// 设置viewpager的图片
			ImageView imageView = mImageViewList.get(newPosition);
			container.addView(imageView);
			return imageView;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// 固定写法，移除视图
			container.removeView((View) object);
		}
		
	}
	
	private class MyPagerListener implements OnPageChangeListener{

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			// 滑动的时候调用
		}

		@Override
		public void onPageSelected(int position) {
			// 设置但position大于mImageViewList.size()的时候重新从0开始
			int newPosition = position % mImageViewList.size();
			// 设置标题和白色小圆点的位置
			mTv_desc.setText(contentDescs[newPosition]);			
			mLl_point_container.getChildAt(previousSelectedPosition).setEnabled(false);
			mLl_point_container.getChildAt(newPosition).setEnabled(true);
			previousSelectedPosition = newPosition;			
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			// 滑动状态发生改变的时候调用
		}
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
//		isRunning = false;
		//界面销毁时结束定时任务
		timer.cancel();
		// 结束监听页面滑动
		mViewPager.removeOnPageChangeListener(myPagerListener);
		
	}

}
