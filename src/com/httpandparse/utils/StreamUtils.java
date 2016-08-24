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
		try {
			BufferedInputStream bis = new BufferedInputStream(in);
			int len;
			StringBuffer sb = new StringBuffer();
			while ((len = bis.read()) != -1) {
				sb.append((char) len);
			}
			return sb.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 使用读取字节数据方法解析inputStream
	 */
	public static String getString(InputStream in) {
		try {
			StringBuffer sb = new StringBuffer();
			byte[] b = new byte[1024];
			int len = 0;
			while ((len = in.read(b)) != -1) {
				sb.append(new String(b, 0, len));
			}
			System.out.println(sb.toString());
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获得输入流，返回字符串 
	 * @param in 传入inputStream
	 * @param decode  传入流解码方式，如"GBK" 或者 "UTF-8"
	 * @return 返回String字符串
	 */
	public static String getDecodeString(InputStream in, String decode) {
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(in, decode);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String str;
			StringBuffer sb = new StringBuffer();
			while ((str = bufferedReader.readLine()) != null) {
				sb.append(str);
			}
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
