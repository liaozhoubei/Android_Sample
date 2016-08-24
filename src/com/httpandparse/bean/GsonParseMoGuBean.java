package com.httpandparse.bean;

import java.util.ArrayList;
/**
 * 封装gsonparse中的数据的bean类(封装mogujson)
 * @author Bei
 *
 */
public class GsonParseMoGuBean {
	public String author;
	public String data;
	public String des;
	public String downloadNum;
	public String downloadUrl;
	public String iconUrl;
	public int id;
	public String name;
	public String packageName;
	public int size;
	public float stars;
	public String version;
	
	public ArrayList<SafeInfo> safe;
	
	public class SafeInfo {
		public String safeDes;
		public int safeDesColor;
		public String safeDesUrl;
		public String safeUrl;
		@Override
		public String toString() {
			return "SafeInfo [safeDes=" + safeDes + "\n" + ", safeDesColor=" + safeDesColor + "\n" + ", safeDesUrl=" + safeDesUrl
					+ "\n" + ", safeUrl=" + safeUrl + "]";
		}
		
		
	}
	
	public ArrayList<String> screen;

	
	
}
