package com.example.customviewdemo.Refreshlist;

import java.util.ArrayList;

import com.example.customviewdemo.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
/**
 * 实现上拉和下拉都能刷新的视图
 * @author ASUS-H61M
 *
 */
public class RefreshlistActivity extends Activity {
	private RefreshListView listview;
	private ArrayList<String> arraylist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_refreshlist);
		listview = (RefreshListView) findViewById(R.id.listview);
		

		arraylist = new ArrayList<String>();
		for (int i = 0 ; i < 30; i ++) {
			String str = "我是listview数据" + i;
			arraylist.add(str);
		}
		listview.setAdapter(new MyAdapter());
	}
	
	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return arraylist.size();
		}

		@Override
		public Object getItem(int position) {
			return arraylist.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView textView = new TextView(parent.getContext());
			textView.setText(arraylist.get(position));
			textView.setTextSize(18);
			
			
			return textView;
		}
		
	}
}
