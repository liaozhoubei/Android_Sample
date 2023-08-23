package com.httpandparse.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.httpandparse.R;
import com.httpandparse.R.id;
import com.httpandparse.R.layout;
import com.httpandparse.R.raw;
import com.httpandparse.bean.SmsBean;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
/**
 * 解析XML的示例DEMO
 * @author ASUS-H61M
 *
 */
public class XMLParseActivity extends Activity implements OnClickListener {
	private TextView tv_xml;
	private Button bt_setxmlparse;
	private Button bt_xmlparse;
	private Button bt_domparse;
	private Button bt_saxparse;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_xmlparse);
		tv_xml = (TextView) findViewById(R.id.tv_xml);
		bt_setxmlparse = (Button) findViewById(R.id.bt_setxmlparse);
		bt_xmlparse = (Button) findViewById(R.id.bt_xmlparse);
		bt_domparse = (Button) findViewById(R.id.bt_domparse);
		bt_saxparse = (Button) findViewById(R.id.bt_saxparse);
		bt_setxmlparse.setOnClickListener(this);
		bt_xmlparse.setOnClickListener(this);
		bt_domparse.setOnClickListener(this);
		bt_saxparse.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_setxmlparse:
			ArrayList<SmsBean> xmLparse = XMLParseAndSet.XMLparse(XMLParseActivity.this);
			tv_xml.setText("生成XML ： " + xmLparse.toString());

			break;

		case R.id.bt_xmlparse:
			ArrayList<SmsBean> parseXMLWithPull = XMLParseAndSet.parseXMLWithPull(XMLParseActivity.this);
			String string = parseXMLWithPull.toString();
			tv_xml.setText("pull解析 ： " + string);
			break;
		case R.id.bt_domparse:
			InputStream inputStream = getResources().openRawResource(R.raw.books);
			ArrayList<Book> parseXMLWithDOM = XMLParseAndSet.parseXMLWithDOM(inputStream);
			tv_xml.setText("DOM解析XML ： " + parseXMLWithDOM.toString());
			break;
		case R.id.bt_saxparse:
			InputStream in = getResources().openRawResource(R.raw.books);
			ArrayList<Book> parseXMLWithSAX = XMLParseAndSet.parseXMLWithSAX(in);
				tv_xml.setText("SAX解析XML ： " + parseXMLWithSAX.toString());
			break;

		default:
			break;
		}

	}

}
