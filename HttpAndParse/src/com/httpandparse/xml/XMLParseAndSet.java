package com.httpandparse.xml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import com.httpandparse.bean.SmsBean;
import com.httpandparse.bean.SmsDao;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Xml;

/**
 * 生成XML和解析XML
 * 
 * @author ASUS-H61M
 *
 */
public class XMLParseAndSet {

	// 使用XmlSerializer来序列化生成xml文件
	public static ArrayList<SmsBean> XMLparse(Context context) {

		try {
			// 0.获取短信数据
			ArrayList<SmsBean> allSms = SmsDao.getAllSms();
			// 1.通过Xml获取一个XmlSerializer对象
			XmlSerializer xs = Xml.newSerializer();
			// 2.设置XmlSerializer的一些参数，比如：设置xml写入到哪个文件中
			// os:xml文件写入流 encoding：流的编码
			xs.setOutput(context.openFileOutput("backupsms2.xml", Context.MODE_PRIVATE), "UTF-8");
			// 3.序列化一个xml的声明头
			// encoding:xml文件的编码 standalone:是否独立
			xs.startDocument("utf-8", true);
			// 4.序列化一个根节点的开始节点
			// namespace:命名空间 name： 标签的名称
			ArrayList<SmsBean> allSms1 = SmsDao.getAllSms();
			xs.startTag(null, "smss");
			for (SmsBean smsBean : allSms) {
				xs.startTag(null, "Sms");
				xs.attribute(null, "id", smsBean.id + "");

				xs.startTag(null, "num");
				xs.text(smsBean.num);
				System.out.println(smsBean.num);
				xs.endTag(null, "num");

				xs.startTag(null, "msg");
				xs.text(smsBean.msg);
				System.out.println(smsBean.msg);
				xs.endTag(null, "msg");

				xs.startTag(null, "data");
				xs.text(smsBean.date);
				System.out.println(smsBean.date);
				xs.endTag(null, "data");

				xs.endTag(null, "Sms");
				smsBean.toString();
				allSms1.add(smsBean);
			}
			xs.endTag(null, "smss");
			xs.endDocument();
			return allSms1;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;

	}

	// 使用StringBuffer直接生成xml格式数据
	public static String getSms() {
		// 获取短信内容，list
		ArrayList<SmsBean> allSms = SmsDao.getAllSms();

		// 将数据以xml格式封装到一个StringBuffer中
		StringBuffer sb = new StringBuffer();
		// 封装一个声明头
		sb.append("<?xml version='1.0' encoding='utf-8' standalone='yes' ?>");
		// 封装根节点
		sb.append("<Smss>");
		// 循环遍历list集合封装所有的短信
		for (SmsBean smsBean : allSms) {

			sb.append("<Sms id = \"" + smsBean.id + "\">");

			sb.append("<num>");
			sb.append(smsBean.num);
			sb.append("</num>");

			sb.append("<msg>");
			sb.append(smsBean.msg);
			sb.append("</msg>");

			sb.append("<date>");
			sb.append(smsBean.date);
			sb.append("</date>");

			sb.append("</Sms>");

		}
		sb.append("</Smss>");
		String xml = sb.toString();
		return xml;

	}

	// 解析xml文件读取短信内容
	public static ArrayList<SmsBean> parseXMLWithPull(Context context) {
		ArrayList<SmsBean> arrayList = null;
		SmsBean smsBean = null;
		try {
			//1.通过Xml获取一个XmlPullParser对象
			XmlPullParser xpp = Xml.newPullParser();
			//2.设置XmlPullParser对象的参数，需要解析的是哪个xml文件,设置一个文件读取流
		
			//通过context获取一个资产管理者对象
			AssetManager assets = context.getAssets();
			//通过资产管理者对象能获取一个文件读取流
			InputStream inputStream = assets.open("backupsms.xml");
			xpp.setInput(inputStream,"utf-8");
//			xpp.setInput(context.openFileInput("backupsms2.xml"), "utf-8");
			//3.获取当前xml行的事件类型
			int type = xpp.getEventType();
			//4.判断事件类型是否是文档结束的事件类型
			while(type != XmlPullParser.END_DOCUMENT){
				//5.如果不是，循环遍历解析每一行的数据。解析一行后，获取下一行的事件类型

				String currentTagName = xpp.getName();
				//判断当前行的事件类型是开始标签还是结束标签
				switch (type) {
				case XmlPullParser.START_TAG:
					if(currentTagName.equals("Smss")){
						//如果当前标签是Smss，需要初始化一个集合
						arrayList = new ArrayList<SmsBean>();
					}else if(currentTagName.equals("Sms")){

						smsBean = new SmsBean();
						smsBean.id = Integer.valueOf(xpp.getAttributeValue(null, "id"));

					}else if(currentTagName.equals("num")){
						smsBean.num =  xpp.nextText();
					}else if(currentTagName.equals("msg")){
						smsBean.msg =  xpp.nextText();
					}else if(currentTagName.equals("date")){
						smsBean.date =  xpp.nextText();
					}
					break;
				case XmlPullParser.END_TAG:
					//当前结束标签是Sms的话，一条短信数据封装完成， 可以加入list中
					if(currentTagName.equals("Sms")){
						arrayList.add(smsBean);
					}
					break;
				default:
					break;
				}

				type = xpp.next();//获取下一行的事件类型
			}

			return arrayList;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解析books.xml中的数据
	 * 
	 * @param inputStream
	 * @return 
	 */
	public static ArrayList<Book> parseXMLWithDOM(InputStream inputStream) {
		ArrayList<Book> booklists = new ArrayList<Book>();
		// 创建一个DocumentBuilderFactory的对象
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		// 创建一个DocumentBuilder的对象
		try {
			// 创建DocumentBuilder对象
			DocumentBuilder db = dbf.newDocumentBuilder();
			// 通过DocumentBuilder对象的parser方法加载books.xml文件到当前项目下
			Document document = db.parse(inputStream);
			// 获取所有book节点的集合
			NodeList bookList = document.getElementsByTagName("book");
			// 通过nodelist的getLength()方法可以获取bookList的长度
			System.out.println("一共有" + bookList.getLength() + "本书");
			// 遍历每一个book节点
			for (int i = 0; i < bookList.getLength(); i++) {
				
				Book books = new Book();
				
				System.out.println("=================下面开始遍历第" + (i + 1) + "本书的内容=================");
				// 通过 item(i)方法 获取一个book节点，nodelist的索引值从0开始
				Node book = bookList.item(i);
				// 获取book节点的所有属性集合
				NamedNodeMap attrs = book.getAttributes();
				System.out.println("第 " + (i + 1) + "本书共有" + attrs.getLength() + "个属性");
				// 遍历book的属性id
				for (int j = 0; j < attrs.getLength(); j++) {
					// 通过item(index)方法获取book节点的某一个属性
					Node attr = attrs.item(j);
					// 获取属性名
					System.out.print("属性名：" + attr.getNodeName());
					// 获取属性值
					System.out.println("--属性值" + attr.getNodeValue());
					books.setId(attr.getNodeValue());
				}
				// //前提：已经知道book节点有且只能有1个id属性
				// //将book节点进行强制类型转换，转换成Element类型
				// Element book = (Element) bookList.item(i);
				// //通过getAttribute("id")方法获取属性值
				// String attrValue = book.getAttribute("id");
				// System.out.println("id属性的属性值为" + attrValue);
				// 解析book节点的子节点
				NodeList childNodes = book.getChildNodes();
				// 遍历childNodes获取每个节点的节点名和节点值
				System.out.println("第" + (i + 1) + "本书共有" + childNodes.getLength() + "个子节点");
				for (int k = 0; k < childNodes.getLength(); k++) {
					// 区分出text类型的node以及element类型的node
					if (childNodes.item(k).getNodeType() == Node.ELEMENT_NODE) {
						
						// 获取了element类型节点的节点名
						String nodeName = childNodes.item(k).getNodeName();
						System.out.print("第" + (k + 1) + "个节点的节点名：" + nodeName);
						// 获取了element类型节点的节点值
						String nodeValue = childNodes.item(k).getFirstChild().getNodeValue();
						System.out.println("--节点值是：" + nodeValue);
						// System.out.println("--节点值是：" +
						// childNodes.item(k).getTextContent());
						
						if (nodeName.equals("name")) {
							books.setName(nodeValue);
						}
						else if (nodeName.equals("author")) {
							books.setAuthor(nodeValue);
						}
						else if (nodeName.equals("year")) {
							books.setYear(nodeValue);
						}
						else if (nodeName.equals("price")) {
							books.setPrice(nodeValue);
						}
						else if (nodeName.equals("language")) {
							books.setLanguage(nodeValue);
						}
						
					}
				}
				booklists.add(books);
				System.out.println("======================结束遍历第" + (i + 1) + "本书的内容=================");
			}
			return booklists;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<Book> parseXMLWithSAX(InputStream inputStream) {
		//获取一个SAXParserFactory实例
		SAXParserFactory factory = SAXParserFactory.newInstance();
		// 通过factory获取ȡSAXParser实例
		try {
			SAXParser parser = factory.newSAXParser();
			//创建SAXParserHandler对象
			SAXParserHandler handler = new SAXParserHandler();
			parser.parse(inputStream, handler);
			System.out.println("一共有" + handler.getBookList().size()
					+ "本书");
			
			for (Book book : handler.getBookList()) {
				System.out.println(book.getId());
				System.out.println(book.getName());
				System.out.println(book.getAuthor());
				System.out.println(book.getYear());
				System.out.println(book.getPrice());
				if (book.getLanguage() != null) {
					System.out.println(book.getLanguage());
				}
				System.out.println("----finish----");
			}
			return handler.getBookList();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
