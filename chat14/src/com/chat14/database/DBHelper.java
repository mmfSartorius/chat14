package com.chat14.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	public static final String DB_TABLE_PERSONS = "persons";
	public static final String DB_TABLE_MESSAGES = "messages";

	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";

	public static final String COLUMN_POSID = "posid";
	public static final String COLUMN_MESSAGE = "message";
	public static final String COLUMN_TIME = "time";

	public DBHelper(Context context, String name, int version) {
		super(context, name, null, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

		final String execStringPersons = "create table " + DB_TABLE_PERSONS
				+ " (" + COLUMN_ID + " integer primary key autoincrement,"
				+ COLUMN_NAME + " text," + ");";
		db.execSQL(execStringPersons);
		final String execStringMessages = "create table " + DB_TABLE_MESSAGES
				+ " (" + COLUMN_ID + " integer primary key autoincrement,"
				+ COLUMN_POSID + " integer," + COLUMN_MESSAGE + " text"
				+ COLUMN_TIME + " long" + ");";
		db.execSQL(execStringMessages);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
