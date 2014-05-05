package com.sms.filter;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DBHelper extends SQLiteOpenHelper {

	private String TABLE_CREATE = " CREATE TABLE " + Consts.TABLE_NAME + " (" +
					Consts.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					Consts.TIMESTAMP + " LONG, " +
					Consts.FROM + " TEXT, " +
					Consts.MESSAGE + " TEXT " +
			");";
	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	
	
}
