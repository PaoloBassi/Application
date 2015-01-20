package it.unozerouno.givemetime.view.editor.constraints;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.google.android.gms.internal.cu;
import com.google.android.gms.internal.di;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.model.constraints.ComplexConstraint;
import it.unozerouno.givemetime.model.constraints.Constraint;
import it.unozerouno.givemetime.model.constraints.DateConstraint;
import it.unozerouno.givemetime.model.constraints.DayConstraint;
import it.unozerouno.givemetime.model.constraints.TimeConstraint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Fragment;
import android.os.Bundle;
import android.provider.Contacts.SettingsColumns;
import android.provider.Settings.System;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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
	TimeConstraint timeConstraint;
	DayConstraint dayConstraint;
	DateConstraint dateConstraint;
	
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
				if(position==0){
					dateConstraint = null;
				}
				if(position==1){
					Time now = new Time();
					now.setToNow();
					//Showing the starting date picker
					new DatePickerDialog(getActivity(), new OnDateSetListener() {
						
						@Override
						public void onDateSet(DatePicker view, int year, int monthOfYear,
								int dayOfMonth) {
							//Creating and adding the constraint
							Time selectedStart = new Time();
							selectedStart.set(dayOfMonth, monthOfYear, year);
							//TODO: kill this
						}
					}, now.year, now.month, now.monthDay).show();
					
				
				}
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
	/**
	 * This fills the ComplexConstraint with selected constraints 
	 */
	private void updateConstraint(){
		ArrayList<Constraint> constraintList = new ArrayList<Constraint>();
		if (dayConstraint!=null){
			constraintList.add(dayConstraint);
		}
		if (dateConstraint!=null){
			constraintList.add(dateConstraint);
		}
		if (timeConstraint!=null){
			constraintList.add(timeConstraint);
		}
		constraintToShow.setConstraints(constraintList);
	}
	
	/**
	 * Updates the constraints into the ComplexConstraint and returns it
	 * @return
	 */
	public ComplexConstraint getConstraint(){
		updateConstraint();
		return constraintToShow;
	}
	
	private void showConstraint(){
		for (Constraint currentConstraint : constraintToShow.getConstraints()) {
			if (currentConstraint instanceof DayConstraint){
				DayConstraint constraint = (DayConstraint) currentConstraint;
				int start = constraint.getStartingDay();
				int end = constraint.getEndingDay();
				//TODO: parse numbers to days
				if (start == end){
					selectedDayText.setText("On " + start);
				} else {
					selectedDayText.setText("From " + start + " to " + end);
				}
				
			}
			if (currentConstraint instanceof DateConstraint){
				DateConstraint constraint = (DateConstraint) currentConstraint;
				Calendar displayer = Calendar.getInstance();
				Time start = constraint.getStartingDate();
				Time end = constraint.getEndingDate();
				displayer.setTimeInMillis(start.toMillis(false));
				String dayStart = displayer.getDisplayName(Calendar.DATE, Calendar.SHORT, Locale.getDefault());
				String monthStart = displayer.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
				displayer.setTimeInMillis(end.toMillis(false));
				
				String dayEnd = displayer.getDisplayName(Calendar.DATE, Calendar.SHORT, Locale.getDefault());
				String monthEnd = displayer.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
				selectedDayText.setText("From " + monthStart + " "+ dayStart + " to " + monthEnd+ " " + dayEnd);
			}
			if (currentConstraint instanceof TimeConstraint){
				TimeConstraint constraint = (TimeConstraint) currentConstraint;
				Calendar displayer = Calendar.getInstance();
				Time start = constraint.getStartingTime();
				Time end = constraint.getEndingTime();
				displayer.setTimeInMillis(start.toMillis(false));
				String hourStart = displayer.getDisplayName(Calendar.HOUR, Calendar.SHORT, Locale.getDefault());
				String minStart = displayer.getDisplayName(Calendar.MINUTE, Calendar.SHORT, Locale.getDefault());
				displayer.setTimeInMillis(end.toMillis(false));
				
				String hourEnd = displayer.getDisplayName(Calendar.HOUR, Calendar.SHORT, Locale.getDefault());
				String minEnd = displayer.getDisplayName(Calendar.MINUTE, Calendar.SHORT, Locale.getDefault());
				selectedDayText.setText("From " + hourStart + " "+ minStart + " to " + hourEnd+ " " + minEnd);
			}
		}
	};
	
}
