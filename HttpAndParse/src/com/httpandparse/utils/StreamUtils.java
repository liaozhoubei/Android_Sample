package com.httpandparse.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 转换流的工具类
 * 有三种不同方式的转换方法
 * @author ASUS-H61M
 *
 */
public class StreamUtils {

	/**
	 * 获得输入流
	 * @param in 传入inputStream
	 * @return 返回从输入流中获得的String
	 */
	public static String getStringData(InputStream in) {
		StringBuffer sb = new StringBuffer();
		BufferedInputStream bis = null;
		try {
			 bis =new BufferedInputStream(in);
			int len;
			while ((len = bis.read()) != -1) {
				sb.append((char) len);
			}
			return sb.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			IOUtils.close(bis);
		}

		return sb.toString();
	}

	/**
	 * 使用读取字节数据方法解析inputStream
	 */
	public static String getString(InputStream in) {
		StringBuffer sb = new StringBuffer();
		try {
			byte[] b = new byte[1024];
			int len = 0;
			while ((len = in.read(b)) != -1) {
				sb.append(new String(b, 0, len));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * 获得输入流，返回字符串 
	 * @param in 传入inputStream
	 * @param decode  传入流解码方式，如"GBK" 或者 "UTF-8"
	 * @return 返回String字符串
	 */
	public static String getDecodeString(InputStream in, String decode) {
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		StringBuffer sb = new StringBuffer();
		try {
			inputStreamReader = new InputStreamReader(in, decode);
			bufferedReader = new BufferedReader(inputStreamReader);
			String str;
			while ((str = bufferedReader.readLine()) != null) {
				sb.append(str);
			}
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.close(inputStreamReader);
			IOUtils.close(bufferedReader);
		}
		return sb.toString();
	}

}
