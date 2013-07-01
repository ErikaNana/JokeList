/**
 * JokeTable class handles all database (CRUD) operations
		 * _ID			joke_text		  rating	author
		------------------------------------------------
		1		A really lame joke		0		jsmith
		2		A slightly lame joke	2		jsmith
		3		A hilarious pun			1		jsmith
		4		A sightly funny joke	0		jsmith
		5		A joke you make often	1		jsmith
		6		A joke you hate			2		jsmith
 */

package edu.calpoly.android.lab4;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class JokeTable{
	/** All static variables */
	//Joke table name
	public static final String JOKE_TABLE = "jokes";
	
	//Joke Table Columns names
	public static final String KEY_ID = "_id";
	public static final int JOKE_COL_ID = 0;
	
	public static final String JOKE_TEXT = "joke_text";
	public static final int JOKE_COL_TEXT = JOKE_COL_ID + 1;
	
	public static final String JOKE_RATING = "rating";
	public static final int JOKE_COL_RATING = JOKE_COL_ID + 2;
	
	public static final String JOKE_AUTHOR = "author";
	public static final int JOKE_COL_AUTHOR = JOKE_COL_ID + 3;
	
	//Database creation sql statement
	public static final  String DATABASE_CREATE = "create table " + JOKE_TABLE + "(" + 
			   										KEY_ID + " integer primary key autoincrement, " + 
			   										JOKE_TEXT + " text not null, " +
			   										JOKE_RATING + " integer not null, " +
			   										JOKE_AUTHOR + " text not null);";
												 
	/** SQLite database table removal statement. Only used if upgrading database. */
	public static final String DATABASE_DROP = "drop table if exists " + JOKE_TABLE;
	
	/**
	 * Where need to write create table statements
	 * This is called when database is created
	 */
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}
	
	/**
	 * This method is called when database is upgraded like modifying the table structure,
	 * adding constraints to database, etc
	 */
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//send a warning that the database is being upgraded
		Log.w(JokeTable.class.getName(), "database is being upgraded form old version to new version");
		//Drop older table if existed
		db.execSQL(DATABASE_DROP);
		onCreate(db);
	}


	
}
