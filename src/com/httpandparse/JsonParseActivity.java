package com.httpandparse;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class JsonParseActivity extends Activity implements OnClickListener{
	
	private TextView tv_json;
	private Button bt_mogujsonparse;
	private Button bt_newjsonparse;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jsonparse);
		tv_json = (TextView) findViewById(R.id.tv_json);
		bt_mogujsonparse = (Button) findViewById(R.id.bt_mogujsonparse);
		bt_newjsonparse = (Button) findViewById(R.id.bt_newjsonparse);
		bt_mogujsonparse.setOnClickListener(this);
		bt_newjsonparse.setOnClickListener(this);
	}
	


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_mogujsonparse:
			
			break;
		case R.id.bt_newjsonparse:
			
			break;


		default:
			break;
		}
	}

}
