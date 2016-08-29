package com.httpandparse;

import com.httpandparse.thread.GetAndPost;

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
	private EditText et_password;
	private Button bt_get;
	private Button bt_post;
	// 192.168.43.73请更改为本机ip地址
	private String url = "http://192.168.43.73:8080/bei/servlet/LoginServlet";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		et_name = (EditText) findViewById(R.id.et_name);
		et_password = (EditText) findViewById(R.id.et_password);
		
		bt_get = (Button) findViewById(R.id.bt_get);
		bt_get.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						final String name = et_name.getText().toString().trim();
						final String password = et_password.getText().toString().trim();
						new GetAndPost(url, name, password).doGet();;
					}
				}).start();
			}
		});

		bt_post = (Button) findViewById(R.id.bt_post);	
		bt_post.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						final String name = et_name.getText().toString().trim();
						final String password = et_password.getText().toString().trim();
						new GetAndPost(url, name, password).doPost();;
					}
				}).start();
			}
		});
	}

}
