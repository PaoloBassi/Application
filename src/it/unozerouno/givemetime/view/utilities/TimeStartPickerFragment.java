package it.unozerouno.givemetime.view.utilities;

import it.unozerouno.givemetime.utils.CalendarUtils;
import it.unozerouno.givemetime.view.editor.EventEditorActivity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.text.format.Time;
import android.widget.TimePicker;

public class TimeStartPickerFragment extends DialogFragment implements OnTimeSetListener{

	private int hour;
	private int minute;
	private EventEditorActivity activity;
	
	public TimeStartPickerFragment(EventEditorActivity eea) {
		activity = eea;
		hour = eea.getStart().hour;
		minute = eea.getStart().minute;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		// use the time appearing in the textview
		
		return new TimePickerDialog(getActivity(), this, hour, minute, true);
	}
	
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		// set the time chosen by the user inside the textview
		activity.getSpinnerStartTime().setText(CalendarUtils.formatHour(hourOfDay, minute));
		// set the time chosen inside the variable start
		Time newStartTime = activity.getStart();
		newStartTime.hour = hourOfDay;
		newStartTime.minute = minute;
		activity.setStart(newStartTime);
		
		// if the new start happens after the end, then set the end accordingly. Otherwise, do nothing
		if (newStartTime.after(activity.getEnd())){
			// set the time of the end 1 hour later
			Time newEndTime = activity.getEnd();
			if ((hourOfDay + 1) > 23){
				newEndTime.hour = 0;
				// TODO change the day
			} else {
				newEndTime.hour = hourOfDay + 1;
			}
			activity.setEnd(newEndTime);
			activity.getSpinnerEndTime().setText(CalendarUtils.formatHour(newEndTime.hour, minute));
		}
	}

}
