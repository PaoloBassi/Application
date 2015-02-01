package it.unozerouno.givemetime.view.editor.constraints;

import it.unozerouno.givemetime.R;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class DoubleDayPickerDialog extends DialogFragment{
	Spinner startSpinner;
	Spinner endSpinner;
	Button okBtn;
	Button cancelBtn;
	OnConstraintSelectedListener listener;
	int startSelected;
	int endSelected;
	
	public DoubleDayPickerDialog(OnConstraintSelectedListener callBack) {
	super();
	listener=callBack;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.editor_constraint_daypicker_double, container, false);
		startSpinner = (Spinner) view.findViewById(R.id.editor_constraint_daypicker_start);
		endSpinner = (Spinner) view.findViewById(R.id.editor_constraint_daypicker_end);
		okBtn = (Button) view.findViewById(R.id.editor_constraint_daypicker_btn_save);
		cancelBtn = (Button) view.findViewById(R.id.editor_constraint_daypicker_btn_cancel);
		setButtonListener();
		getDialog().setTitle(R.string.editor_constraints_daypicker_title);
		setSpinnerAdapters();
		
		
		return view;
	}
	
	
	private void setSpinnerAdapters(){
		String[] values = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
		ArrayAdapter<String> spinnerAdapter= new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item,values);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		startSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				startSelected = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		endSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				endSelected = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		startSpinner.setAdapter(spinnerAdapter);
		endSpinner.setAdapter(spinnerAdapter);
	}
	
	
	
	
	 private void setButtonListener() {
		 
		okBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
					listener.onDaySelected(startSelected, endSelected);
					DoubleDayPickerDialog.this.dismiss();
			}
		});
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DoubleDayPickerDialog.this.dismiss();
			}
		});
	}


	abstract static class OnConstraintSelectedListener{
		abstract void onDaySelected(int startDay, int endDay);
		abstract void dayNotSelected();
	}
}
