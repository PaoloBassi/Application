package it.unozerouno.givemetime.view.utilities;

import it.unozerouno.givemetime.view.editor.EventEditorActivity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

public class DayStartPickerFragment extends DialogFragment implements OnDateSetListener{

	private int day;
	private int month;
	private int year;
	private EventEditorActivity activity;
	
	public DayStartPickerFragment(EventEditorActivity eea) {
		activity = eea;
		day = eea.getStart().monthDay;
		month = eea.getStart().month + 1;
		year = eea.getStart().year;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		// use the time appearing in the textview
		
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		
		activity.getSpinnerStartDay().setText(dayOfMonth + "/" + monthOfYear + "/" + year);
		
	}

}
