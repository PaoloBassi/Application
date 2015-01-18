package it.unozerouno.givemetime.view.editor;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.controller.fetcher.DatabaseManager;
import it.unozerouno.givemetime.model.events.EventDescriptionModel;
import it.unozerouno.givemetime.model.events.EventInstanceModel;
import it.unozerouno.givemetime.model.places.PlaceModel;
import it.unozerouno.givemetime.utils.CalendarUtils;
import it.unozerouno.givemetime.view.utilities.DayEndPickerFragment;
import it.unozerouno.givemetime.view.utilities.DayStartPickerFragment;
import it.unozerouno.givemetime.view.utilities.TimeEndPickerFragment;
import it.unozerouno.givemetime.view.utilities.TimeStartPickerFragment;
import it.unozerouno.givemetime.view.utilities.WeekView;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

public class EventEditorActivity extends Activity{
	
	private String editOrNew;
	private EventInstanceModel eventToAdd;
	private ScrollView scrollView;
	private EditText editEventTitle;
	private EditText editEventLocation;
	private CommonLocationFragment fragmentLocations;
	private Switch switchDeadline;
	private TextView textDeadLine;
	private Switch switchAllDay;
	private TextView spinnerStartDay;
	private TextView spinnerEndDay;
	private TextView spinnerStartTime;
	private TextView spinnerEndTime;
	private TextView endDayTextView;
	private TextView startHourTextView;
	private TextView endHourTextView;
	private View middleBar;
	private Spinner spinnerRepetition;
	private Switch switchIsMovable;
	private ConstraintsFragment fragmentConstraints;
	private Spinner spinnerCategory;
	private Switch switchDoNotDisturb;
	private Button buttonCancel;
	private Button buttonSave;
	private Time now = new Time();
	private Time start;
	private Time end;
	
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
		// set the title
		editOrNew = getIntent().getStringExtra("EditOrNew");
		if (editOrNew.equals("New")){
			this.setTitle("New Event");
		} else {
			this.setTitle("Edit Event");
		}
		getUiContent();
		setUiListeners();
		hideFragment(fragmentLocations);
		hideFragment(fragmentConstraints);
		getEventInfo();
	}
	private void getUiContent(){
		
		 scrollView = (ScrollView) findViewById(R.id.editor_edit_event_scroll);
		 editEventTitle = (EditText) findViewById(R.id.editor_edit_event_text_title);
		 
		 editEventLocation = (EditText) findViewById(R.id.editor_edit_text_location);
		 //TODO: get the fragment reference
		 fragmentLocations = (CommonLocationFragment) getFragmentManager().findFragmentById(R.id.editor_edit_event_fragment_locations_container);
		 switchDeadline = (Switch) findViewById(R.id.editor_edit_event_switch_deadline);
		 textDeadLine = (TextView) findViewById(R.id.editor_edit_event_text_deadline);
		 
		 // retrieve time views
		 spinnerStartDay = (TextView) findViewById(R.id.editor_edit_event_spinner_start_day);
		 spinnerEndDay = (TextView) findViewById(R.id.editor_edit_event_spinner_end_day);
		 spinnerStartTime = (TextView) findViewById(R.id.editor_edit_event_spinner_start_time);
		 spinnerEndTime = (TextView) findViewById(R.id.editor_edit_event_spinner_end_time);
		 endDayTextView = (TextView) findViewById(R.id.end_day_textview);
		 startHourTextView = (TextView) findViewById(R.id.start_hour_textview);
		 endHourTextView = (TextView) findViewById(R.id.end_hour_textview);
		 middleBar = (View) findViewById(R.id.bottom_day_top_hour_bar);
		 
		 switchAllDay = (Switch) findViewById(R.id.editor_edit_event_switch_allday);
		 // TODO view for repeating events
		 switchIsMovable = (Switch) findViewById(R.id.editor_edit_event_switch_ismovable);
		 fragmentConstraints = (ConstraintsFragment) getFragmentManager().findFragmentById(R.id.editor_edit_event_fragment_constraints_container);
		 spinnerCategory = (Spinner) findViewById(R.id.editor_edit_event_spinner_category);
		 switchDoNotDisturb = (Switch) findViewById(R.id.editor_edit_event_switch_donotdisturb);
		 buttonCancel = (Button) findViewById(R.id.editor_edit_event_btn_cancel);
		 buttonSave = (Button) findViewById(R.id.editor_edit_event_btn_save);
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
					 setSpinnerVisibility(View.VISIBLE);
				 } else {
					 System.out.println("invisible");
					 setSpinnerVisibility(View.GONE);
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
				setResult(RESULT_CANCELED);
				EventEditorActivity.this.finish();
			}
		});
		buttonSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				saveEvent();
				setResult(RESULT_OK);
				EventEditorActivity.this.finish();
			}
		});
	}

	/**
	 * Acquires informations about creating or editing an event, retrieve and display information consequently
	 */
	private void getEventInfo(){
		//TODO: Here get the event passed by the calendarView.
		if(editOrNew.equals("New")){
			//If it is not present, this activity is used as "new event activity"
			//Using editor as "newEventActivity"
			editEventTitle.setText("New Event");
			start = new Time();
			start.setToNow();
			end = new Time(start);
			if(end.hour != 23){
				end.hour++;
				//Have to find a clever way to set default times
			} else {
				end.hour = 0;
			}
			// set data inside the spinner
			setSpinnerData(start, end);
			
		} else {
			// if this activity is called for editing an event
			editEventTitle.setText(getIntent().getStringExtra("Title"));
			// check if it is an all day event
			int i = getIntent().getIntExtra("AllDayEvent", 0);
			boolean isAllDay;
			if (i == 1){
				isAllDay = true;
			} else {
				isAllDay = false;
			}
			switchAllDay.setChecked(isAllDay);
			
			// if all day events, disable all the others
			if (!switchAllDay.isChecked()){
				setSpinnerVisibility(View.VISIBLE);
			} else {
				setSpinnerVisibility(View.GONE);
			}
			
			// retrieve day and hour informations
			now.setToNow();
			long nowInMillis = now.toMillis(false);
			long startTimeInMillis = getIntent().getLongExtra("StartTime", nowInMillis);
			long endTimeInMillis = getIntent().getLongExtra("EndTime", nowInMillis);
			start = CalendarUtils.longToTime(startTimeInMillis);
			end = CalendarUtils.longToTime(endTimeInMillis);
			setSpinnerData(start, end);
		}
	}
	
	private void saveEvent(){
		//TODO: Complete this function
		//Here update all data on the EventDescriptionModel and EventInstanceModel
		if(editOrNew.equals("New")){
			// here goes the event creation model
			EventDescriptionModel newEvent = new EventDescriptionModel("", editEventTitle.getText().toString(), start.toMillis(false), end.toMillis(false));
			// TODO: set all other data that we have
			// create the relative instance of the Event
			eventToAdd = new EventInstanceModel(newEvent, start, end);
			// if the event is recursive, set the duration
			eventToAdd.setStartingTime();
			//TODO: Here we are creating a new event on Calendar, so we have to ask the CalendarFetcher to create the new event 
			DatabaseManager.addEvent(this, eventToAdd);
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
	
	private void setSpinnerVisibility(int visibility){
		spinnerEndDay.setVisibility(visibility);
		spinnerStartTime.setVisibility(visibility);
		spinnerEndTime.setVisibility(visibility);
		endDayTextView.setVisibility(visibility);
		startHourTextView.setVisibility(visibility);
		endHourTextView.setVisibility(visibility);
		middleBar.setVisibility(visibility);
	}
	
	private void setSpinnerData(Time start, Time end){
		spinnerStartDay.setText(start.monthDay + "/" + (start.month + 1) + "/" + start.year);
		spinnerEndDay.setText(end.monthDay + "/" + (end.month + 1) + "/" + end.year);
		spinnerStartTime.setText(CalendarUtils.formatHour(start.hour, start.minute));
		spinnerEndTime.setText(CalendarUtils.formatHour(end.hour, end.minute));
	}
	
	
}
