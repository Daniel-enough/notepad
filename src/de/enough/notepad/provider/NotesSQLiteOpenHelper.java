package de.enough.notepad.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NotesSQLiteOpenHelper extends SQLiteOpenHelper {
	
	public static final String DATABASE_NAME = "notesDatabase.db";
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_TABLE = "notes";
	
	private static final String SQL_CREATE_DATABASE;
	static {
		StringBuilder createDatabaseBuilder = new StringBuilder();
		createDatabaseBuilder.append("create table ").append(DATABASE_TABLE);
		createDatabaseBuilder.append(" (");
		createDatabaseBuilder.append(NotesProvider.KEY_ID).append(' ').append("integer primary key autoincrement,").append(' ');
		createDatabaseBuilder.append(NotesProvider.KEY_TITLE).append(' ').append("TEXT,").append(' ');
		createDatabaseBuilder.append(NotesProvider.KEY_DESCRIPTION).append(' ').append("TEXT,").append(' ');
		createDatabaseBuilder.append(NotesProvider.KEY_TIMESTAMP).append(' ').append("INTEGER");
		createDatabaseBuilder.append(')');
		SQL_CREATE_DATABASE = createDatabaseBuilder.toString();
	}
	
	public NotesSQLiteOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_DATABASE);
		Log.i("create Database", SQL_CREATE_DATABASE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i("notes database", "Upgrading from version " + oldVersion + " to " + newVersion);
		
		db.execSQL("drop table if it exists " + DATABASE_TABLE + ";");
		onCreate(db);
	}

}
