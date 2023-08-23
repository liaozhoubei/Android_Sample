package com.httpandparse.gsonandjson;

import java.io.InputStream;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.httpandparse.R;
import com.httpandparse.R.id;
import com.httpandparse.R.layout;
import com.httpandparse.R.raw;
import com.httpandparse.bean.GsonParseMoGuBean;
import com.httpandparse.bean.GsonParseMoGuBean.SafeInfo;
import com.httpandparse.bean.NewBean;
import com.httpandparse.bean.NewsData;
import com.httpandparse.bean.NewsTab;
import com.httpandparse.utils.StreamUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class GsonParseActivity extends Activity implements OnClickListener {
	private TextView tv_gson;
	private Button bt_mogujson;
	private Button bt_newjson;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gsonparse);
		tv_gson = (TextView) findViewById(R.id.tv_gson);
		bt_mogujson = (Button) findViewById(R.id.bt_mogujson);
		bt_newjson = (Button) findViewById(R.id.bt_newjson);
		bt_mogujson.setOnClickListener(this);
		bt_newjson.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_mogujson:
			// 解析mogujson中的数据
			InputStream inputStream = getResources().openRawResource(R.raw.mogujson);
			String jsonData = StreamUtils.getDecodeString(inputStream, "utf-8");
			// 使用Gson解析
			Gson gson = new Gson();
			GsonParseMoGuBean mogujie = gson.fromJson(jsonData, GsonParseMoGuBean.class);
			ArrayList<SafeInfo> safe = mogujie.safe;
			for (SafeInfo info : safe) {
				System.out.println(info);
			}
			ArrayList<String> screen = mogujie.screen;
			for (String string : screen) {
				System.out.println(string);
			}
			// 显示部分数据，检验是否解析成功
			tv_gson.setText(safe.toString());

			break;

		case R.id.bt_newjson:
			// 解析newjson中的数据
			InputStream newjsonInputStream = getResources().openRawResource(R.raw.newjson);
			String newjson = StreamUtils.getString(newjsonInputStream);
			// 使用Gson解析
			Gson newjsonGson = new Gson();
			NewBean NewBean = newjsonGson.fromJson(newjson, NewBean.class);
			ArrayList<NewsData> newsDatas = NewBean.data;
			String childrenText = null;
			for (NewsData newData : newsDatas) {
				ArrayList<NewsTab> children = newData.children;
				if (children != null) {
					childrenText = children.toString();
				}
			}
			// 显示部分数据，检验查看是否解析成功
			tv_gson.setText(childrenText);
			break;

		default:
			break;
		}
	}

}
