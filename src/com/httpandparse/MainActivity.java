package com.httpandparse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.netfile).setOnClickListener(this);
		findViewById(R.id.webview).setOnClickListener(this);
		findViewById(R.id.getandpost).setOnClickListener(this);
		findViewById(R.id.json_parse).setOnClickListener(this);
		findViewById(R.id.gson_parse).setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.netfile:
			startActivity(new Intent(MainActivity.this, NetFileActivity.class));
			break;
		case R.id.webview:
			startActivity(new Intent(MainActivity.this, WebViewActivity.class));
			break;
		case R.id.getandpost:
			startActivity(new Intent(MainActivity.this, RegisterActivity.class));
			break;
		case R.id.json_parse:
			startActivity(new Intent(MainActivity.this, JsonParseActivity.class));
			break;
		case R.id.gson_parse:
			startActivity(new Intent(MainActivity.this, GsonParseActivity.class));
			break;

		default:
			break;
		}
	}
	
	
	

}
