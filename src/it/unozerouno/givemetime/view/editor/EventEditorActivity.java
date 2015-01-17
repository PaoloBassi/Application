package it.unozerouno.givemetime.view.editor;

import com.google.android.gms.games.event.Events.LoadEventsResult;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.model.events.EventDescriptionModel;
import it.unozerouno.givemetime.model.events.EventInstanceModel;
import it.unozerouno.givemetime.model.events.EventModel;
import it.unozerouno.givemetime.model.places.PlaceModel;
import it.unozerouno.givemetime.utils.CalendarUtils;
import it.unozerouno.givemetime.view.utilities.DayEndPickerFragment;
import it.unozerouno.givemetime.view.utilities.DayStartPickerFragment;
import it.unozerouno.givemetime.view.utilities.TimeEndPickerFragment;
import it.unozerouno.givemetime.view.utilities.TimeStartPickerFragment;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

public class EventEditorActivity extends Activity{
	EventInstanceModel eventToEdit;
	ScrollView scrollView;
	EditText editEventTitle;
	EditText editEventLocation;
	CommonLocationFragment fragmentLocations;
	Switch switchDeadline;
	TextView textDeadLine;
	Switch switchAllDay;
	TextView spinnerStartDay;
	TextView spinnerEndDay;
	TextView spinnerStartTime;
	TextView spinnerEndTime;
	Spinner spinnerRepetition;
	Switch switchIsMovable;
	ConstraintsFragment fragmentConstraints;
	Spinner spinnerCategory;
	Switch switchDoNotDisturb;
	Button buttonCancel;
	Button buttonSave;
	Time now = new Time();
	Time start;
	Time end;
	
	public Time getStart() {
		return start;
	}
	
	public Time getEnd() {
		return end;
	}

	public TextView getSpinnerStartDay() {
		return spinnerStartDay;
	}

	public TextView getSpinnerEndDay() {
		return spinnerEndDay;
	}

	public TextView getSpinnerStartTime() {
		return spinnerStartTime;
	}

