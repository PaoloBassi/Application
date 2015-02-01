package it.unozerouno.givemetime.view.editor.constraints;


import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.model.constraints.ComplexConstraint;
import it.unozerouno.givemetime.model.constraints.Constraint;
import it.unozerouno.givemetime.model.constraints.DateConstraint;
import it.unozerouno.givemetime.model.constraints.DayConstraint;
import it.unozerouno.givemetime.model.constraints.TimeConstraint;
import it.unozerouno.givemetime.utils.GiveMeLogger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SingleConstraintFragment extends Fragment {
	TextView selectedDayText;
	TextView selectedDateText;
	TextView selectedTimeText;
	ToggleButton timeButton;
	ToggleButton dateButton;
	ToggleButton dayButton;
	OnRemoveButtonClickedListener listener;
	
	Button removeBtn;
	//Single constraints, they are copies of the ones retrieved from complex constraint
	TimeConstraint timeConstraint;
	DayConstraint dayConstraint;
	DateConstraint dateConstraint;
	//Constraint passed from EventEditor, or a new constraint to be created
	ComplexConstraint complexConstraint;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.editor_constraint_single_fragment, container, false);
		dateButton = (ToggleButton)view.findViewById(R.id.editor_constraint_single_button_date);
		dayButton = (ToggleButton)view.findViewById(R.id.editor_constraint_single_button_day);
		timeButton = (ToggleButton)view.findViewById(R.id.editor_constraint_single_button_time);
		removeBtn = (Button) view.findViewById(R.id.editor_constraint_single_btn_remove);
		selectedDayText= (TextView) view.findViewById(R.id.editor_constraint_single_selected_day);
		selectedDateText= (TextView) view.findViewById(R.id.editor_constraint_single_selected_date);
		selectedTimeText= (TextView) view.findViewById(R.id.editor_constraint_single_selected_time);
		if (complexConstraint != null) setConstraint(complexConstraint);
		initializeButtons();
		return view;
	}
	
	
	public void setListener (OnRemoveButtonClickedListener listener){
		this.listener = listener;
	}
	
	private void initializeButtons(){
		dateButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					showDateDialog();
				}
				else{
					dateConstraint=null;
					updateText();
				}
				
			}
		});
		dayButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					showDayDialog();
				}
				else{
					dayConstraint=null;
					updateText();
				}
				
			}
		});
		timeButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					showTimeDialog();
				}
				else{
					timeConstraint=null;
					updateText();
				}
				
			}
		});
		removeBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				listener.onRemoveButtonClicked(SingleConstraintFragment.this);				
			}
		});
	}
	
	private void showDateDialog(){
		DoubleDatePickerDialog datePickerDialog = new DoubleDatePickerDialog(new DoubleDatePickerDialog.OnConstraintSelectedListener() {
			
			@Override
			void onDateSelected(Time startTime, Time endTime) {
				dateConstraint = new DateConstraint(startTime, endTime);
				GiveMeLogger.log("Got start: " + startTime.toString() + " end: " + endTime.toString());
				updateText();
			}

			@Override
			void dateNotSelected() {
				dateButton.setChecked(false);							
			}
		});
		datePickerDialog.show(getActivity().getFragmentManager(), getTag());
	}
	private void showDayDialog(){
		DoubleDayPickerDialog dayPickerDialog = new DoubleDayPickerDialog(new DoubleDayPickerDialog.OnConstraintSelectedListener() {
			
			@Override
			void onDaySelected(int startDay, int endDay) {
				dayConstraint = new DayConstraint(startDay, endDay);
				GiveMeLogger.log("Got start: " + startDay + " end: " + endDay);
				updateText();
			}

			@Override
			void dayNotSelected() {
				dayButton.setChecked(false);						
			}
		});
		dayPickerDialog.show(getActivity().getFragmentManager(), getTag());
	}
	private void showTimeDialog(){

		DoubleTimePickerDialog timePickerDialog = new DoubleTimePickerDialog(new DoubleTimePickerDialog.OnConstraintSelectedListener() {
			
			@Override
			void onTimeSelected(Time startTime, Time endTime) {
				timeConstraint = new TimeConstraint(startTime, endTime);
				GiveMeLogger.log("Got start: " + startTime.toString() + " end: " + endTime.toString());
				updateText();
			}

			@Override
			void timeNotSelected() {
				timeButton.setChecked(false);							
			}
		});
		timePickerDialog.show(getActivity().getFragmentManager(), getTag());
	}
	
	public void setConstraint(ComplexConstraint constraint){
		if (constraint==null) complexConstraint = new ComplexConstraint();
		this.complexConstraint = constraint;
		for (Constraint currentConstraint : constraint.getConstraints()) {
			if (currentConstraint instanceof DayConstraint){
				dayConstraint = ((DayConstraint)currentConstraint).clone();
				dayButton.setChecked(true);
			}
			if (currentConstraint instanceof DateConstraint){
				dateConstraint = ((DateConstraint)currentConstraint).clone();
				dateButton.setChecked(true);
			}
			if (currentConstraint instanceof TimeConstraint){
				timeConstraint = ((TimeConstraint)currentConstraint).clone();
				timeButton.setChecked(true);
			}
		}
		updateText();
	}
	
	
	
	/**
	 * Updates the constraints into the ComplexConstraint and returns it
	 * @return
	 */
	public ComplexConstraint getConstraint(){
		
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
		if (complexConstraint == null){
			complexConstraint=new ComplexConstraint();
		}
		complexConstraint.setConstraints(constraintList);
		return complexConstraint;
	}
	
	private void updateText(){
		if (dayConstraint != null){
		int start = dayConstraint.getStartingDay();
		int end = dayConstraint.getEndingDay();
		//TODO: parse numbers to days
		if (start == end){
			selectedDayText.setText("On " + start);
		} else {
			selectedDayText.setText("From " + start + " to " + end);
		}
		}
		if(dateConstraint!=null){
			Calendar displayer = Calendar.getInstance();
			Time start = dateConstraint.getStartingDate();
			Time end = dateConstraint.getEndingDate();
			displayer.setTimeInMillis(start.toMillis(false));
			String dayStart = displayer.getDisplayName(Calendar.DATE, Calendar.SHORT, Locale.getDefault());
			String monthStart = displayer.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
			displayer.setTimeInMillis(end.toMillis(false));
			
			String dayEnd = displayer.getDisplayName(Calendar.DATE, Calendar.SHORT, Locale.getDefault());
			String monthEnd = displayer.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
			selectedDayText.setText("From " + monthStart + " "+ dayStart + " to " + monthEnd+ " " + dayEnd);
		}
		if(timeConstraint != null){
			Calendar displayer = Calendar.getInstance();
			Time start = timeConstraint.getStartingTime();
			Time end = timeConstraint.getEndingTime();
			displayer.setTimeInMillis(start.toMillis(false));
			String hourStart = displayer.getDisplayName(Calendar.HOUR, Calendar.SHORT, Locale.getDefault());
			String minStart = displayer.getDisplayName(Calendar.MINUTE, Calendar.SHORT, Locale.getDefault());
			displayer.setTimeInMillis(end.toMillis(false));
			
			String hourEnd = displayer.getDisplayName(Calendar.HOUR, Calendar.SHORT, Locale.getDefault());
			String minEnd = displayer.getDisplayName(Calendar.MINUTE, Calendar.SHORT, Locale.getDefault());
			selectedDayText.setText("From " + hourStart + " "+ minStart + " to " + hourEnd+ " " + minEnd);
		}
	}
	
		
	public static abstract class OnRemoveButtonClickedListener{
		public abstract void onRemoveButtonClicked(SingleConstraintFragment fragment);
	}
}
