package it.unozerouno.givemetime.view.intro;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.controller.fetcher.DatabaseManager;
import it.unozerouno.givemetime.model.UserKeyRing;
import it.unozerouno.givemetime.model.constraints.TimeConstraint;
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
	private TextView startView;
	private TextView endView;
	private TimePickerDialog sleep;
	private TimePickerDialog wakeUp;
	private Time setBegin;
	private Time setEnd;
	private Button btnCancel;
	private Button btnConfirm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_sleep_time);
		
		setBegin = new Time();
		setEnd = new Time();
		setBegin.hour = 7;
		setBegin.minute = 0;
		setBegin.second = 0;
		setEnd.hour = 23;
		setEnd.minute = 0;
		setEnd.second = 0;
		
		wakeUpText = (TextView) findViewById(R.id.home_start_day_textview);
		sleepText = (TextView) findViewById(R.id.home_end_day_textview);
		startView = (TextView) findViewById(R.id.home_spinner_start_day);
		endView = (TextView) findViewById(R.id.home_spinner_end_day);
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
					startView.setEnabled(false);
					endView.setEnabled(false);
					startView.setText(CalendarUtils.formatHour(-1, -1));
					endView.setText(CalendarUtils.formatHour(-1, -1));
				} else {
					wakeUpText.setEnabled(true);
					sleepText.setEnabled(true);
					startView.setEnabled(true);
					endView.setEnabled(true);
					startView.setText(CalendarUtils.formatHour(setBegin.hour, setBegin.minute));
					endView.setText(CalendarUtils.formatHour(setEnd.hour, setEnd.minute));
				}
			}
		});
		
		startView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new WakeUpTimePicker();
				newFragment.show(getFragmentManager(), "Wake Up Picker");
				
			}
		});
		
		endView.setOnClickListener(new OnClickListener() {
			
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
				// TODO needs the user account, precedence problem
				//DatabaseManager.setUserSleepTime(UserKeyRing.getUserEmail(HomeSleepDialogActivity.this), new TimeConstraint(setBegin, setEnd));
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
			return new TimePickerDialog(getActivity(), this, setBegin.hour, setBegin.minute, true);
		}
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// set the values in the view
			startView.setText(CalendarUtils.formatHour(hourOfDay, minute));
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
			return new TimePickerDialog(getActivity(), this, setEnd.hour, setEnd.minute, true);
		}
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// set the values in the view
			endView.setText(CalendarUtils.formatHour(hourOfDay, minute));
			// save the values inside the variable
			setEnd = new Time();
			setEnd.hour = hourOfDay;
			setEnd.minute = minute;
			
		}
		
	}
	

}
