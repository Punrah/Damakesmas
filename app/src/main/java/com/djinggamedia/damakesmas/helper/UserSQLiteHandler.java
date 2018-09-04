/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 * */
package com.djinggamedia.damakesmas.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.djinggamedia.damakesmas.persistence.User;


public class UserSQLiteHandler extends SQLiteOpenHelper {

	private static final String TAG = UserSQLiteHandler.class.getSimpleName();

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 2;

	// Database Name
	private static final String DATABASE_NAME = "AbsenJingga";

	// Login table name
	private static final String TABLE_USER = "user";

	// Login Table Columns names
	private static final String KEY_TIKET = "tiket";
	private static final String KEY_NAME = "name";
	private static final String KEY_JABATAN = "jabatan";
	private static final String KEY_PHOTO = "photo";
	private static final String KEY_ALAMAT = "alamat";
	private static final String KEY_JK = "unit";
	private static final String KEY_NIP = "nip";
    private UserSQLiteHandler db;
    private SessionManager session;

	public UserSQLiteHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
				+ KEY_TIKET + " TEXT UNIQUE," + KEY_NAME + " TEXT,"
				+ KEY_JABATAN + " TEXT," + KEY_PHOTO + " TEXT,"+ KEY_JK + " TEXT,"+KEY_NIP + " TEXT,"+ KEY_ALAMAT + " TEXT" + ")";
		db.execSQL(CREATE_LOGIN_TABLE);

		Log.d(TAG, "Database tables created");
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
		// Create tables again
		onCreate(db);
	}

	/**
	 * Storing user details in database
	 * */
	public void addUser(User user) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TIKET, user.getTiket());
		values.put(KEY_NAME, user.name); // Name
		values.put(KEY_JABATAN, user.jabatan); // Email
		values.put(KEY_PHOTO, user.photo);
		values.put(KEY_ALAMAT,user.alamat);
		values.put(KEY_JK,user.jk);
		values.put(KEY_NIP,user.nip);

		// Inserting Row
		long id = db.insert(TABLE_USER, null, values);
		db.close(); // Closing database connection

		Log.d(TAG, "New user inserted into sqlite: " + id);
	}

	public void updateUser(User user) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TIKET, user.getTiket());
		values.put(KEY_NAME, user.name); // Name
		values.put(KEY_JABATAN, user.jabatan); // Email
		values.put(KEY_PHOTO, user.photo);
		values.put(KEY_ALAMAT,user.alamat);// Phone
		values.put(KEY_JK,user.jk);
		values.put(KEY_NIP,user.nip);

		// Inserting Row
		long id = db.update(TABLE_USER, values, KEY_TIKET +"="+ user.getTiket(),null);
		db.close(); // Closing database connection

		Log.d(TAG, "New user inserted into sqlite: " + id);
	}

	/**
	 * Getting user data from database
	 * */
	public User getUserDetails() {
		User user = new User();
		String selectQuery = "SELECT  * FROM " + TABLE_USER;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			user.setTiket(cursor.getString(cursor.getColumnIndex(KEY_TIKET)));
			user.name=cursor.getString(cursor.getColumnIndex(KEY_NAME));
			user.jabatan =cursor.getString(cursor.getColumnIndex(KEY_JABATAN));
			user.photo=cursor.getString(cursor.getColumnIndex(KEY_PHOTO));
			user.alamat =cursor.getString(cursor.getColumnIndex(KEY_ALAMAT));
			user.jk =cursor.getString(cursor.getColumnIndex(KEY_JK));
			user.nip =cursor.getString(cursor.getColumnIndex(KEY_NIP));
		}
		cursor.close();
		db.close();
		// return user
		Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

		return user;
	}

	/**
	 * Re crate database Delete all tables and create them again
	 * */
	public void deleteUsers() {
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(TABLE_USER, null, null);
		db.close();

		Log.d(TAG, "Deleted all user info from sqlite");
	}

}
