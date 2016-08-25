package com.httpandparse.adapter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;
/**
 * 下载图片并显示的线程类
 * @author Bei
 *
 */
public class HttpImage extends Thread{
	private Handler handler;
	private ImageView imageView;
	private String url;
	public HttpImage(String url, Handler handler, ImageView imageView) {
		super();
		this.handler = handler;
		this.imageView = imageView;
		this.url = url;
	}
	
	@Override
	public void run() {
		URL httpUrl;
		try {
			httpUrl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
			connection.setReadTimeout(5000);
			connection.setConnectTimeout(5000);
			connection.setRequestMethod("GET");
			int responseCode = connection.getResponseCode();
			System.out.println("链接的响应码" +responseCode);
			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream inputStream = connection.getInputStream();
				final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						imageView.setImageBitmap(bitmap);
					}
				});
				
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		super.run();
	}
	

}
