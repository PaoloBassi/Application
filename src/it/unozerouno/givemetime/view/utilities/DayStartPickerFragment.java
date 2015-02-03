package it.unozerouno.givemetime.view.utilities;

import it.unozerouno.givemetime.view.editor.EventEditorActivity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.format.Time;
import android.widget.DatePicker;

public class DayStartPickerFragment extends DialogFragment implements OnDateSetListener{

	private int day;
	private int month;
	private int year;
	private Time dayStart;
	private EventEditorActivity activity;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (EventEditorActivity) activity;
	}
	
	public DayStartPickerFragment(Time dayStart) {
		this.dayStart = dayStart;
		System.out.println("ora rilevata: " + dayStart.hour  + " " + dayStart.minute);
		day = dayStart.monthDay;
		month = dayStart.month;
		year = dayStart.year;
		//activity = (EventEditorActivity) getActivity();
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
		activity.getSpinnerStartDay().setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
		// set the variable referring to the starting data
		Time newStartDay = dayStart;
		newStartDay.monthDay = dayOfMonth;
		newStartDay.month = monthOfYear;
		newStartDay.year = year;
		activity.setStart(newStartDay);
		System.out.println("ora settata: " + newStartDay.hour  + " " + newStartDay.minute);
		// if the new start happens after the end, then set the end accordingly. Otherwise, do nothing
		if (newStartDay.after(activity.getEnd())){
			// set the time of the end at the same day of the start
			Time end = activity.getEnd();
			end.monthDay = newStartDay.monthDay;
			end.month = newStartDay.month;
			end.year = newStartDay.year;
			activity.getSpinnerEndDay().setText(newStartDay.monthDay + "/" + (newStartDay.month + 1) + "/" + newStartDay.year);
		}
	}

}