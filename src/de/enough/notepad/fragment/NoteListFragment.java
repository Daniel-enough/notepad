package de.enough.notepad.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import de.enough.notepad.R;
import de.enough.notepad.adapter.NotesAdapter;
import de.enough.notepad.provider.NotesProvider;

public class NoteListFragment extends Fragment {
	
	public interface Listener {
		public void onNewNote();
		public void onNoteSelected(long id);
	}
	
	private Context mContext;
	private ContentResolver mContentResolver;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {

		View contentView = inflater.inflate(R.layout.fragment_note_list, container, false);
		
		setHasOptionsMenu(true);
		
		return contentView;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		ListView list = (ListView)getView().findViewById(R.id.list);
		list.setOnItemClickListener(createOnItemClickListener());	
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Cursor cursor = mContentResolver.query(NotesProvider.CONTENT_URI, null, null,
				null, NotesProvider.KEY_TIMESTAMP + " DESC");
		NotesAdapter adapter = new NotesAdapter(mContext, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER );
		
		ListView list = (ListView)getView().findViewById(R.id.list);
		list.setAdapter(adapter);
	}
	
	@Override
	public void onAttach(Activity activity) {
		mContext = getActivity().getBaseContext();
		mContentResolver = mContext.getContentResolver();	
		super.onAttach(activity);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_list, menu);
		super.onCreateOptionsMenu(menu,inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_new_note:
			Activity activity = getActivity();
			if (activity instanceof Listener) {
				((Listener) activity).onNewNote();
				return true;
			}
			return false;	
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public OnItemClickListener createOnItemClickListener() {
		
		return new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				Activity activity = getActivity();
				if (activity instanceof Listener) {
					((Listener) activity).onNoteSelected(id);
				}	
			}
		};
	}
}
