package it.unozerouno.givemetime.view.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.ical.values.RRule;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.controller.fetcher.DatabaseManager;
import it.unozerouno.givemetime.model.UserKeyRing;
import it.unozerouno.givemetime.model.events.EventCategory;
import it.unozerouno.givemetime.model.events.EventDescriptionModel;
import it.unozerouno.givemetime.model.events.EventInstanceModel;
import it.unozerouno.givemetime.model.events.EventListener;
import it.unozerouno.givemetime.model.places.PlaceModel;
import it.unozerouno.givemetime.utils.CalendarUtils;
import it.unozerouno.givemetime.utils.GiveMeLogger;
import it.unozerouno.givemetime.view.editor.LocationEditorFragment.OnSelectedPlaceModelListener;
import it.unozerouno.givemetime.view.main.fragments.EventListFragment;
import it.unozerouno.givemetime.view.utilities.DayEndPickerFragment;
import it.unozerouno.givemetime.view.utilities.DayStartPickerFragment;
import it.unozerouno.givemetime.view.utilities.TimeEndPickerFragment;
import it.unozerouno.givemetime.view.utilities.TimeStartPickerFragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

public class EventEditorActivity extends ActionBarActivity implements OnSelectedPlaceModelListener{
	
	private String editOrNew;
	private ScrollView scrollView;
	private EditText editEventTitle;
	private TextView textLocation;
	private Button buttonLocation;
	private LocationEditorFragment fragmentLocations;
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
	private Time start;
	private Time end;
	private String categoryName;
	private List<String> items; 
	private EventCategory selectedCategory;
	private EventDescriptionModel eventToEdit;
	private EventInstanceModel eventToAdd;
	private String eventID;
	private String eventName;
	private PlaceModel selectedPlaceModel;
	private Toolbar toolbar;
	private EventListener<EventInstanceModel> eventListener;
	
	public void setStart(Time start) {
		this.start = start;
	}
	
