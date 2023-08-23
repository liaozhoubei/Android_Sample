package com.httpandparse.thread;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
/**
 * 像服务端上传文件
 * Erro: 服务器端存在问题，暂时无法解决
 * @author ASUS-H61M
 *
 */
public class UploadThread extends Thread {

	private String fileName;
	private String url;
	
	

	public UploadThread(String fileName, String url) {
		super();
		this.fileName = fileName;
		this.url = url;
	}

	@Override
	public void run() {
		String boundary = "---------------------------7e047c180a54";
		String prefix = "--";
		String end = "\r\n";
		try {
			URL httpUrl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			out.writeBytes(prefix + boundary + end);
			out.writeBytes("Content-Disposition: form-data;" + "name=\"file\"; filename=\"" + "Sky.jpg" + "\"" + end);
			out.writeBytes(end);
			FileInputStream fileInputStream = new FileInputStream(new File(fileName));
			byte[] b = new byte[1024*4];
			int len;
			while((len = fileInputStream.read(b)) != -1){
				out.write(b, 0, len);
			} 
			out.writeBytes(end);
			out.writeBytes(prefix + boundary + prefix + end);
			out.flush();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String str;
			while ((str = bufferedReader.readLine()) != null) {
				sb.append(str);
			}
			System.out.println("respose" + sb.toString());
			if (out != null) {
				out.close();
			}
			if (bufferedReader != null) {
				bufferedReader.close();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
