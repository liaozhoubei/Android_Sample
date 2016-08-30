package com.httpandparse.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;

import com.httpandparse.utils.IOUtils;

/**
 * 使用Post和Get请求方式请求网络(在RegisterActivity中被使用)
 * @author ASUS-H61M
 *
 */
public class GetAndPost {

	private String name;
	private String password;
	private String url;

	public GetAndPost(String url, String name, String password) {
		super();
		this.name = name;
		this.password = password;
		this.url = url;
	}


	/**
	 * Get方式
	 */	
	public void doGet() {
		try {
			// 当出现乱码时，可能是由于编码错误引起，尝试改变编码格式，使用URLEncoder.encode()方法
			url = url + "?username=" + URLEncoder.encode(name, "UTF-8") + "&pwd=" + URLEncoder.encode(password, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader bufferedReader = null;
		try {
			URL httpUrl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
			// 设置读取超时
			connection.setReadTimeout(5000);
			// 设置链接超时时间
			connection.setConnectTimeout(5000);
			connection.setRequestMethod("GET");
			// 获得链接请求码
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				is = connection.getInputStream();
				isr = new InputStreamReader(is);
				bufferedReader = new BufferedReader(isr);
				String str = null;
				StringBuffer sb = new StringBuffer();
				while ((str = bufferedReader.readLine()) != null) {
					sb.append(str);
				}
				System.out.println("Get方式的result:" + sb.toString());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.close(is);
			IOUtils.close(isr);
			IOUtils.close(bufferedReader);
		}
	}

	/**
	 * Post方式
	 */
	public void doPost() {
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader bufferedReader = null;
		try {
			// 获得当前系统信息
//			Properties properties = System.getProperties();
//			properties.list(System.out);

			URL httpUrl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
			connection.setReadTimeout(5000);
			connection.setConnectTimeout(5000);
			connection.setRequestMethod("POST");
			String content = "username=" + name + "&pwd=" + password;
			// Content-Length： 请求内容的长度
			connection.setRequestProperty("Content-Length", content.length() + "");
			// Cache-Control： 控制HTTP缓存的方法， 详情可见：
			// http://www.cnblogs.com/yuyii/archive/2008/10/16/1312238.html
			connection.setRequestProperty("Cache-Control", "max-age=0");
			// Origin： 请求发起者
			connection.setRequestProperty("Origin", "http://192.168.2.124:8080");
			OutputStream outputStream = connection.getOutputStream();
			// 提交数据
			outputStream.write(content.getBytes());
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				is = connection.getInputStream();
				isr = new InputStreamReader(is);
				bufferedReader = new BufferedReader(isr);
				String str = null;
				StringBuffer sb = new StringBuffer();
				while ((str = bufferedReader.readLine()) != null) {
					sb.append(str);
				}
				System.out.println("Post方式的result:" + sb.toString());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.close(is);
			IOUtils.close(isr);
			IOUtils.close(bufferedReader);
		}
	}

}
