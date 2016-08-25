package com.httpandparse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlSerializer;

import com.httpandparse.bean.SmsBean;

import android.content.Context;
import android.util.Xml;

/**
 * 生成XML和解析XML
 * 
 * @author ASUS-H61M
 *
 */
public class XMLParseAndSet {

	// 使用XmlSerializer来序列化xml文件
	public static ArrayList<SmsBean> XMLparse(Context context) {
		
		try {
			//0.获取短信数据
			ArrayList<SmsBean> allSms = SmsDao.getAllSms();
			//1.通过Xml获取一个XmlSerializer对象
			XmlSerializer xs = Xml.newSerializer();
			//2.设置XmlSerializer的一些参数，比如：设置xml写入到哪个文件中
			//os:xml文件写入流   encoding：流的编码
			xs.setOutput(context.openFileOutput("backupsms2.xml", Context.MODE_PRIVATE), "UTF-8");
			//3.序列化一个xml的声明头
			//encoding:xml文件的编码  standalone:是否独立
			xs.startDocument("utf-8", true);
			//4.序列化一个根节点的开始节点
			//namespace:命名空间  name： 标签的名称
			ArrayList<SmsBean> allSms1 = SmsDao.getAllSms();
			xs.startTag(null, "smss");
			for (SmsBean smsBean : allSms) {
				xs.startTag(null, "Sms");
				xs.attribute(null, "id", smsBean.id + "");
				
				xs.startTag(null, "num");
				xs.text(smsBean.num);
				xs.endTag(null, "num");
				
				xs.startTag(null, "msg");
				xs.text(smsBean.msg);
				xs.endTag(null, "null");
				
				xs.startTag(null, "data");
				xs.text(smsBean.date);
				xs.endTag(null, "data");
				
				xs.endTag(null, "Sms");
				smsBean.toString();
				allSms1.add(smsBean);
			}
			xs.endTag(null, "smss");
			xs.endDocument();
			return allSms1;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;

	}

	// 备份的逻辑
	public static String getSms() {
		//获取短信内容，list
		ArrayList<SmsBean> allSms = SmsDao.getAllSms();

		//将数据以xml格式封装到一个StringBuffer中
		StringBuffer sb = new StringBuffer();
		//封装一个声明头
		sb.append("<?xml version='1.0' encoding='utf-8' standalone='yes' ?>");
		//封装根节点
		sb.append("<Smss>");
		//循环遍历list集合封装所有的短信
		for (SmsBean smsBean : allSms) {

			sb.append("<Sms id = \""+smsBean.id+"\">");

			sb.append("<num>");
			sb.append(smsBean.num);
			sb.append("</num>");

			sb.append("<msg>");
			sb.append(smsBean.msg);
			sb.append("</msg>");


			sb.append("<date>");
			sb.append(smsBean.date);
			sb.append("</date>");

			sb.append("</Sms>");

		}
		sb.append("</Smss>");
		String xml = sb.toString();
		return xml;

	}

	// 解析xml文件读取短信内容
	public static int restoreSms(Context context) {
		return 0;

	}



}
