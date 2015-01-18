package it.unozerouno.givemetime.view.utilities;

import it.unozerouno.givemetime.utils.CalendarUtils;
import it.unozerouno.givemetime.view.editor.EventEditorActivity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.widget.TimePicker;

public class TimeEndPickerFragment extends DialogFragment implements OnTimeSetListener{

	private int hour;
	private int minute;
	private EventEditorActivity activity;
	
	public TimeEndPickerFragment(EventEditorActivity eea) {
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
		activity.getSpinnerEndTime().setText(CalendarUtils.formatHour(hourOfDay, minute));
		
		
	}

}
