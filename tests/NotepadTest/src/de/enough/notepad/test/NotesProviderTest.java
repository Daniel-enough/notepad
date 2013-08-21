package de.enough.notepad.test;

import de.enough.notepad.provider.NotesProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;
import android.util.Log;

public class NotesProviderTest extends ProviderTestCase2<NotesProvider> {

	private static final String TAG = NotesProvider.class.getName();

	private static MockContentResolver sResolve;

	public NotesProviderTest() {
		super(NotesProvider.class, NotesProvider.AUTHORITY);
	}

	public NotesProviderTest(Class<NotesProvider> providerClass,
			String providerAuthority) {
		super(providerClass, providerAuthority);
	}

	@Override
	public void setUp() {
		Log.i(TAG, "Entered Setup");

		try {
			super.setUp();
			sResolve = getMockContentResolver();
		} catch (Exception e) {
			String message = "SetUp failed";
			Log.w(TAG, message);
			throw new IllegalStateException(message);
		}
	}

	public void testInsert() {
		Log.i(TAG, "Insert test");

		ContentValues values = new ContentValues();
		String title = "mock title 01";
		String description = "mock description 01";
		Long timestamp = System.currentTimeMillis();
		values.put(NotesProvider.KEY_TITLE, title);
		values.put(NotesProvider.KEY_DESCRIPTION, description);
		values.put(NotesProvider.KEY_TIMESTAMP, timestamp);

		Uri newUri = sResolve.insert(NotesProvider.CONTENT_URI, values);

		Cursor cursor = sResolve.query(newUri, null, null, null, null);

		cursor.moveToFirst();

		assertEquals(1, cursor.getCount());
		assertEquals(1, cursor.getInt(cursor.getColumnIndex(NotesProvider.KEY_ID)));
		assertEquals(title, cursor.getString(cursor.getColumnIndex(NotesProvider.KEY_TITLE)));
		assertEquals(description, cursor.getString(cursor.getColumnIndex(NotesProvider.KEY_DESCRIPTION)));
		assertEquals(timestamp, (Long) cursor.getLong(cursor.getColumnIndex(NotesProvider.KEY_TIMESTAMP)));

		printNotesCursor(cursor);
	}

	public void testUpdate() {
		Log.i(TAG, "Update test");

		ContentValues values = new ContentValues();
		String title = "mock title 02";
		String description = "mock description 02";
		Long timestamp = System.currentTimeMillis();
		values.put(NotesProvider.KEY_TITLE, title);
		values.put(NotesProvider.KEY_DESCRIPTION, description);
		values.put(NotesProvider.KEY_TIMESTAMP, timestamp);
		Uri newUri = sResolve.insert(NotesProvider.CONTENT_URI, values);

		values = new ContentValues();
		String titleUpdate = "mock title 02 updated";
		String descriptionUpdate = "mock description 02";
		Long timestampUpdate = System.currentTimeMillis();	
		values.put(NotesProvider.KEY_TITLE, titleUpdate);
		values.put(NotesProvider.KEY_DESCRIPTION, descriptionUpdate);
		values.put(NotesProvider.KEY_TIMESTAMP, timestampUpdate);

		int count = sResolve.update(newUri, values, null, null);

		assertEquals(1, count);

		Cursor cursor = sResolve.query(newUri, null, null, null, null);

		cursor.moveToFirst();

		assertEquals(1, cursor.getCount());
		assertEquals(1, cursor.getInt(cursor.getColumnIndex(NotesProvider.KEY_ID)));
		assertEquals(titleUpdate, cursor.getString(cursor.getColumnIndex(NotesProvider.KEY_TITLE)));
		assertEquals(descriptionUpdate, cursor.getString(cursor.getColumnIndex(NotesProvider.KEY_DESCRIPTION)));
		assertEquals(timestampUpdate, (Long) cursor.getLong(cursor.getColumnIndex(NotesProvider.KEY_TIMESTAMP)));

		printNotesCursor(cursor);
	}

