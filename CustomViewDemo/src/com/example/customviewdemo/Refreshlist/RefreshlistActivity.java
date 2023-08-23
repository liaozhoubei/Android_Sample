package com.example.customviewdemo.Refreshlist;

import java.util.ArrayList;

import com.example.customviewdemo.R;
import com.example.customviewdemo.Refreshlist.RefreshListView.OnRefreshListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 实现上拉和下拉都能刷新的视图
 * @author ASUS-H61M
 */
public class RefreshlistActivity extends Activity {
	private RefreshListView listview;
	private ArrayList<String> listDatas;
	private MyAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题
		setContentView(R.layout.activity_refreshlist);

		listview = (RefreshListView) findViewById(R.id.listview);

		listview.setRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				new Thread() {
					public void run() {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						listDatas.add(0, "我是下拉刷新出来的数据!");

						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								adapter.notifyDataSetChanged();
								listview.onRefreshComplete();
							}
						});
					};

				}.start();
			}

			@Override
			public void onLoadMore() {
				new Thread() {
					public void run() {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						listDatas.add("我是加载更多出来的数据!1");
						listDatas.add("我是加载更多出来的数据!2");
						listDatas.add("我是加载更多出来的数据!3");

						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								adapter.notifyDataSetChanged();
								listview.onRefreshComplete();
							}
						});
					};

				}.start();
			}

		});

		listDatas = new ArrayList<String>();
		for (int i = 0; i < 30; i++) {
			listDatas.add("这是一条ListView数据: " + i);
		}

		// 设置数据适配器

		adapter = new MyAdapter();
		listview.setAdapter(adapter);

	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return listDatas.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView textView = new TextView(parent.getContext());
			textView.setTextSize(18f);
			textView.setText(listDatas.get(position));

			return textView;
		}

		@Override
		public Object getItem(int position) {
			return listDatas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

	}
}
