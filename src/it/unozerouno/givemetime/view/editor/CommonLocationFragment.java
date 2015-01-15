package it.unozerouno.givemetime.view.editor;

import it.unozerouno.givemetime.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CommonLocationFragment extends Fragment {
 @Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
	inflater.inflate(R.layout.fragment_event_editor_locations, container);
	return super.onCreateView(inflater, container, savedInstanceState);
}
}
