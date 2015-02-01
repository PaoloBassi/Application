package it.unozerouno.givemetime.view.editor.constraints;

import it.unozerouno.givemetime.R;

import java.sql.Date;

import android.app.DialogFragment;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

public class DoubleDatePickerDialog extends DialogFragment{
	DatePicker startPicker;
	DatePicker endPicker;
	Button okBtn;
	Button cancelBtn;
	OnConstraintSelectedListener listener;
	
	public DoubleDatePickerDialog(OnConstraintSelectedListener callBack) {
	super();
	listener=callBack;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.editor_constraint_datepicker_double, container);
		startPicker = (DatePicker) view.findViewById(R.id.editor_constraint_datepicker_start);
		endPicker = (DatePicker) view.findViewById(R.id.editor_constraint_datepicker_end);
		okBtn = (Button) view.findViewById(R.id.editor_constraint_datepicker_btn_save);
		cancelBtn = (Button) view.findViewById(R.id.editor_constraint_datepicker_btn_cancel);
		setButtonListener();
		getDialog().setTitle(R.string.editor_constraints_datepicker_title);
		return view;
	}
	
	
	 private void setButtonListener() {
		 
		okBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Getting start date
				int startDay = startPicker.getDayOfMonth();
				int startMonth = startPicker.getMonth();
				int startYear = startPicker.getYear();
				//Getting ending date
				int endDay = endPicker.getDayOfMonth();
				int endMonth = endPicker.getMonth();
				int endYear = endPicker.getYear();
				Time startTime = new Time();
				Time endTime = new Time();
				
				startTime.set(startDay, startMonth, startYear);
				endTime.set(endDay, endMonth, endYear);
				if(!startTime.after(endTime)){
					listener.onDateSelected(startTime, endTime);
					DoubleDatePickerDialog.this.dismiss();
				} else {
					Toast.makeText(getActivity(), "Cannot select a negative interval", Toast.LENGTH_SHORT).show();
				}
			}
		});
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DoubleDatePickerDialog.this.dismiss();
			}
		});
	}


	abstract static class OnConstraintSelectedListener{
		abstract void onDateSelected(Time startTime, Time endTime);
		abstract void dateNotSelected();
	}
}