	public TextView getSpinnerEndTime() {
		return spinnerEndTime;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editor_edit_event);
		getUiContent();
		setUiListeners();
		hideFragment(fragmentLocations);
		hideFragment(fragmentConstraints);
		getEvent();
	}
	private void getUiContent(){
		 scrollView = (ScrollView) findViewById(R.id.editor_edit_event_scroll);
		 editEventTitle = (EditText) findViewById(R.id.editor_edit_event_text_title);
		 editEventTitle.setText(getIntent().getExtras().getString("Title"));
		 editEventLocation = (EditText) findViewById(R.id.editor_edit_text_location);
		 //TODO: get the fragment reference
		 fragmentLocations= (CommonLocationFragment) getFragmentManager().findFragmentById(R.id.editor_edit_event_fragment_locations_container);
		 switchDeadline= (Switch) findViewById(R.id.editor_edit_event_switch_deadline);
		 textDeadLine= (TextView) findViewById(R.id.editor_edit_event_text_deadline);
		 // check if it is an all day event
		 switchAllDay= (Switch) findViewById(R.id.editor_edit_event_switch_allday);
		 int i = getIntent().getIntExtra("AllDayEvent", 0);
		 boolean isAllDay;
		 if (i == 1){
			 isAllDay = true;
		 } else {
			 isAllDay = false;
		 }
		 switchAllDay.setChecked(isAllDay);
		 // retrieve day and hour informations
		 now.setToNow();
		 long nowInMillis = now.toMillis(false);
		 long startTimeInMillis = getIntent().getLongExtra("StartTime", nowInMillis);
		 long endTimeInMillis = getIntent().getLongExtra("EndTime", nowInMillis);
		 start = CalendarUtils.longToTime(startTimeInMillis);
		 end = CalendarUtils.longToTime(endTimeInMillis);
		 // retrieve time views
		 spinnerStartDay= (TextView) findViewById(R.id.editor_edit_event_spinner_start_day);
		 spinnerEndDay= (TextView) findViewById(R.id.editor_edit_event_spinner_end_day);
		 spinnerStartTime= (TextView) findViewById(R.id.editor_edit_event_spinner_start_time);
		 spinnerEndTime= (TextView) findViewById(R.id.editor_edit_event_spinner_end_time);
		 
		 spinnerStartDay.setText(start.monthDay + "/" + (start.month + 1) + "/" + start.year);
		 spinnerEndDay.setText(end.monthDay + "/" + (end.month + 1) + "/" + end.year);
		 spinnerStartTime.setText(CalendarUtils.formatHour(start.hour, start.minute));
		 spinnerEndTime.setText(CalendarUtils.formatHour(end.hour, end.minute));
		 // if all day events, disable all the others
		 if (!switchAllDay.isChecked()){
			 spinnerEndDay.setVisibility(View.VISIBLE);
			 spinnerStartTime.setVisibility(View.VISIBLE);
			 spinnerEndTime.setVisibility(View.VISIBLE);
		 } else {
			 spinnerEndDay.setVisibility(View.GONE);
			 spinnerStartTime.setVisibility(View.GONE);
			 spinnerEndTime.setVisibility(View.GONE);
		 }
		 
		 spinnerRepetition= (Spinner) findViewById(R.id.editor_edit_event_spinner_repetition);
		 switchIsMovable= (Switch) findViewById(R.id.editor_edit_event_switch_ismovable);
		 fragmentConstraints= (ConstraintsFragment) getFragmentManager().findFragmentById(R.id.editor_edit_event_fragment_constraints_container);
		 spinnerCategory= (Spinner) findViewById(R.id.editor_edit_event_spinner_category);
		 switchDoNotDisturb= (Switch) findViewById(R.id.editor_edit_event_switch_donotdisturb);
		 buttonCancel= (Button) findViewById(R.id.editor_edit_event_btn_cancel);
		 buttonSave= (Button) findViewById(R.id.editor_edit_event_btn_save);
	}
	
	private void setUiListeners(){
		//TODO: Insert listeners for the ui
		
		//Passing this reference to the fragments
		fragmentLocations.setPlaceOnclick(new LocationClickListener() {
			@Override
			public void doSomething(PlaceModel placeSelected) {
				//TODO: Do something with clicked event
			}
		});
		
		
		//focusing on location will show the common location fragment
		//TODO: Change this listener, we have 2 choices: an autocompletion (with a button to add/edit locations) or make this a button and open a fragment that shows common locations.
		editEventLocation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO: Show location fragment	
				showFragment(fragmentLocations);
			}
		});
		
		// set all day events toggle
		switchAllDay.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// if all day events, disable all the others
				 if (!switchAllDay.isChecked()){
					 System.out.println("visible");
					 spinnerEndDay.setVisibility(View.VISIBLE);
					 spinnerStartTime.setVisibility(View.VISIBLE);
					 spinnerEndTime.setVisibility(View.VISIBLE);
				 } else {
					 System.out.println("invisible");
					 spinnerEndDay.setVisibility(View.GONE);
					 spinnerStartTime.setVisibility(View.GONE);
					 spinnerEndTime.setVisibility(View.GONE);
				 }
				
			}
		});
		
		// show time and date picker for date textview
		spinnerStartTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new TimeStartPickerFragment(EventEditorActivity.this);
				newFragment.show(getFragmentManager(), "Time Starting Picker");
				
			}
		});
		
		spinnerEndTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new TimeEndPickerFragment(EventEditorActivity.this);
				newFragment.show(getFragmentManager(), "Time Ending Picker");
				
			}
		});
		
		spinnerStartDay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new DayStartPickerFragment(EventEditorActivity.this);
				newFragment.show(getFragmentManager(), "Day Starting Picker");
				
			}
		});
		
		spinnerEndDay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new DayEndPickerFragment(EventEditorActivity.this);
				newFragment.show(getFragmentManager(), "Day Ending Picker");
				
			}
		});
		
		//Setting button actions
		buttonCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				EventEditorActivity.this.finish();
			}
		});
		buttonSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				saveEvent();
			}
		});
	}

	private void getEvent(){
		//TODO: Here get the event passed by the calendarView.
		if(eventToEdit==null){
		//If it is not present, this activity is used as "new event activity"
		
		//Using editor as "newEventActivity"
		Time defaultStart = new Time();
		defaultStart.setToNow();
		Time defaultEnd = new Time(defaultStart);
		if(defaultEnd.hour != 23){
			defaultEnd.hour++;
			//Have to find a clever way to set default times
		} 
		EventDescriptionModel newEvent = new EventDescriptionModel("", editEventTitle.getText().toString(), defaultStart.toMillis(false), defaultEnd.toMillis(false));
		eventToEdit = new EventInstanceModel(newEvent, defaultStart, defaultEnd);
		}
	}
	
	private void saveEvent(){
		//TODO: Complete this function
		//Here update all data on the EventDescriptionModel and EventInstanceModel
		//TODO: Update data on model (Don't change ID)
		if(eventToEdit.getEvent().getID() == ""){
			//Here we are creating a new event on Calendar, so we have to ask the CalendarFetcher to create the new event 
			//DatabaseManager.createEvent(eventToedit);
		} else {
			//Here we are updating an existing event
			//Updating data about event description
			//eventToEdit.getEvent().setUpdated();
			//Updating data about instance?
			//eventToEdit.setUpdated();
			//DatabaseManager.update(eventToEdit);
		}
	}
	
	public void setSelectedPlaceModel(PlaceModel placeSelected){
		//Hiding the fragment
		hideFragment(fragmentLocations);
		//TODO: Set the selected place
	}
	private void hideFragment(Fragment fragment){
		FragmentManager fm = getFragmentManager();
		fm.beginTransaction()
		          .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
		          .hide(fragment)
		          .commit();
	}
	private void showFragment(Fragment fragment){
		FragmentManager fm = getFragmentManager();
		fm.beginTransaction()
		          .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
		          .show(fragmentLocations)
		          .commit();
	}
}
