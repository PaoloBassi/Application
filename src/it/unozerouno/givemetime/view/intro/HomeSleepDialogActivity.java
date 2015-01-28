package it.unozerouno.givemetime.view.intro;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.utils.CalendarUtils;
import it.unozerouno.givemetime.view.editor.EventEditorActivity;
import it.unozerouno.givemetime.view.utilities.TimeEndPickerFragment;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

public class HomeSleepDialogActivity extends Activity{
	
	private Switch isSleeping;
	private TextView wakeUpText;
	private TextView sleepText;
	private TextView start;
	private TextView end;
	private TimePickerDialog sleep;
	private TimePickerDialog wakeUp;
	private Time sevenAM;
	private Time elevenPM;
	private Time setBegin;
	private Time setEnd;
	private Button btnCancel;
	private Button btnConfirm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_sleep_time);
		
		sevenAM = new Time();
		elevenPM = new Time();
		sevenAM.hour = 7;
		sevenAM.minute = 0;
		sevenAM.second = 0;
		elevenPM.hour = 23;
		elevenPM.minute = 0;
		elevenPM.second = 0;
		
		wakeUpText = (TextView) findViewById(R.id.home_start_day_textview);
		sleepText = (TextView) findViewById(R.id.home_end_day_textview);
		start = (TextView) findViewById(R.id.home_spinner_start_day);
		end = (TextView) findViewById(R.id.home_spinner_end_day);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnConfirm = (Button) findViewById(R.id.btnConfirm);
		
		isSleeping = (Switch) findViewById(R.id.home_switch_sleep_time);
		isSleeping.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// enable all elements
				if (isSleeping.isChecked()) {
					wakeUpText.setEnabled(false);
					sleepText.setEnabled(false);
					start.setEnabled(false);
					end.setEnabled(false);
					start.setText(CalendarUtils.formatHour(-1, -1));
					end.setText(CalendarUtils.formatHour(-1, -1));
				} else {
					wakeUpText.setEnabled(true);
					sleepText.setEnabled(true);
					start.setEnabled(true);
					end.setEnabled(true);
					setBegin = new Time(sevenAM);
					setEnd = new Time(elevenPM);
					start.setText(CalendarUtils.formatHour(setBegin.hour, setBegin.minute));
					end.setText(CalendarUtils.formatHour(setEnd.hour, setEnd.minute));
				}
			}
		});
		
		start.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new WakeUpTimePicker();
				newFragment.show(getFragmentManager(), "Wake Up Picker");
				
			}
		});
		
		end.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				DialogFragment newFragment = new SleepTimePicker();
				newFragment.show(getFragmentManager(), "Sleep Picker");
				
			}
		});
		
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		btnConfirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO save the values somewhere
				finish();
			}
		});
	}
	
	/**
	 * private class for handling the pick of the time
	 * @author paolo
	 *
	 */
	
	private class WakeUpTimePicker extends DialogFragment implements OnTimeSetListener{

		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return new TimePickerDialog(getActivity(), this, sevenAM.hour, sevenAM.minute, true);
		}
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// set the values in the view
			start.setText(CalendarUtils.formatHour(hourOfDay, minute));
			// save the values inside the variable
			setBegin = new Time();
			setBegin.hour = hourOfDay;
			setBegin.minute = minute;
			
		}
		
	}
	
	
	/**
	 * private class for handling the pick of the time
	 * @author paolo
	 *
	 */
	
	private class SleepTimePicker extends DialogFragment implements OnTimeSetListener{

		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return new TimePickerDialog(getActivity(), this, elevenPM.hour, elevenPM.minute, true);
		}
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// set the values in the view
			end.setText(CalendarUtils.formatHour(hourOfDay, minute));
			// save the values inside the variable
			setEnd = new Time();
			setEnd.hour = hourOfDay;
			setEnd.minute = minute;
			
		}
		
	}
	

}
