package com.httpandparse.thread;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.webkit.WebView;
import android.widget.ImageView;
/**
 * 网络下载文件
 * @author ASUS-H61M
 *
 */
public class HttpThread extends Thread {

	private String url;
	private Handler handler;
	private NetListener netListener;

	public HttpThread(String url, Handler handler, NetListener netListener) {
		super();
		this.url = url;
		this.handler = handler;
		this.netListener = netListener;
	}

	@Override
	public void run() {
		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;
		try {
			URL httpUrl = new URL(url);
			try {
				// 访问Http协议使用HttpURLConnection， 访问https协议使用HttpsURLConnection
				HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
				connection.setReadTimeout(5000);
				connection.setRequestMethod("GET");
				connection.getDoInput();
				inputStream = connection.getInputStream();
				File downloadfile = null;
				
				// 只有当网络连接正常的时候才开始读取流文件
				if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					// 校验外置存储卡是否挂载
					if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
						downloadfile = new File(getFilePath(url));
						fileOutputStream = new FileOutputStream(downloadfile);
					}
					// 设置缓存大小
					byte[] b = new byte[1024];
					int len;
					if (fileOutputStream != null) {
						while ((len = inputStream.read(b)) != -1) {
							// 写入文件
							fileOutputStream.write(b, 0, len);
						}
					}
					// 返回下载文件的绝对路径
					final String absolutePath = downloadfile.getAbsolutePath();
					// final Bitmap bitmap = BitmapFactory.decodeFile(downloadfile.getAbsolutePath());
					
					handler.post(new Runnable() {
						@Override
						public void run() {
							setNetListener(netListener, absolutePath);
							System.out.println("输出结果");
						}
					});
				} else {
					System.out.println("网络设置错误·····");
				}
				

			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 获取文件的下载路径：/storage/sdcard/HttpAndParse/download/xxxx
	 * 
	 * @param url
	 *            文件的下载地址
	 * @return 返回文件的全路径
	 */
	private static String getFilePath(String url) {
		// 从下载的URL地址中截取文件名
		String fileName = url.substring(url.lastIndexOf("/") + 1);
		StringBuffer sb = new StringBuffer();
		// 获取SD卡根目录的绝对路径
		String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		sb.append(sdcardPath);
		sb.append(File.separator);
		sb.append("HttpAndParse");
		sb.append(File.separator);
		sb.append("download");

		File dirFile = new File(sb.toString());
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			// 如果文件夹不存在,或者此文件不是一个文件夹,需要创建文件夹
			dirFile.mkdirs();
		}
		return sb.toString() + File.separator + fileName;
	}

	/**
	 * 调用接口类的方法
	 * 
	 * @param netListener
	 *            NetListener接口的实例
	 * @param absolutePath
	 *            下载文件的绝对路径
	 */
	public void setNetListener(NetListener netListener, String absolutePath) {
		netListener.showView(absolutePath);
	}

	/**
	 * 设置网络数据的接口类，在MainActivity中被调用
	 * 
	 * @author ASUS-H61M
	 *
	 */
	public interface NetListener {
		public void showView(String absolutePath);
	}

}
