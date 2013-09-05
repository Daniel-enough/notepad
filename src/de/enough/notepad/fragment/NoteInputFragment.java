package de.enough.notepad.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import de.enough.notepad.MainActivity.OnBackKeyListener;
import de.enough.notepad.R;
import de.enough.notepad.provider.NotesProvider;

public class NoteInputFragment extends Fragment implements OnBackKeyListener {

	
	private static final String TAG = NoteInputFragment.class.getName();

	private Context mContext;
	private ContentResolver mContentResolver;
	
	private EditText mInputTitle;
	private EditText mInputDescription;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {

		View contentView = inflater.inflate(R.layout.fragment_note_input, container, false);
		setupInput(contentView);
		return contentView;
	}
	
	public void setupInput(View contentView) {
		mInputTitle = (EditText) contentView.findViewById(R.id.input_title);
		mInputDescription = (EditText) contentView.findViewById(R.id.input_description);
	}	

	@Override
	public void onAttach(Activity activity) {
		mContext = getActivity().getBaseContext();
		mContentResolver = mContext.getContentResolver();		
		super.onAttach(activity);
	} 
	
	@Override
	public void onDestroy() {	
		printNotes();
		super.onDestroy();
	}
	
	public boolean storeNote(String title, String description, long timestamp) {
		
		String selection = NotesProvider.KEY_TITLE + "=?";
		String[] selectionArgs = {title};
		
		Cursor cursor = mContentResolver.query(NotesProvider.CONTENT_URI, null, 
				selection, selectionArgs, null);
		
		if (cursor.getCount() != 0) {
			String message = "Note: " + title + " allready exists"; 
			Log.i(TAG, message);	
			Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
			return false;
		}
		
		ContentValues values = new ContentValues();	
		values.put(NotesProvider.KEY_TITLE, title);
		values.put(NotesProvider.KEY_DESCRIPTION, description);
		values.put(NotesProvider.KEY_TIMESTAMP, timestamp);
		mContentResolver.insert(NotesProvider.CONTENT_URI, values); 
		
		mInputTitle.getText().clear();
		mInputDescription.getText().clear();
		
		return true;
	}
	
	public void printNotes() {
		
		Cursor cursor = mContentResolver.query(NotesProvider.CONTENT_URI, null, null,
				null, null);
		
		cursor.moveToFirst();
		
		for (int i = 0; i < cursor.getCount(); i++) {
			Log.i(TAG , "Title: " + cursor.getString(cursor.getColumnIndex(NotesProvider.KEY_TITLE)));
			cursor.moveToNext();
		}
	}

	@Override
	public boolean onBackKeyDown() {
		String title = mInputTitle.getText().toString();
		title = title.trim();
		String description = mInputDescription.getText().toString();
		description = description.trim();
		Long timestamp = System.currentTimeMillis();
		
		if (TextUtils.isEmpty(title.trim()) && TextUtils.isEmpty(description.trim())) {
			return true;
		} 
		
		if (TextUtils.isEmpty(title.trim()) || TextUtils.isEmpty(description.trim())) {
			String message = "Fill in or clear both input fields"; 
			Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
			return false;
		}
		
		boolean noteStored = storeNote(title, description, timestamp);
		return noteStored;
	}

}
