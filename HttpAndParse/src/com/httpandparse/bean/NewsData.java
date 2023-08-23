package com.httpandparse.bean;

import java.util.ArrayList;

/**
 * 封装json数据的bean类(封装newjson)
 * @author ASUS-H61M
 *
 */
public class NewsData {
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
