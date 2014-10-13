package com.chat14.database;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBProvider {
	private DBHelper dbHelper;
	private SQLiteDatabase db;

	public static String DB_NAME;
	public static final int DB_VERSION = 1;

	Context dbContext;

	public DBProvider(Context context) {
		this.dbContext = context;
	}

	public void open(String DB_NAME) {
		DBProvider.DB_NAME = DB_NAME;
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

	public Cursor getMessages(int id) {
		String selection = "posid = ?";
		String[] selectionArgs = new String[] { Integer.toString(id) };

		return db.query(DBHelper.DB_TABLE_MESSAGES, null, selection,
				selectionArgs, null, null, null);
	}

	public long getLastMessage() {
		Long lastTime = (long) 0;
		Cursor cursor = db.rawQuery("SELECT MAX( " + DBHelper.COLUMN_TIME
				+ " ) as " + DBHelper.COLUMN_TIME + " FROM "
				+ DBHelper.DB_TABLE_MESSAGES, null);
		Log.d("DB", cursor.toString());
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				if (!cursor.isNull(cursor.getColumnIndex(DBHelper.COLUMN_TIME))) {
					lastTime = cursor.getLong(cursor
							.getColumnIndex(DBHelper.COLUMN_TIME));
				}
			}
		}

		return lastTime;
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
