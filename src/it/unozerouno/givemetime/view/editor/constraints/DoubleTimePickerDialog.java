package it.unozerouno.givemetime.view.editor.constraints;

import it.unozerouno.givemetime.R;


import android.app.DialogFragment;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

public class DoubleTimePickerDialog extends DialogFragment{
	TimePicker startPicker;
	TimePicker endPicker;
	Button okBtn;
	Button cancelBtn;
	OnConstraintSelectedListener listener;
	
	public DoubleTimePickerDialog(OnConstraintSelectedListener callBack) {
	super();
	listener=callBack;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.editor_constraint_timepicker_double, container);
		startPicker = (TimePicker) view.findViewById(R.id.editor_constraint_timepicker_start);
		endPicker = (TimePicker) view.findViewById(R.id.editor_constraint_timepicker_end);
		okBtn = (Button) view.findViewById(R.id.editor_constraint_timepicker_btn_save);
		cancelBtn = (Button) view.findViewById(R.id.editor_constraint_timepicker_btn_cancel);
		setButtonListener();
		getDialog().setTitle(R.string.editor_constraints_timepicker_title);
		return view;
	}
	
	
	 private void setButtonListener() {
		 
		okBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Getting start time
				int startHour = startPicker.getCurrentHour();
				int startMinute = startPicker.getCurrentMinute();
				//Getting ending time
				int endHour = endPicker.getCurrentHour();
				int endMinute = endPicker.getCurrentMinute();
				Time startTime = new Time();
				Time endTime = new Time();
				startTime.setJulianDay(Time.EPOCH_JULIAN_DAY);
				endTime.setJulianDay(Time.EPOCH_JULIAN_DAY);
				startTime.hour = startHour;
				startTime.minute = startMinute;
				endTime.hour = endHour;
				endTime.minute = endMinute;
				
				if(!startTime.after(endTime)){
					listener.onTimeSelected(startTime, endTime);
					DoubleTimePickerDialog.this.dismiss();
				} else {
					Toast.makeText(getActivity(), "Cannot select a negative interval", Toast.LENGTH_SHORT).show();
				}
			}
		});
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DoubleTimePickerDialog.this.dismiss();
			}
		});
	}


	abstract static class OnConstraintSelectedListener{
		abstract void onTimeSelected(Time startTime, Time endTime);
		abstract void timeNotSelected();
	}
}