	public void testDelete() {
		Log.i(TAG, "Delete test");

		ContentValues values = new ContentValues();
		String title = "mock title 03";
		String description = "mock description 03";
		Long timestamp = System.currentTimeMillis();
		values.put(NotesProvider.KEY_TITLE, title);
		values.put(NotesProvider.KEY_DESCRIPTION, description);
		values.put(NotesProvider.KEY_TIMESTAMP, timestamp);

		Uri newUri = sResolve.insert(NotesProvider.CONTENT_URI, values);

		int count = sResolve.delete(newUri, null, null);

		assertEquals(1, count);
		
		Cursor cursor = sResolve.query(newUri, null, null, null, null);

		assertEquals(0, cursor.getCount());

		cursor = sResolve.query(NotesProvider.CONTENT_URI, null, null, null,
				null);

		assertEquals(0, cursor.getCount());
	}

	public void testQuery() {
		Log.i(TAG, "Query test");

		ContentValues values = new ContentValues();
		String title = "mock title 04";
		String description = "mock description 04";
		Long timestamp = System.currentTimeMillis();
		values.put(NotesProvider.KEY_TITLE, title);
		values.put(NotesProvider.KEY_DESCRIPTION, description);
		values.put(NotesProvider.KEY_TIMESTAMP, timestamp);

		Uri newUri = sResolve.insert(NotesProvider.CONTENT_URI, values);
		
		Cursor cursor = sResolve.query(newUri, null, null,
				null, null);
		
		cursor.moveToFirst();
		
		assertEquals(1, cursor.getCount());
		assertEquals(1, cursor.getInt(cursor.getColumnIndex(NotesProvider.KEY_ID)));
		assertEquals(title, cursor.getString(cursor.getColumnIndex(NotesProvider.KEY_TITLE)));
		assertEquals(description, cursor.getString(cursor.getColumnIndex(NotesProvider.KEY_DESCRIPTION)));
		assertEquals(timestamp, (Long) cursor.getLong(cursor.getColumnIndex(NotesProvider.KEY_TIMESTAMP)));
		
		values = new ContentValues();
		title = "mock title 05";
		description = "mock description 05";
		timestamp = System.currentTimeMillis();
		values.put(NotesProvider.KEY_TITLE, title);
		values.put(NotesProvider.KEY_DESCRIPTION, description);
		values.put(NotesProvider.KEY_TIMESTAMP, timestamp);

		newUri = sResolve.insert(NotesProvider.CONTENT_URI, values);
		
		cursor = sResolve.query(NotesProvider.CONTENT_URI, null, null,
				null, null);
		
		cursor.moveToLast();

		assertEquals(2, cursor.getCount());
		assertEquals(2, cursor.getInt(cursor.getColumnIndex(NotesProvider.KEY_ID)));
		assertEquals(title, cursor.getString(cursor.getColumnIndex(NotesProvider.KEY_TITLE)));
		assertEquals(description, cursor.getString(cursor.getColumnIndex(NotesProvider.KEY_DESCRIPTION)));
		assertEquals(timestamp, (Long) cursor.getLong(cursor.getColumnIndex(NotesProvider.KEY_TIMESTAMP)));
		
		cursor.moveToFirst();

		for (int i = 0; i < cursor.getCount(); i++) {
			printNotesCursor(cursor);
			cursor.moveToNext();
		}
	}

	private void printNotesCursor(Cursor cursor) {
		Log.i(TAG, "ID: " + cursor.getString(cursor.getColumnIndex(NotesProvider.KEY_ID)));
		Log.i(TAG, "Title: " + cursor.getString(cursor.getColumnIndex(NotesProvider.KEY_TITLE)));
		Log.i(TAG, "Description: "
						+ cursor.getString(cursor.getColumnIndex(NotesProvider.KEY_DESCRIPTION)));
		Log.i(TAG, "Timestamp: " 
						+ cursor.getString(cursor.getColumnIndex(NotesProvider.KEY_TIMESTAMP)));
	}

}
