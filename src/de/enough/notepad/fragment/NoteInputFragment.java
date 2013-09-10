package de.enough.notepad.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
	private static final String EXTRA_NOTE_ID = "id";

	private Context mContext;
	private ContentResolver mContentResolver;
	
	private EditText mInputTitle;
	private EditText mInputDescription;
	
	private String mTitleInDatabase;
	private String mDescriptionInDatabase;

	private boolean isUpdate;
	private boolean mTitleChanged;
	private boolean mDescriptionChanged;
	

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
	
	@Override
	public void onStart() {
		
		Bundle data = getArguments();
		
		isUpdate = data != null;
		
		if (isUpdate) {
			setStringsFromDatabase(data);
			setEditText(data);
		}
		
		mTitleChanged = false;
		mDescriptionChanged = false;
		
		super.onStart();
	}

	@Override
	public void onAttach(Activity activity) {
		mContext = getActivity().getBaseContext();
		mContentResolver = mContext.getContentResolver();		
		super.onAttach(activity);
	} 
	
	@Override
	public boolean onBackKeyDown() {
		
		if (isUpdate) {
			Bundle data = getArguments();
			Boolean hasUpdated = updateNote(data);	
			if (hasUpdated) {
				String message = "Note has been updated"; 
				Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
			}
			return true;
		}
/*		
		String title = mInputTitle.getText().toString();
		title = title.trim();
		
		String description = mInputDescription.getText().toString();
		description = description.trim();
		
		Long timestamp = System.currentTimeMillis();
		
		if (TextUtils.isEmpty(title) && TextUtils.isEmpty(description)) {
			return true;
		} 
		
		if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {
			String message = "Fill in or clear both input fields"; 
			Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
			return false;
		}

		boolean noteStored = storeNote(title, description, timestamp);
*/
		boolean noteStored = storeNote();
		return noteStored;
	}
	
public void setEditText(Bundle data) {
		
		Long id = data.getLong(EXTRA_NOTE_ID);

		Uri selectedNoteUri = ContentUris.withAppendedId(NotesProvider.CONTENT_URI, id);
		
		Cursor cursor = mContentResolver.query(selectedNoteUri, null, null,
				null, null);
		
		cursor.moveToFirst();
		
		String title = cursor.getString(cursor.getColumnIndex(NotesProvider.KEY_TITLE));
		mInputTitle.setText(title);
	
		String description = cursor.getString(cursor.getColumnIndex(NotesProvider.KEY_DESCRIPTION));
		mInputDescription.setText(description);		
	}
	
	public void setupInput(View contentView) {
		
		TextWatcher watcher = createTextWatcher();
		
		mInputTitle = (EditText) contentView.findViewById(R.id.input_title);
		mInputTitle.addTextChangedListener(watcher);
		
		mInputDescription = (EditText) contentView.findViewById(R.id.input_description);
		mInputDescription.addTextChangedListener(watcher);
	}
	
	public boolean storeNote() {
		
		String title = mInputTitle.getText().toString();
		title = title.trim();
		
		String description = mInputDescription.getText().toString();
		description = description.trim();
		
		Long timestamp = System.currentTimeMillis();
		
		if (TextUtils.isEmpty(title) && TextUtils.isEmpty(description)) {
			return true;
		} 
		
		if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {
			String message = "Fill in or clear both input fields"; 
			Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
			return false;
		}
		
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
	
	public boolean updateNote(Bundle data) {
		
		if (!mTitleChanged && !mDescriptionChanged) {
			return false;
		}
		
		Long timestamp = System.currentTimeMillis();
		Long id = data.getLong(EXTRA_NOTE_ID);
		
		String title = mInputTitle.getText().toString();
		title = title.trim();
		
		String description = mInputDescription.getText().toString();
		description = description.trim();
		
		Uri selectedNoteUri = ContentUris.withAppendedId(NotesProvider.CONTENT_URI, id);
		
		ContentValues values = new ContentValues();	
		
		if (mTitleChanged) {
			values.put(NotesProvider.KEY_TITLE, title);
		}
		if (mDescriptionChanged) {
			values.put(NotesProvider.KEY_DESCRIPTION, description);
		}
		
		values.put(NotesProvider.KEY_TIMESTAMP, timestamp);
		int count = mContentResolver.update(selectedNoteUri, values, null, null);
		
		if (count > 0) {
			return true;
		}
		return false;
	}
	
	public TextWatcher createTextWatcher() {
		
		return new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				checkTextChange();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		};
	}
	
	public void setStringsFromDatabase(Bundle data) {
		
		Long id = data.getLong(EXTRA_NOTE_ID);
		
		Uri selectedNoteUri = ContentUris.withAppendedId(NotesProvider.CONTENT_URI, id);
		
		Cursor cursor = mContentResolver.query(selectedNoteUri, null, null,
				null, null);
		
		cursor.moveToFirst();
		
		mTitleInDatabase = cursor.getString(cursor.getColumnIndex(NotesProvider.KEY_TITLE));
		mDescriptionInDatabase = cursor.getString(cursor.getColumnIndex(NotesProvider.KEY_DESCRIPTION));	
	}
	
	public void checkTextChange() {

		if (isUpdate) {
			String titleInEditText = mInputTitle.getText().toString();
			titleInEditText = titleInEditText.trim();
			mTitleChanged = !mTitleInDatabase.equals(titleInEditText);
			
			String descriptionInEditText = mInputDescription.getText().toString();
			descriptionInEditText = descriptionInEditText.trim();
			mDescriptionChanged = !mDescriptionInDatabase.equals(descriptionInEditText);
		}
	}
	
}
