package com.example.databasecontentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MyContentProvider extends ContentProvider{
	//定一个一个uri路径匹配器
	private static final UriMatcher sUrimatcher = new UriMatcher(UriMatcher.NO_MATCH);
	private static final int QUERYSUCESS = 0;  
	private static final int INSERTSUCESS = 1;
	private static final int UPDATESUCESS = 2;
	private static final int DELETESUCESS = 3;
	private MyDatabaseHelper databaseHelper;
	
	static {
		sUrimatcher.addURI("com.example.databasecontentprovider.provider", "query", QUERYSUCESS);
		sUrimatcher.addURI("com.example.databasecontentprovider.provider", "insert", INSERTSUCESS);
		sUrimatcher.addURI("com.example.databasecontentprovider.provider", "update", UPDATESUCESS);
		sUrimatcher.addURI("com.example.databasecontentprovider.provider", "delete", DELETESUCESS);
	}

	@Override
	public boolean onCreate() {
		databaseHelper = new MyDatabaseHelper(getContext(), "BookStore.db");
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		int code = sUrimatcher.match(uri);
		if (code == QUERYSUCESS) {
			SQLiteDatabase readableDatabase = databaseHelper.getReadableDatabase();
			Cursor cursor = readableDatabase.query("Book", projection, selection, selectionArgs, null, null, sortOrder);
			return cursor;
		} else {
			
			return null;
		}
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int code = sUrimatcher.match(uri);
		if (code == INSERTSUCESS){
			SQLiteDatabase readableDatabase = databaseHelper.getReadableDatabase();
			long insert = readableDatabase.insert("Book", null, values);
			Uri myUri = Uri.parse("com.youCanDoIt/" + insert);
			return myUri;
		} else {
			return null;
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int code = sUrimatcher.match(uri);
		if (code == DELETESUCESS) {
			SQLiteDatabase readableDatabase = databaseHelper.getReadableDatabase();
			int delete = readableDatabase.delete("Book", selection, selectionArgs);
			return delete;
		} else {
			return 0;
		}
		
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int code = sUrimatcher.match(uri);
		if (code == UPDATESUCESS){
			SQLiteDatabase readableDatabase = databaseHelper.getReadableDatabase();
			int update = readableDatabase.update("Book", values, selection, selectionArgs);
			return update;
		} else {
			return 0;
		}
	}

}
