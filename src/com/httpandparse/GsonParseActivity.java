package com.httpandparse;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.httpandparse.GsonParseBean.SafeInfo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class GsonParseActivity extends Activity implements OnClickListener{
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
//			String jsonData = getJsonData(R.raw.mogujson);
			String jsonData = getData(R.raw.mogujson);
			Gson gson = new Gson();
			GsonParseBean mogujie = gson.fromJson(jsonData, GsonParseBean.class);
			ArrayList<SafeInfo> safe = mogujie.safe;
			for (SafeInfo info :safe){
				System.out.println(info);
			}
			ArrayList<String> screen = mogujie.screen;
			for (String string : screen) {
				System.out.println(string);
			}
			break;
			
		case R.id.bt_newjson:
			
			break;

		default:
			break;
		}
	}
	
	private String getJsonData(int id){
		try {
		InputStream inputStream = getResources().openRawResource(id);
		InputStreamReader isr = new InputStreamReader(inputStream);
		BufferedReader br = new BufferedReader(isr);
		String str = null;
		StringBuffer sb = new StringBuffer();
		
			while ((str = br.readLine()) != null) {
				sb.append(str);
			}
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String getData(int id){
		try {
		InputStream inputStream = getResources().openRawResource(id);
		BufferedInputStream bis = new BufferedInputStream(inputStream);
		byte[] b = new byte[1024];
		int len;
		StringBuffer sb = new StringBuffer();
		while ((len = bis.read()) != -1) {
			sb.append((char) len);
		}
		return sb.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private String getJson(int id){
		try {
		InputStream inputStream = getResources().openRawResource(id);
		BufferedInputStream bis = new BufferedInputStream(inputStream);
		int read;
		String str = null;
		StringBuffer sb = new StringBuffer();
		byte[] b = new byte[1024];
		while ((read = bis.read(b, 0, b.length)) != -1) {
			sb.append(b);
//			str += str;
		}
		return sb.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	

}
