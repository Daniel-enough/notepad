package de.enough.notepad;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class NoteFragment extends Fragment {
	
	private EditText mInputTitle;
	private EditText mInputDescription;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View contentView = inflater.inflate(R.layout.fragment_note, container, false);
		setupInput(contentView);
		return contentView;
	}
	
	
	public void setupInput(View contentView) {
		mInputTitle = (EditText) contentView.findViewById(R.id.input_title);
		mInputDescription = (EditText) contentView.findViewById(R.id.input_description);
	}

	
}
