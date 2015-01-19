package it.unozerouno.givemetime.view.utilities;

import it.unozerouno.givemetime.view.editor.EventEditorActivity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.format.Time;
import android.widget.DatePicker;

public class DayEndPickerFragment extends DialogFragment implements OnDateSetListener{

	private int day;
	private int month;
	private int year;
	private EventEditorActivity activity;
	
	public DayEndPickerFragment(EventEditorActivity eea) {
		activity = eea;
		day = eea.getEnd().monthDay;
		month = eea.getEnd().month;
		year = eea.getEnd().year;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		// use the time appearing in the textview
		
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// set the text of the data
		activity.getSpinnerEndDay().setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
		// set the variable referring to the starting data
		Time newEndDay = activity.getEnd();
		newEndDay.monthDay = dayOfMonth;
		newEndDay.month = monthOfYear;
		newEndDay.year = year;
		activity.setEnd(newEndDay);
		
		// if the new end happens after the start, then set the start accordingly. Otherwise, do nothing
		if (newEndDay.before(activity.getStart())){
			// set the time of the start the same day as the end
			activity.setStart(newEndDay);
			activity.getSpinnerStartDay().setText(newEndDay.monthDay + "/" + (newEndDay.month + 1) + "/" + newEndDay.year);
		}
		
	}

}