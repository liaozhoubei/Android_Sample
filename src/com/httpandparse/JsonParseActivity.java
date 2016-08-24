package com.httpandparse;

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

import com.httpandparse.HttpThread.NetListener;
import com.httpandparse.adapter.HttpImage;
import com.httpandparse.adapter.Person;
import com.httpandparse.adapter.SchoolInfo;
import com.httpandparse.utils.StreamUtils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class JsonParseActivity extends Activity implements OnClickListener {

	private TextView tv_json;
	private Button bt_mogujsonparse;
	private Button bt_newjsonparse;
	private String url = "http://192.168.43.73:8080/web/JsonServerServlet";
//	private ListView lv_jsonparse;
	private List<Person> personlists;
	private ImageView jsonimavege;

	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jsonparse);
//		lv_jsonparse = (ListView) findViewById(R.id.lv_jsonparse);
		tv_json = (TextView) findViewById(R.id.tv_json);
		bt_mogujsonparse = (Button) findViewById(R.id.bt_mogujsonparse);
		bt_newjsonparse = (Button) findViewById(R.id.bt_newjsonparse);
		bt_mogujsonparse.setOnClickListener(this);
		bt_newjsonparse.setOnClickListener(this);
		jsonimavege = (ImageView) findViewById(R.id.jsonimavege);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_mogujsonparse:
			new Thread() {
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
//							System.out.println(json);
							personlists = jsonParse(json);
//							handler.sendEmptyMessage(0);
							String url2 = "http://192.168.43.73" + personlists.get(0).getUrl();
							System.out.println(url2);
//							new HttpImage(handler, jsonimavege, url2).start();
							new HttpThread(url2, handler, new NetListener() {
								
								@Override
								public void showView(String absolutePath) {
									Bitmap decodeFile = BitmapFactory.decodeFile(absolutePath);
									jsonimavege.setImageBitmap(decodeFile);
									System.out.println("执行了");
								}
							}).start();
						}
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					super.run();
				}
			}.start();
//			String url1 = "http://img.pconline.com.cn/images/upload/upc/tx/photoblog/1503/17/c2/3974346_1426551981202_mthumb.jpg";
//			new HttpImage(handler, jsonimavege, url1).start();
			
			break;
		case R.id.bt_newjsonparse:

			break;

		default:
			break;
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
					System.out.println();

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
