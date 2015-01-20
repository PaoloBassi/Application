package it.unozerouno.givemetime.view.editor.constraints;

import it.unozerouno.givemetime.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

public class SingleConstraintFragment extends Fragment {
	Spinner daySpinner;
	Spinner dateSpinner;
	Spinner timeSpinner;
	Button addBtn;
	Button removeBtn;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.editor_constraint_single_fragment, container);
		dateSpinner = (Spinner)view.findViewById(R.id.editor_constraint_single_spinner_date);
		daySpinner = (Spinner)view.findViewById(R.id.editor_constraint_single_spinner_day);
		timeSpinner = (Spinner)view.findViewById(R.id.editor_constraint_single_spinner_time);
		addBtn = (Button) view.findViewById(R.id.editor_constraint_single_btn_add);
		removeBtn = (Button) view.findViewById(R.id.editor_constraint_single_btn_remove);
		return view;
	}
}
