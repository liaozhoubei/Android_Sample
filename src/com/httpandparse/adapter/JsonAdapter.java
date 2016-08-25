package com.httpandparse.adapter;

import java.util.List;

import com.httpandparse.HttpThread;
import com.httpandparse.HttpThread.NetListener;
import com.httpandparse.R;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 填充JsonParseActivity中listView的适配器
 * @author ASUS-H61M
 *
 */
public class JsonAdapter extends BaseAdapter{
	
	private Context context;
	private LayoutInflater layoutInflager;
	private List<Person> list;
	private Handler handler = new Handler();
	
	
	public JsonAdapter(Context context, List<Person> list) {
		super();
		this.context = context;
		this.list = list;
		layoutInflager = LayoutInflater.from(context);
	}

	
	public JsonAdapter(Context context) {
		super();
		this.context = context;
		layoutInflager = LayoutInflater.from(context);
	}
	
	public void setData(List<Person> list) {
		this.list = list;
	}

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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = layoutInflager.inflate(R.layout.json_item, null);
			viewHolder = new ViewHolder(convertView);			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		Person person = list.get(position);
		viewHolder.name.setText(person.getName());
		viewHolder.age.setText(String.valueOf(person.getAge()));
		List<SchoolInfo> schoolInfos = person.getSchoolInfo();
		viewHolder.school1.setText(schoolInfos.get(0).getSchool_name());
		viewHolder.school2.setText(schoolInfos.get(1).getSchool_name());
		new HttpImage(person.getUrl(), handler, viewHolder.imageView).start();
		
		return convertView;
	}
	
	
	class ViewHolder {
		ImageView imageView;
		TextView name;
		TextView age;
		TextView school1;
		TextView school2;
		
		public ViewHolder(View convertView) {
			imageView = (ImageView) convertView.findViewById(R.id.imageView);
			name = (TextView) convertView.findViewById(R.id.name);
			age = (TextView) convertView.findViewById(R.id.age);
			school1 = (TextView) convertView.findViewById(R.id.school1);
			school2 = (TextView) convertView.findViewById(R.id.school2);
		}
	}

}
