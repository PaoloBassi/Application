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

public class TimeEndPickerFragment extends DialogFragment implements OnTimeSetListener{

	private int hour;
	private int minute;
	private EventEditorActivity activity;
	
	public TimeEndPickerFragment(EventEditorActivity eea) {
		activity = eea;
		hour = eea.getEnd().hour;
		minute = eea.getEnd().minute;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		// use the time appearing in the textview
		
		return new TimePickerDialog(getActivity(), this, hour, minute, true);
	}
	
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		// set the time chosen by the user inside the textview
		activity.getSpinnerEndTime().setText(CalendarUtils.formatHour(hourOfDay, minute));
		// set the time chosen inside the variable start
		Time newEndTime = activity.getEnd();
		newEndTime.hour = hourOfDay;
		newEndTime.minute = minute;
		activity.setEnd(newEndTime);
		
		// if the new end happens before the start, then set the start accordingly. Otherwise, do nothing
		if (newEndTime.before(activity.getStart())){
			// set the time of the start 1 hour earlier
			Time newStartTime = activity.getStart();
			if ((hourOfDay - 1) < 0){
				newStartTime.hour = 23;
				// TODO change the day
			} else {
				newStartTime.hour = hourOfDay - 1;
			}
			activity.setStart(newStartTime);
			activity.getSpinnerStartTime().setText(CalendarUtils.formatHour(newStartTime.hour, minute));
		}
	}

}
