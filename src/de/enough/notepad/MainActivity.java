package de.enough.notepad;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.RelativeLayout;
import de.enough.notepad.fragment.NoteInputFragment;
import de.enough.notepad.fragment.NoteListFragment;
import de.enough.notepad.fragment.NoteListFragment.Listener;

public class MainActivity extends FragmentActivity implements Listener {

	public interface OnBackKeyListener {
		boolean onBackKeyDown();
	}
	
	private static final String TAG_FRAGMENT_NOTE_LIST = NoteListFragment.class.getName();
	private static final String TAG_FRAGMENT_NOTE_INPUT = NoteInputFragment.class.getName();
	
	private NoteListFragment mNoteListFragment;
	private NoteInputFragment mNoteInputFragment;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		RelativeLayout layoutHolder = (RelativeLayout) findViewById(R.id.layout_holder);
		registerForContextMenu(layoutHolder);
		
		if (savedInstanceState != null) {
			return;
		}
		setupFragments();
		showList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Fragment fragmentOnBackStack = getFragmentOnBackStack();
		if(fragmentOnBackStack == null) {
			return super.onKeyDown(keyCode, event);
		}
		
		boolean isOnBackKeyDownListener = fragmentOnBackStack instanceof OnBackKeyListener;
		if (keyCode != KeyEvent.KEYCODE_BACK || !isOnBackKeyDownListener) {
			return super.onKeyDown(keyCode, event);
		}

		OnBackKeyListener onBackKeyDownListener = (OnBackKeyListener) fragmentOnBackStack;
		boolean handled = onBackKeyDownListener.onBackKeyDown();
		if (!handled) {
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
	
	private void setupFragments() {
		mNoteListFragment = new NoteListFragment();
		mNoteInputFragment = new NoteInputFragment();
	}
	
	public Fragment getFragmentOnBackStack() {
		FragmentManager supportFragmentManager = getSupportFragmentManager();
		if (supportFragmentManager.getBackStackEntryCount() == 0) {
			return null;
		}
		int currentFragmentIndex = supportFragmentManager
				.getBackStackEntryCount() - 1;
		BackStackEntry backStackEntry = supportFragmentManager
				.getBackStackEntryAt(currentFragmentIndex);
		String tag = backStackEntry.getName();
		return supportFragmentManager.findFragmentByTag(tag);
	}
	
	public void showList() {
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.add(R.id.layout_holder, mNoteListFragment, TAG_FRAGMENT_NOTE_LIST);
		fragmentTransaction.commit();  
	}
	
	public void showInput() {
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.layout_holder, mNoteInputFragment, TAG_FRAGMENT_NOTE_INPUT);
		fragmentTransaction.addToBackStack(TAG_FRAGMENT_NOTE_INPUT); 		
		fragmentTransaction.commit();  
	}

	@Override
	public void onNewNote() {
		showInput();	
	}
}
