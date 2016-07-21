package com.example.customviewdemo.MyViewpager;

import java.util.ArrayList;

import com.example.customviewdemo.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_myviewpager);
		initView();
		initData();
		initAdapter();
	}


	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mTv_desc = (TextView) findViewById(R.id.tv_desc);
		mLl_point_container = (LinearLayout) findViewById(R.id.ll_point_container);
		
		// 初始化要显示的数据

		// 图片资源id数组
		imageResIds = new int[] { R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e };

		// 文本描述
		contentDescs = new String[] { "巩俐不低俗，我就不能低俗", "扑树又回来啦！再唱经典老歌引万人大合唱", "揭秘北京电影如何升级", "乐视网TV版大派送", "热血屌丝的反杀" };
		
		mImageViewList = new ArrayList<ImageView>();
		ImageView imageView;
		for(int i = 0; i < imageResIds.length; i++) {
			imageView = new ImageView(MyViewpager.this);
			imageView.setBackgroundResource(imageResIds[i]);
			mImageViewList.add(imageView);

		}
	}

	private void initData() {

		
	}
	
	private void initAdapter() {
		mViewPager.setAdapter(new MyPagerAdapter());
	}
	
	private class MyPagerAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mImageViewList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			
			ImageView imageView = mImageViewList.get(position);
			container.addView(imageView);
			return imageView;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			
			container.removeView((View)object);
		}
		
		
	}

}
