package com.sms.filter;

import static com.sms.filter.Consts.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
//Import the cons !! :) only the static variables and functions.
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class DBHandler {

	private DBHelper helper;

	public DBHandler(Context con) 
	{
		helper = new DBHelper(con, DataBaseName, null, DataBaseVersion);
	}

	public boolean addMessage(String from, String message) 
	{
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(TIMESTAMP, System.currentTimeMillis());
		values.put(FROM, from);
		values.put(MESSAGE, message);
		long id_s = db.insert(TABLE_NAME, null, values);
		
		//db.close();
		if (id_s == -1) {
			return false;
		}

		return true;
	}

	public Cursor getAllMessages(boolean upOrDown) {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor;
		if(upOrDown){
		cursor = db.query(TABLE_NAME,
				new String[] { ID, TIMESTAMP, FROM, MESSAGE}, null, null, null, null,
				TIMESTAMP+" ASC");
		}else{
			cursor = db.query(TABLE_NAME,
					new String[] { ID, TIMESTAMP, FROM, MESSAGE}, null, null, null, null,
					TIMESTAMP+" DESC");
		}
		return cursor;
	}

	public Cursor getAllMessageByName(String name) {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor;
		String str = name+"%";
		String [] n = {str};
		cursor = db.query(TABLE_NAME,
				new String[] { ID, TIMESTAMP, FROM, MESSAGE }, FROM+" like ?",n , null, null,
				null);
		
		return cursor;
	}
	
	public void deleteAll() {
		SQLiteDatabase db = helper.getWritableDatabase();
		try {
			db.delete(TABLE_NAME, null, null);
		} catch (SQLiteException e) {
			Log.i("tag", "EROR IN DELETING ALL");
		} finally {
			if (db.isOpen())
				db.close();
		}
	}

	public void deleteMessage(String id) {
		SQLiteDatabase db = helper.getWritableDatabase();
		try {
			String[] str = { id };
			db.delete(TABLE_NAME, ID + "=?", str);
		} catch (SQLiteException e) {
			Log.i("tag", "EROR IN DELETING");
		} finally {
			if (db.isOpen())
				db.close();
		}
	}

}
