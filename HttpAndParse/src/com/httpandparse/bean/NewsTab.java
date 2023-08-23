package com.httpandparse.bean;
/**
 * 封装json数据的bean类(封装newjson)
 * @author ASUS-H61M
 *
 */
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
