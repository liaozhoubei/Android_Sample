package com.httpandparse.gsonandjson;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 暂时无用
 */
import com.httpandparse.utils.StreamUtils;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;
/**
 * 获取WEB项目中的json数据和解析json的线程类
 * @author ASUS-H61M
 *
 */
public class Httpjson extends Thread {
	private String url;
	private Context context;
	private ListView listView;
	private JsonAdapter adapter;
	private Handler handler;

	public Httpjson(String url, ListView listView, JsonAdapter adapter, Handler handler) {
		super();
		this.url = url;
		this.listView = listView;
		this.adapter = adapter;
		this.handler = handler;
	}

	@Override
	public void run() {
		URL httpUrl;
		try {
			httpUrl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
			connection.setReadTimeout(5000);
			connection.setConnectTimeout(5000);
			connection.setRequestMethod("GET");
			int responseCode = connection.getResponseCode();
			String json = null;
			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream inputStream = connection.getInputStream();
				json = StreamUtils.getString(inputStream);
				System.out.println(json);
			}
			final List<Person> jsonParse = jsonParse(json);
			handler.post(new Runnable() {

				@Override
				public void run() {
					adapter.setData(jsonParse);
					listView.setAdapter(adapter);
				}
			});
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private List<Person> jsonParse(String json) {
		try {
			List<Person> personlist = new ArrayList<Person>();
			JSONObject jsonObject = new JSONObject(json);
			int result = jsonObject.getInt("result");
			if (result == 1) {
				JSONArray jsonArray = jsonObject.getJSONArray("personData");
				for (int i = 0; i < jsonArray.length(); i++) {
					Person person = new Person();
					JSONObject personData = jsonArray.getJSONObject(i);
					int age = personData.getInt("age");
					String url = personData.getString("url");
					String name = personData.getString("name");
					person.setAge(age);
					person.setName(name);
					person.setUrl(url);
					System.out.println(url);

					List<SchoolInfo> schoolInfolist = new ArrayList<SchoolInfo>();
					JSONArray schoolInfoArray = personData.getJSONArray("schoolInfo");
					for (int j = 0; j < schoolInfoArray.length(); j++) {
						JSONObject schoolInfojson = schoolInfoArray.getJSONObject(j);
						String schoolName = schoolInfojson.getString("School_name");
						SchoolInfo schoolInfo = new SchoolInfo();
						schoolInfo.setSchool_name(schoolName);
						schoolInfolist.add(schoolInfo);
					}
					person.setSchoolInfo(schoolInfolist);
					personlist.add(person);
				}
				return personlist;
			} else {
				Toast.makeText(context, "erro", Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("JsonParseActivity", "json解析出现了问题");
		}

		return null;
	}
}
