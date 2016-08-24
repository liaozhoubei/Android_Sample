package com.httpandparse.adapter;

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

public class Httpjson extends Thread {
	private String url;
	
	

	public Httpjson(String url) {
		this.url = url;
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
			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream inputStream = connection.getInputStream();
				String json = StreamUtils.getString(inputStream);
				System.out.println(json);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
			}
			return personlist;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
