package com.example.databasecontentprovider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * BookStore数据库的操作类
 * 
 * @author ASUS-H61M
 *
 */
public class BookDao {
	private MyDatabaseHelper helper;

	public BookDao(Context context) {
		helper = new MyDatabaseHelper(context, "BookStore.db");
	}

	/**
	 * 添加数据到Book表中
	 * @return 返回新插入的行号，如果插入失败返回-1
	 */
	public long addData() {
		SQLiteDatabase database = helper.getReadableDatabase();
		// 使用anddroid封装的SQL语法
		ContentValues values = new ContentValues();
		values.put("bookname", "The Da Vinci Code");
		values.put("author", "Dan Brown");
		values.put("pages", 454);
		values.put("price", 16.96);
		long insert = database.insert("Book", null, values);
		values.clear();
		values.put("bookname", "The Lost Symbol");
		values.put("author", "Dan Brown");
		values.put("pages", 510);
		values.put("price", 19.95);
		long insert1 = database.insert("Book", null, values);

		// 使用原生SQL语法
		database.execSQL(
				"insert into Book(bookname, author, pages, price) values('The Da Vinci Code', 'Dan Brown', 454, 16.96)");
		database.execSQL(
				"insert into Book(bookname, author, pages, price) values('The Lost Symbol', 'Dan Brown', 510, 19.95)");
		database.execSQL(
				"insert into Book(bookname, author, pages, price) values('piao liu chuan shuo', 'Baby lin', 189, 12.99)");
		database.execSQL(
				"insert into Book(bookname, author, pages, price) values('lv bing xun chuan qi', 'Baby lin', 470, 10.99)");
		database.execSQL(
				"insert into Book(bookname, author, pages, price) values('bing yu huo zhi ge', 'Dan Brown', 624, 10.99)");
		database.execSQL(
				"insert into Book(bookname, author, pages, price) values('bing yu huo zhi ge', 'Dan Brown', 624, 10.99)");
		database.execSQL(
				"insert into Book(bookname, author, pages, price) values('wo yao du shu', 'Dan Brown', 510, 10.99)");
		// database.execSQL("insert into Book(bookname, author, pages, price) values('The Lost Symbol', 'Dan Brown', 510, 19.95)");

		// 使用原生与android封装方法，在values(?,?,?,?)有4个问号，代表的是占位符，分别对应后面的String数组中的四个值。
		// database.execSQL("insert into Book(bookname, author, pages, price) values(?, ?, ?, ?)",
		// new String[]{"The Da Vinci Code", "Dan Brown", "454", "16.96"});
		//
		// database.execSQL("insert into Book(bookname, author, pages, price) values(?, ?, ?, ?)",
		//			new String[] { "The Lost Symbol", "Dan Brown", "510", "19.95" });
		System.out.println("新增加后的全部数据·····················");
		queryAll(database);
		return insert;
	}

	/**
	 * 更新Book表中的数据
	 * @return 返回受影响的行
	 */
	public int updateData() {
		SQLiteDatabase database = helper.getReadableDatabase();
		// 使用anddroid封装的SQL语法
		ContentValues values = new ContentValues();
		values.put("price", 10.99);
		int update = database.update("Book", values, "bookname = ?", new String[] { "The Da Vinci Code" });
		// 使用原生SQL语法
		// database.execSQL("update Book SET price=10.99 where bookname='The Da
		// Vinci Code' ");
		// 使用原生与android封装方法
		// database.execSQL("update Book SET price=? where bookname=? ", new String[] { "10.99", "The Da Vinci Code" });
		System.out.println("更新后的数据································");
		queryAll(database);
		return update;
	}

	/**
	 * 删除Book中的数据
	 * @return 返回受影响的行
	 */
	public int delete() {
		SQLiteDatabase database = helper.getReadableDatabase();
		// 使用anddroid封装的SQL语法
		int delete = database.delete("Book", "pages > ?", new String[] { "500" });
		// 使用原生SQL语法
//		 database.execSQL("delete from Book where pages > 500");
//		 使用原生与android封装方法
//		database.execSQL("delete from Book where pages > ?", new String[] { "500" });
		System.out.println("删除后的数据································");
		queryAll(database);
		return delete;

	}

	/**
	 * 查询Book表中的数据
	 */
	public void query() {
		SQLiteDatabase database = helper.getReadableDatabase();

		// 使用原生SQL语法
		Cursor cursor = database.rawQuery(
				"select * from Book where price=10.99 group by bookname having author='Baby lin' order by author", null);
		
		// 使用anddroid封装的SQL语法
		//Cursor cursor = database.query("Book", null, "price=?", new String[]{"10.99"}, "bookname", "author='Baby lin'", "author");
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				String bookname = cursor.getString(cursor.getColumnIndex("bookname"));
				String author = cursor.getString(cursor.getColumnIndex("author"));
				int pages = cursor.getInt(cursor.getColumnIndex("pages"));
				double price = cursor.getDouble(cursor.getColumnIndex("price"));
				System.out.println("书名：" + bookname + "  作者：" + author + "  页数" + pages + "   价格" + price);

			}
			cursor.close();
		}

	}
	/**
	 * 查询所有数据
	 * @param database
	 */
	public void queryAll(SQLiteDatabase database){
		Cursor cursor = database.rawQuery("select * from Book", null);
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				String bookname = cursor.getString(cursor.getColumnIndex("bookname"));
				String author = cursor.getString(cursor.getColumnIndex("author"));
				int pages = cursor.getInt(cursor.getColumnIndex("pages"));
				double price = cursor.getDouble(cursor.getColumnIndex("price"));
				System.out.println("书名：" + bookname + "  作者：" + author + "  页数" + pages + "   价格" + price);

			}
			cursor.close();
		}
	}

}
