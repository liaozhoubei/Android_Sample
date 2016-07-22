package com.example.customviewdemo.MyPopupwindow;

import java.util.ArrayList;

import com.example.customviewdemo.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
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
public class MyPopupwindow extends Activity implements OnClickListener{
	private EditText et_input;
	private ImageView ib_dropdown;
	private ListView listView;
	private ArrayList<String> list;
	private PopupWindow popupWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mypopupwindow);
		
		ininView();
	}

	private void ininView() {
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
		
		popupWindow = new PopupWindow(listView, et_input.getWidth(), 300);
		popupWindow.showAsDropDown(et_input, 0, 0);
		popupWindow.setOutsideTouchable(true);
	}
	
	private void initListView() {
		listView = new ListView(getApplicationContext());
		list = new ArrayList<String>();
		for (int i = 0; i < 30; i ++){
			String str = "1000" + i;
			list.add(str);
		}
		
		listView.setAdapter(new MyAdapter());		
	}

	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
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
			viewHolder.tv_number.setText(list.get(position));
			
			viewHolder.ib_delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					list.remove(position);
					if (list.size() == 0) {
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
