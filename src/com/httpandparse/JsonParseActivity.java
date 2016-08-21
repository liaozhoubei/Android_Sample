package com.httpandparse;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.Gson;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class JsonParseActivity extends Activity{
	
	private TextView tv_json;
	private Button bt_jsonparse;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jsonparse);
		tv_json = (TextView) findViewById(R.id.tv_json);
		bt_jsonparse = (Button) findViewById(R.id.bt_jsonparse);
		onClick();

	}
	
	public void onClick(){
		bt_jsonparse.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					StringBuffer buffer = new StringBuffer();
					InputStream openRawResource = getResources().openRawResource(R.raw.mogujson);
					InputStreamReader inputStreamReader = new InputStreamReader(openRawResource);
					BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
					String line = "";
					while ((line= bufferedReader.readLine()) != null) {
						buffer.append(line);
					}
					tv_json.setText(buffer.toString());
					
					Gson gson = new Gson();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
	}

}
