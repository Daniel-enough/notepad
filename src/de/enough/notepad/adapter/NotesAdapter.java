package de.enough.notepad.adapter;

import de.enough.notepad.R;
import de.enough.notepad.provider.NotesProvider;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NotesAdapter extends CursorAdapter {
	
	private final LayoutInflater mInflater;
	

	public NotesAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
	
        String title = cursor.getString(cursor.getColumnIndex(NotesProvider.KEY_TITLE));
        TextView listTitle = (TextView)view.findViewById(R.id.list_title);
        listTitle.setText(title);

        String description = cursor.getString(cursor.getColumnIndex(NotesProvider.KEY_DESCRIPTION));
        TextView listDescription = (TextView)view.findViewById(R.id.list_description);
        listDescription.setText(description);	
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		
		return mInflater.inflate(R.layout.list_note, viewGroup, false);
	}

}
