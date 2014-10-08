package com.chat14.database;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBProvider {
	private DBHelper dbHelper;
	private SQLiteDatabase db;

	public static String DB_NAME;
	public static final int DB_VERSION = 1;

	Context dbContext;

	public DBProvider(Context context, String login) {
		this.dbContext = context;
		DB_NAME = login;
	}

	public void open() {
		dbHelper = new DBHelper(dbContext, DB_NAME, DB_VERSION);
		db = dbHelper.getWritableDatabase();
	}

	public void close() {
		if (dbHelper != null) {
			dbHelper.close();
		}
	}

	public Cursor getAllPersons() {
		return db.query(DBHelper.DB_TABLE_PERSONS, null, null, null, null,
				null, null);
	}

	public Cursor getMesages(int id) {
		String selection = "posid = ?";
		String[] selectionArgs = new String[] { Integer.toString(id) };

		return db.query(DBHelper.DB_TABLE_MESSAGES, null, selection,
				selectionArgs, null, null, null);
	}

	public long addPerson(String name) {
		ContentValues cv = new ContentValues();

		cv.put(DBHelper.COLUMN_NAME, name);
		return db.insert(DBHelper.DB_TABLE_PERSONS, null, cv);
	}

	public void addMessage(String message, long posid) {
		ContentValues cv = new ContentValues();
		Date date = new Date();
		
		cv.put(DBHelper.COLUMN_MESSAGE, message);
		cv.put(DBHelper.COLUMN_TIME, date.getTime());
		cv.put(DBHelper.COLUMN_POSID, posid);
		db.insert(DBHelper.DB_TABLE_MESSAGES, null, cv);
	}
}
