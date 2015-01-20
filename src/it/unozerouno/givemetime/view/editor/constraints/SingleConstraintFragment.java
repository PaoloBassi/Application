package it.unozerouno.givemetime.view.editor.constraints;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.model.constraints.ComplexConstraint;
import it.unozerouno.givemetime.model.constraints.Constraint;
import it.unozerouno.givemetime.model.constraints.DayConstraint;
import android.app.Fragment;
import android.os.Bundle;
import android.provider.Contacts.SettingsColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SingleConstraintFragment extends Fragment {
	TextView selectedDayText;
	TextView selectedDateText;
	TextView selectedTimeText;
	Spinner daySpinner;
	Spinner dateSpinner;
	Spinner timeSpinner;
	ArrayAdapter<String> spinnerAdapter; 
	Button addBtn;
	Button removeBtn;
	ComplexConstraint constraintToShow;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.editor_constraint_single_fragment, container);
		dateSpinner = (Spinner)view.findViewById(R.id.editor_constraint_single_spinner_date);
		daySpinner = (Spinner)view.findViewById(R.id.editor_constraint_single_spinner_day);
		timeSpinner = (Spinner)view.findViewById(R.id.editor_constraint_single_spinner_time);
		addBtn = (Button) view.findViewById(R.id.editor_constraint_single_btn_add);
		removeBtn = (Button) view.findViewById(R.id.editor_constraint_single_btn_remove);
		selectedDayText= (TextView) view.findViewById(R.id.editor_constraint_single_selected_day);
		selectedDateText= (TextView) view.findViewById(R.id.editor_constraint_single_selected_date);
		selectedTimeText= (TextView) view.findViewById(R.id.editor_constraint_single_selected_time);
		initializeSpinners();
		constraintToShow = new ComplexConstraint();
		return view;
	}
	private void initializeSpinners(){
		String[] values = {"Ever","Edit.."};
		spinnerAdapter= new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item,values);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		dateSpinner.setAdapter(spinnerAdapter);
		timeSpinner.setAdapter(spinnerAdapter);
		daySpinner.setAdapter(spinnerAdapter);
		
		dateSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View view,
					int position, long id) {
				Toast.makeText(SingleConstraintFragment.this.getActivity(), "Clicked pos " + position, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				Toast.makeText(SingleConstraintFragment.this.getActivity(), "Nothing selected", Toast.LENGTH_SHORT).show();				
			}

		});

	}
	
	public void setConstraint(ComplexConstraint constraint){
		constraintToShow = constraint;
		showConstraint();
	}
	
	private void showConstraint(){
		for (Constraint currentConstraint : constraintToShow.getConstraints()) {
			if (currentConstraint instanceof DayConstraint){
				
			}
			if (currentConstraint instanceof DayConstraint){
				
			}
			if (currentConstraint instanceof DayConstraint){
				
			}
		}
	};
	
}
