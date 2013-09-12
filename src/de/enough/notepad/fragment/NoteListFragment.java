package de.enough.notepad.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;
import de.enough.notepad.R;
import de.enough.notepad.adapter.NotesAdapter;
import de.enough.notepad.provider.NotesProvider;

public class NoteListFragment extends Fragment implements LoaderCallbacks<Cursor> {
	
	public interface Listener {
		public void onNewNote();
	}
	
	private static final String CONTEXT_MENU_ITEM_HEADER_TITLE = "Delete note";
	private static final String CONTEXT_MENU_ITEM_DELETE = "Delete";
	private Context mContext;
	private ContentResolver mContentResolver;
	private NotesAdapter mAdapter;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {

		View contentView = inflater.inflate(R.layout.fragment_note_list, container, false);
		
		setHasOptionsMenu(true);
		
		getLoaderManager().initLoader(0, null, this);

		return contentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mAdapter = new NotesAdapter(mContext, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER );
		
		ListView list = (ListView)getView().findViewById(R.id.list);
		list.setAdapter(mAdapter);
		registerForContextMenu(list);
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

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle(CONTEXT_MENU_ITEM_HEADER_TITLE);  
        menu.add(0, v.getId(), 0, CONTEXT_MENU_ITEM_DELETE);  
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		String itemTitle = (String)item.getTitle();
		if (itemTitle.equals(CONTEXT_MENU_ITEM_DELETE)) {			 
			AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			long id = menuInfo.id;
			deleteSelectedNote(id);
			return true;
		}
		return false;
	}
	
	public void deleteSelectedNote(long id) {
		Uri selectedNoteUri = ContentUris.withAppendedId(NotesProvider.CONTENT_URI, id);
		mContentResolver.delete(selectedNoteUri, "", null);
		String message = "Note has been deleted";
		Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle bundle) {
		return new CursorLoader(mContext, NotesProvider.CONTENT_URI, null, null, null, NotesProvider.KEY_TIMESTAMP + " DESC ");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.swapCursor(cursor);	
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}
	
}
