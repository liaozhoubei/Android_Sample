package com.httpandparse.bean;

import java.util.ArrayList;
/**
 * 封装json数据的bean类(封装newjson)
 * @author Bei
 *
 */
public class NewBean {
	public ArrayList<Integer> extend;
	public int retcode;
	public ArrayList<NewsData> data;

	@Override
	public String toString() {
		return "GsonParseNewBean [extend=" + extend  + "\n" + ", retcode=" + retcode + "\n" + ", data=" + data + "]";
	}


}


