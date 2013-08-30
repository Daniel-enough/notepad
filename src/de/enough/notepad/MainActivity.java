package de.enough.notepad;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Menu;

public class MainActivity extends FragmentActivity {

	public interface OnBackKeyListener {
		boolean onBackKeyDown();
	}
	
	private static final String TAG_FRAGMENT_NOTE = NoteFragment.class.getName();
	
	private NoteFragment mNoteFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState != null) {
			return;
		}
		
		mNoteFragment = new NoteFragment();
		
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.add(R.id.layout_holder, mNoteFragment, TAG_FRAGMENT_NOTE);
		fragmentTransaction.addToBackStack(TAG_FRAGMENT_NOTE);
		fragmentTransaction.commit();  
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Fragment currentFragment = getCurrentFragment();
		boolean isOnBackKeyDownListener = currentFragment instanceof OnBackKeyListener;
		if (keyCode != KeyEvent.KEYCODE_BACK || !isOnBackKeyDownListener) {
			return super.onKeyDown(keyCode, event);
		}

		OnBackKeyListener onBackKeyDownListener = (OnBackKeyListener) currentFragment;
		boolean handled = onBackKeyDownListener.onBackKeyDown();
		if (!handled) {
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	public Fragment getCurrentFragment() {
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

}
