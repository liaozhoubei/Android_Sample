package com.httpandparse.bean;

import java.util.ArrayList;
/**
 * 封装gsonparse中的数据的bean类(封装newjson)
 * @author Bei
 *
 */
public class GsonParseNewBean {
	public ArrayList<Integer> extend;
	public int retcode;
	
	public ArrayList<NewsData> data;
	
	public class NewsData{
		public ArrayList<NewsTab> children;
		public int id;
		public String title;
		public int type;
		public String url;
		@Override
		public String toString() {
			return "NewsData [children=" + children + ", id=" + id + ", title=" + title + ", type=" + type + ", url="
					+ url + "]";
		}
		
	}
	
	public class NewsTab {
		public int id;
		public String title;
		public int type;
		public String url;
		@Override
		public String toString() {
			return "NewsTab [id=" + id + "\n" + ", title=" + title + "\n" + ", type=" + type + "\n" + ", url=" + url + "]";
		}
		
	}

	@Override
	public String toString() {
		return "GsonParseNewBean [extend=" + extend  + "\n" + ", retcode=" + retcode + "\n" + ", data=" + data + "]";
	}
	

}
