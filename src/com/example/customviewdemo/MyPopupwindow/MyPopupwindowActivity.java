package com.example.customviewdemo.MyPopupwindow;

import java.util.ArrayList;

import com.example.customviewdemo.R;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
/**
 * 实现下拉选择框的视图
 * @author ASUS-H61M
 *
 */
public class MyPopupwindowActivity extends Activity implements OnClickListener, OnItemClickListener{
	private EditText et_input;
	private ImageView ib_dropdown;
	private ListView listView;
	private ArrayList<String> data;
	private PopupWindow popupWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mypopupwindow);
		
		et_input = (EditText) findViewById(R.id.et_input);
		ib_dropdown = (ImageView) findViewById(R.id.ib_dropdown);
		ib_dropdown.setOnClickListener(this);
	}



	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.ib_dropdown){
			showPopupWindow();
		}
	}

	private void showPopupWindow() {
		
		initListView();
		// 显示下拉选择框
		popupWindow = new PopupWindow(listView, et_input.getWidth(), 300);
		// 设置点击外部区域, 自动隐藏
		popupWindow.setOutsideTouchable(true);// 外部可触摸
		popupWindow.setBackgroundDrawable(new BitmapDrawable());// 设置空的背景, 响应点击事件
		popupWindow.setFocusable(true); //设置可获取焦点
		popupWindow.showAsDropDown(et_input, 0, 0);
	}
	// 初始化要显示的内容
	private void initListView() {
		listView = new ListView(getApplicationContext());
		listView.setDividerHeight(0);  // 设置分割线边距
		listView.setBackgroundResource(R.drawable.listview_background);
		
		listView.setOnItemClickListener(this);
		
		data = new ArrayList<String>();
		for (int i = 0; i < 30; i ++){
			// 添加数据
			String str = "1000" + i;
			data.add(str);
		}
		
		listView.setAdapter(new MyAdapter());		
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// listview条目点击事件
		String string = data.get(position);
		et_input.setText(string);
		popupWindow.dismiss();
	}

	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder viewHolder;
			if (convertView == null) {
				view = View.inflate(getApplicationContext(), R.layout.item_number, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_number = (ImageView) view.findViewById(R.id.iv_number);
				viewHolder.tv_number = (TextView) view.findViewById(R.id.tv_number);
				viewHolder.ib_delete = (ImageView) view.findViewById(R.id.ib_delete);
				view.setTag(viewHolder);
			} else {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			}
			viewHolder.tv_number.setText(data.get(position));
			
			viewHolder.ib_delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// 在条目中点击删除
					data.remove(position);
					notifyDataSetChanged();
					if (data.size() == 0) {
						// 当数据为0的时候隐藏popuwindow
						popupWindow.dismiss();
					}
				}
			});
			
			return view;
		}
		
		class ViewHolder{
			ImageView iv_number;
			TextView tv_number;
			ImageView ib_delete;
		}
		
	}






}
