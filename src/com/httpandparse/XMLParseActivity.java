package com.httpandparse;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class XMLParseActivity extends Activity implements OnClickListener {
	private TextView tv_xml;
	private Button bt_setxmlparse;
	private Button bt_xmlparse;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_xmlparse);
		tv_xml = (TextView) findViewById(R.id.tv_xml);
		bt_setxmlparse = (Button) findViewById(R.id.bt_setxmlparse);
		bt_xmlparse = (Button) findViewById(R.id.bt_xmlparse);
		bt_setxmlparse.setOnClickListener(this);
		bt_xmlparse.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_setxmlparse:
			XMLParseAndSet parseAndSet = new XMLParseAndSet();
			String sms = XMLParseAndSet.getSms();
			tv_xml.setText(sms);
			System.out.println(sms);
			break;

		case R.id.bt_xmlparse:
			XMLParseAndSet parseAndSet1 = new XMLParseAndSet();
			String sms1 = XMLParseAndSet.getSms();
			break;

		default:
			break;
		}
		
	}
	
	
}
