package com.httpandparse;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
/**
 * 使用Get或者Post方式请求的Activity
 * @author ASUS-H61M
 *
 */
public class RegisterActivity extends Activity {

	private EditText et_name;
	private EditText et_age;
	private Button bt_get;
	private Button bt_post;
	private String url = "http://192.168.2.124:8080/web/MyServlet";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		et_name = (EditText) findViewById(R.id.et_name);
		et_age = (EditText) findViewById(R.id.et_age);
		bt_get = (Button) findViewById(R.id.bt_get);
		bt_post = (Button) findViewById(R.id.bt_post);
		
		
		
		bt_get.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						final String name = et_name.getText().toString().trim();
						final String age = et_age.getText().toString().trim();
						new GetAndPost(url, name, age).doGet();;
					}
				}).start();
			}
		});

		
		bt_post.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						final String name = et_name.getText().toString().trim();
						final String age = et_age.getText().toString().trim();
						new GetAndPost(url, name, age).doPost();;
					}
				}).start();
			}
		});
	}

}
