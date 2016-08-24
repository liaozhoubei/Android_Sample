package com.httpandparse.adapter;

import java.util.List;

import com.httpandparse.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class JsonAdapter extends BaseAdapter{
	
	private Context context;
	private LayoutInflater layoutInflager;
	private List<Person> list;
	
	
	public JsonAdapter(Context context, List<Person> list) {
		super();
		this.context = context;
		this.list = list;
		layoutInflager = LayoutInflater.from(context);
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
		person.getAge();
		person.getName();
		List<SchoolInfo> schoolInfos = person.getSchoolInfo();
		person.getUrl();
		
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