	public void setEnd(Time end) {
		this.end = end;
	}
	
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
		// set the title of the activity
		editOrNew = getIntent().getStringExtra("EditOrNew");
		if (editOrNew.equals("New")){
			this.setTitle("New Event");
		} else {
			this.setTitle("Edit Event");
			// load all the events infos from provider
			final String ID = getIntent().getStringExtra("EventID");
			long startTimeinMillis = getIntent().getLongExtra("StartTime", 0);
			long endTimeinMillis = getIntent().getLongExtra("EndTime", 0);
			final Time startTime = new Time();
			startTime.set(startTimeinMillis);
			final Time endTime = new Time();
			endTime.set(endTimeinMillis);
			
	        eventListener = new EventListener<EventInstanceModel>() {
				@Override
				public void onEventCreation(final EventInstanceModel newEvent) {
					
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							
							// when found, assign the corresponding event
							eventToEdit = newEvent.getEvent();
							// retrieve all data in order to display them on screen
							
							// set the start and end variables to startTime and EndTime for update 
							start = startTime;
							end = endTime;
							
							editEventTitle.setText(eventToEdit.getName());
							if (eventToEdit.getPlace() == null){
								textLocation.setText("Location not set");
							} else {
								textLocation.setText(eventToEdit.getPlace().getName());
							}
							if (eventToEdit.getCategory() == null) {
								// loading an external application generated event
								// set amusement, better than work
								spinnerCategory.setSelection(2);
							} else {
								spinnerCategory.setSelection(items.indexOf(eventToEdit.getCategory().getName()));
							}
							System.out.println("deadline just before setting it in update fetch : " + eventToEdit.getHasDeadline());
							switchDeadline.setChecked(eventToEdit.getHasDeadline());
							System.out.println("deadline on click: " + switchDeadline.isChecked());
							// check if the event has repetitions
							if (eventToEdit.isRecursive()){
								//TODO set spinner repetition with correct text
							} else {
								spinnerRepetition.setSelection(0); // it the no repetition choice
							}
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
							setSpinnerData(eventToEdit.getSeriesStartingDateTime(), eventToEdit.getSeriesEndingDateTime());
							
							switchIsMovable.setChecked(eventToEdit.getIsMovable());
							switchDoNotDisturb.setChecked(eventToEdit.getDoNotDisturb());
							
						}
					});
				}
				
				@Override
				public void onEventChange(EventInstanceModel newEvent) {
					// TODO: It's very unlikely that the event changes while watched, but this is the place for updates.
				}

				@Override
				public void onLoadCompleted() {
					//This is called from the Fetcher thread, so we had to swap to the UI thread		
				}
			};
			// fetch the event instances searching for the edit event
			System.out.println("start: " + startTime.hour + " " + startTime.minute + " " + startTime.second);
			System.out.println("end: " + endTime.hour + " " + endTime.minute + " " + endTime.second);
			DatabaseManager.getEventsInstances(Integer.parseInt(ID), startTime, endTime, this, eventListener);
		}
		getUiContent();
		setUiListeners();
		hideFragment(fragmentLocations);
		hideFragment(fragmentConstraints);

		getEventInfo();
	}
	
	
	private void getUiContent(){
		
		// set the toolbar 
        toolbar = (Toolbar) findViewById(R.id.toolbar_edit_event);
        if (toolbar != null){
        	// set the toolbar as the action bar
        	setSupportActionBar(toolbar);
        	getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        	getSupportActionBar().setHomeButtonEnabled(true);
        }
		
		 scrollView = (ScrollView) findViewById(R.id.editor_edit_event_scroll);
		 editEventTitle = (EditText) findViewById(R.id.editor_edit_event_text_title);
		 
		 textLocation = (TextView) findViewById(R.id.editor_text_location);
		 //get the fragment reference
		 fragmentLocations = (LocationEditorFragment) getSupportFragmentManager().findFragmentById(R.id.editor_edit_event_fragment_locations_container);
		 buttonLocation = (Button) findViewById(R.id.editor_button_location);
		 
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
		 spinnerRepetition = (Spinner) findViewById(R.id.recursive_spinner);
		 
		 switchAllDay = (Switch) findViewById(R.id.editor_edit_event_switch_allday);
		 switchIsMovable = (Switch) findViewById(R.id.editor_edit_event_switch_ismovable);
		 switchDoNotDisturb = (Switch) findViewById(R.id.editor_edit_event_switch_donotdisturb);
		 fragmentConstraints = (ConstraintsFragment) getSupportFragmentManager().findFragmentById(R.id.editor_edit_event_fragment_constraints_container);
		 
		 ArrayAdapter<CharSequence> spinnerAdapterRepetition = ArrayAdapter.createFromResource(getBaseContext(), R.array.Repetitions, android.R.layout.simple_spinner_item);
		 spinnerAdapterRepetition.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		 spinnerRepetition.setAdapter(spinnerAdapterRepetition);
		 
		 // set the category spinner
		 spinnerCategory = (Spinner) findViewById(R.id.category_spinner);
		 // retrieve the name of all categories
		 items = new ArrayList<String>();
		 for (EventCategory category : DatabaseManager.getCategories()) {
			items.add(category.getName());
		 }
		 Collections.reverse(items);
		 // at the end, add the "Add category" option
		 items.add("Add Category");
		 ArrayAdapter<String> spinnerAdapterCategory = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
		 spinnerAdapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		 spinnerCategory.setAdapter(spinnerAdapterCategory);
		 // set the first item in the list as first selection
		 spinnerCategory.setSelection(0);
		 categoryName = (String) spinnerCategory.getItemAtPosition(0);
		 switchDoNotDisturb.setChecked(DatabaseManager.getCategoryByName(categoryName).isDefault_donotdisturb());
		 switchIsMovable.setChecked(DatabaseManager.getCategoryByName(categoryName).isDefault_movable());
		 
		 buttonCancel = (Button) findViewById(R.id.editor_edit_event_btn_cancel);
		 buttonSave = (Button) findViewById(R.id.editor_edit_event_btn_save);
	}
	
	private void setUiListeners(){
		//TODO: Insert listeners for the ui
		
		
		
		//Setting Location Button onClick
		buttonLocation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (fragmentLocations.isHidden()) {
					showFragment(fragmentLocations);
				} else {
					hideFragment(fragmentLocations);
				}
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
					 eventToEdit.setAllDay(0);
				 } else {
					 System.out.println("invisible");
					 setSpinnerVisibility(View.GONE);
					 eventToEdit.setAllDay(1);
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
		
		// set the spinner listener
		spinnerCategory.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// if the position is not the last, save the name of the category and load the switch values associated
				if (position != (items.size() - 1)){
					categoryName = items.get(position);
					boolean doNotDisturb = DatabaseManager.getCategoryByName(categoryName).isDefault_donotdisturb();
					boolean isMovable = DatabaseManager.getCategoryByName(categoryName).isDefault_movable();
					switchDoNotDisturb.setChecked(doNotDisturb);
					switchIsMovable.setChecked(isMovable);
					eventToEdit.setCategory(DatabaseManager.getCategoryByName(categoryName));
					eventToEdit.setDoNotDisturb(doNotDisturb);
					eventToEdit.setIsMovable(isMovable);
				} else {
					// TODO creation of new category
					spinnerCategory.setSelection(0);
					new AlertDialog.Builder(EventEditorActivity.this)
						.setTitle("Option Not Available")
						.setMessage("This option is not available in the free version. Purchase the pro version in order to create you own personalized categories!")
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
								
							}
						}).show();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
		switchDeadline.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(switchDeadline.isChecked()) {
					// TODO disable other options
					textDeadLine.setText("This event has an active deadline");
					spinnerRepetition.setSelection(0);
					setSpinnerVisibility(2);
					switchIsMovable.setChecked(false);
					eventToEdit.setHasDeadline(true);
				} else {
					textDeadLine.setText("This event has no deadline");
					setSpinnerVisibility(0);
					eventToEdit.setHasDeadline(false);
				}
				
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
		
		//Setting isMovable switch behaviour
			switchIsMovable.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// if all day events, disable all the others
					 if (!switchIsMovable.isChecked()){
						hideFragment(fragmentConstraints);
						eventToEdit.setIsMovable(false);
					 } else {
						 showFragment(fragmentConstraints);
						 eventToEdit.setIsMovable(true);
					 }
					
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
			editEventTitle.setText("New Event");
			start = new Time();
			start.setToNow();
			CalendarUtils.approximateMinutes(start);
			end = new Time(start);
			if(end.hour != 23){
				end.hour++;
				//Have to find a clever way to set default times
			} else {
				end.hour = 0;
			}
			// create a description model in order to load the constraint fragment
			eventToEdit = new EventDescriptionModel("", editEventTitle.getText().toString());
			// set data inside the spinner
			setSpinnerData(start, end);
			// get the constraint list associated to the event
			fragmentConstraints.setConstraintList(eventToEdit.getConstraints());
			
		} else {
			// Event Edit with listener in onCreate
		}
	}
	
	private void saveEvent(){
		//TODO: Complete this function
		//Here update all data on the EventDescriptionModel and EventInstanceModel
		if(editOrNew.equals("New")){
			// set the (probably) new title, start and ending time of the event
			eventToEdit.setName(editEventTitle.getText().toString());
			eventToEdit.setSeriesStartingDateTime(start);
			eventToEdit.setSeriesEndingDateTime(end);
			
			eventToEdit.setCalendarId(UserKeyRing.getCalendarId(this));
			// retrieve the name of the category selected and the default data of the switch associated
			selectedCategory = DatabaseManager.getCategoryByName(categoryName);
			// check if it is a default category
			if (selectedCategory.isDefaultCategory()){
				eventToEdit.setCategory(selectedCategory);
				switchDoNotDisturb.setChecked(selectedCategory.isDefault_donotdisturb());
				switchIsMovable.setChecked(selectedCategory.isDefault_movable());
			} else {
				// TODO do other things with non default categories
			}
			// TODO: set all other data that we have
			eventToEdit.setHasDeadline(switchDeadline.isChecked());
			System.out.println("first deadline set to : " + eventToEdit.getHasDeadline());
			// set the constraint List inside the event
			eventToEdit.setConstraints(fragmentConstraints.getConstraintList());
			// create the relative instance of the Event
			eventToAdd = new EventInstanceModel(eventToEdit, start, end);
			// if the event is recursive, set the duration
			// set the RRULE to let googleCalendar display the view
			eventToAdd.getEvent().setRRULE(spinnerRepetition.getSelectedItem(), start, end);
			// duration is an empty string
			eventToAdd.setStartingTime();
			
			// finally add the event to the db 
			DatabaseManager.addEvent(this, eventToAdd);
			System.out.println("deadline after insert set to : " + eventToAdd.getEvent().getHasDeadline());
		} else {
			//Here we are updating an existing event
			// start and end are correctly set during the fetch, line 153-154
			DatabaseManager.updateEvent(EventEditorActivity.this, new EventInstanceModel(eventToEdit, start, end));
			System.out.println("deadline set to : " + eventToEdit.getHasDeadline());
			
		}
		EventListFragment.getWeekViewInstance().notifyDatasetChanged();
	}
	
	private void hideFragment(Fragment fragment){
		FragmentManager fm = getSupportFragmentManager();
		fm.beginTransaction()
		          //.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
		          .hide(fragment)
		          .commit();
	}
	private void showFragment(Fragment fragment){
		FragmentManager fm = getSupportFragmentManager();
		fm.beginTransaction()
		          //.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
		          .show(fragment)
		          .commit();
	}
	
	/**
	 * 0 set visibility of the spinners. 0 visibile 1 invisibile 2 gone
	 * @param visibility
	 */
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

	@Override
	public void onSelectedPlaceModel(PlaceModel place) {
		textLocation.setText(place.getName());
		hideFragment(fragmentLocations);
		buttonLocation.setText("Edit");
		selectedPlaceModel = place;
		// attach the selected place model to the event to add or edit in the UI
		eventToEdit.setPlace(selectedPlaceModel);
	}
	
	
}
