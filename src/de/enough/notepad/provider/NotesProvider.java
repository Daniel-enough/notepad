package de.enough.notepad.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class NotesProvider extends ContentProvider {
	
	public static final String AUTHORITY = "de.enough.notepad.provider";
	public static final String CONTENT_PATH = "notes";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + CONTENT_PATH);
	
	public static final String KEY_ID = "_id";
	public static final String KEY_TITLE = "title";
	public static final String KEY_DESCRIPTION = "description";
	public static final String KEY_TIMESTAMP = "timestamp";
	
	private static final int ALL_ROWS = 1;
	private static final int SINGLE_ROW = 2;
	
	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, CONTENT_PATH, ALL_ROWS);
		uriMatcher.addURI(AUTHORITY, CONTENT_PATH + "/#", SINGLE_ROW);
	}

	private NotesSQLiteOpenHelper mNotesSQLiteOpenHelper;
	
	
	@Override
	public boolean onCreate() {
		
		mNotesSQLiteOpenHelper = new NotesSQLiteOpenHelper(
	            getContext(),        								
	            NotesSQLiteOpenHelper.DATABASE_NAME,              
	            null,                								// uses the default SQLite cursor
	            NotesSQLiteOpenHelper.DATABASE_VERSION          
	    );
		return true;
	}
	
	@Override
	public String getType(Uri uri) {
		int match = uriMatcher.match(uri);
		switch (match) {
			case ALL_ROWS:
				return ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + AUTHORITY + "." + CONTENT_PATH;
			case SINGLE_ROW:
				return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + AUTHORITY + "." + CONTENT_PATH;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {		
		SQLiteDatabase db = mNotesSQLiteOpenHelper.getReadableDatabase();
			
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(NotesSQLiteOpenHelper.DATABASE_TABLE);
		
		int uriType = uriMatcher.match(uri);
		
		if (uriType == SINGLE_ROW) {
			String id = uri.getLastPathSegment();
			queryBuilder.appendWhere(KEY_ID + "=" + id);
		}
	
		String groupBy = null;
		String having = null;
		Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, groupBy, having, sortOrder);
		
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		
		return cursor;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = mNotesSQLiteOpenHelper.getReadableDatabase();
		
		long newId = db.insert(NotesSQLiteOpenHelper.DATABASE_TABLE, null, values);
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		if (newId > -1) {
			Uri insertedUri = ContentUris.withAppendedId(CONTENT_URI, newId);
			getContext().getContentResolver().notifyChange(insertedUri, null);
			return insertedUri;
		} else {
			return null;
		}
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mNotesSQLiteOpenHelper.getWritableDatabase();
		
		int uriType = uriMatcher.match(uri);
		
		if (uriType == SINGLE_ROW) {
			String id = uri.getLastPathSegment();
			
			if (TextUtils.isEmpty(selection)) {
				selection = KEY_ID + "=" + id;
			} else {
				selection = KEY_ID + "=" + id + " and " + selection;
			}
		}
		
		int deletedRows = db.delete(NotesSQLiteOpenHelper.DATABASE_TABLE, selection, selectionArgs);
		if (deletedRows > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
			Log.d("notes", "deleted " + deletedRows + " rows using selection " + selection);
		}
		
		return deletedRows;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = mNotesSQLiteOpenHelper.getWritableDatabase();
		
		int uriType = uriMatcher.match(uri);
		
		if (uriType == SINGLE_ROW) {
			String id = uri.getLastPathSegment();

			if (TextUtils.isEmpty(selection)) {
				selection = KEY_ID + "=" + id;
			} else {
				selection = KEY_ID + "=" + id + " and " + selection;
			}
		}
		
		int updateRows = db.update(NotesSQLiteOpenHelper.DATABASE_TABLE, values, selection, selectionArgs);
		
		getContext().getContentResolver().notifyChange(uri, null);
		Log.d("notes", "updated " + updateRows + " rows using selection " + selection);
		
		return updateRows;
	}

}
