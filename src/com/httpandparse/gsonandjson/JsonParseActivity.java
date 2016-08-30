package com.httpandparse.gsonandjson;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.httpandparse.R;
import com.httpandparse.R.id;
import com.httpandparse.R.layout;
import com.httpandparse.R.raw;
import com.httpandparse.bean.NewBean;
import com.httpandparse.bean.NewsData;
import com.httpandparse.bean.NewsTab;
import com.httpandparse.utils.StreamUtils;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
/**
 * 使用JsonObject解析JSON数据
 *
 */
public class JsonParseActivity extends Activity implements OnClickListener {

	private TextView tv_json;
	private Button bt_jsonparse;
	private Button bt_newjsonparse;
	private String url = "http://192.168.1.66:8080/bei/JsonServerServlet"; // 使用web项目组件本地服务器，此处为本机地址
	private ListView lv_jsonparse;
	private List<Person> personlists;
	private JsonAdapter adapter;
	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jsonparse);
		lv_jsonparse = (ListView) findViewById(R.id.lv_jsonparse);
		tv_json = (TextView) findViewById(R.id.tv_json);
		bt_jsonparse = (Button) findViewById(R.id.bt_jsonparse);
		bt_newjsonparse = (Button) findViewById(R.id.bt_newjsonparse);
		bt_jsonparse.setOnClickListener(this);
		bt_newjsonparse.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_jsonparse:
			// 从网络中获取JSON数据，完整json数据在raw文件夹下的netjson
			lv_jsonparse.setVisibility(View.VISIBLE);
			adapter = new JsonAdapter(JsonParseActivity.this);
			new Httpjson(url, lv_jsonparse, adapter, handler).start();
			
			break;
		case R.id.bt_newjsonparse:
			lv_jsonparse.setVisibility(View.GONE);
			
			InputStream newjsonString = getResources().openRawResource(R.raw.newjson);
			String jsonString = StreamUtils.getString(newjsonString);
			NewBean parseNewJson = parseNewJson(jsonString);
			
			ArrayList<NewsData> newsDatas = parseNewJson.data;
			String childrenText = null;
			for (NewsData newData : newsDatas) {
				ArrayList<NewsTab> children = newData.children;
				if (children != null) {
					childrenText = children.toString();
				}
			}
			// 显示部分数据，检验查看是否解析成功
			tv_json.setText(childrenText);
			System.out.println("childrenText中的数据" + childrenText);
			
			break;

		default:
			break;
		}
	}
	/**
	 * 使用JsonObject直接解析json数据
	 * @param json
	 * @return
	 */
	private NewBean parseNewJson(String json){
		try {
			JSONObject jsonObject = new JSONObject(json);
			int retcode = jsonObject.getInt("retcode");
			if (retcode == 200) {
				// 使用newBean来暂时保存json中的数据
				NewBean newBean = new NewBean();
				ArrayList<Integer> extendlist = new ArrayList<Integer>();
				
				JSONArray extendArray = jsonObject.getJSONArray("extend");
				for (int i = 0; i < extendArray.length(); i ++) {
					int extendData = extendArray.getInt(i);
					extendlist.add(extendData);
				}
				
				newBean.extend = extendlist;
				
				ArrayList<NewsData> datalist = new ArrayList<NewsData>();
				JSONArray dataArray = jsonObject.getJSONArray("data");
				for (int j = 0; j < dataArray.length(); j ++) {
					JSONObject dataObject = dataArray.getJSONObject(j);
					int id = dataObject.getInt("id");
					String title = dataObject.getString("title");
					String url = null;
					// 判断dataObject是否存在url属性
					if (dataObject.has("url")) {
						url = dataObject.getString("url");
					}
					
					NewsData newsData = new NewsData();
					newsData.id = id;
					newsData.title = title;
					newsData.url = url;
					
					ArrayList<NewsTab> childrenlist = new ArrayList<NewsTab>();
					if (dataObject.has("children")) {
						JSONArray childrenArray = dataObject.getJSONArray("children");
						for (int k = 0; k < childrenArray.length(); k ++) {
							NewsTab newsTab = new NewsTab();
							JSONObject children = childrenArray.getJSONObject(k);
							int childrenId = children.getInt("id");
							String childrenTitle = children.getString("title");
							String childrenUrl = children.getString("url");
							
							newsTab.id = childrenId;
							newsTab.title = childrenTitle;
							newsTab.url = childrenUrl;
							childrenlist.add(newsTab);
							newsData.children = childrenlist;
						}
					}
					datalist.add(newsData);
				}
				newBean.data = datalist;
				return newBean;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}


}
