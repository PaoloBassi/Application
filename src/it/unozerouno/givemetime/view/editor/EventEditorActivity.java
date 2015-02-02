package it.unozerouno.givemetime.view.editor;

import java.text.BreakIterator;
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
import it.unozerouno.givemetime.utils.Dialog;
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
import android.view.KeyEvent;
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
import android.widget.TextView.OnEditorActionListener;

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
	private String categoryName;
	private List<String> items; 
	private EventCategory selectedCategory;
	private EventInstanceModel eventToEdit;
	private Toolbar toolbar;
	private EventListener<EventInstanceModel> eventListener;
	
	public void setStart(Time start) {
		eventToEdit.setStartingTime(start);
	}
	
	public void setEnd(Time end) {
		eventToEdit.setEndingTime(end);
	}
	
	public Time getStart() {
		return eventToEdit.getStartingTime();
	}
	
	public Time getEnd() {
		 return eventToEdit.getEndingTime();
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
		//Creating a new Dummy event
		eventToEdit = new EventInstanceModel(new EventDescriptionModel(null, null), null, null);
		// set the title of the activity
		editOrNew = getIntent().getStringExtra("EditOrNew");
		
		//Interface Init
		getUiContent();
		setUiListeners();
		hideFragment(fragmentLocations);
		hideFragment(fragmentConstraints);
		//Setting default data
		setDefaults();
		
		//Here we prepare to get the event
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
	        	EventInstanceModel fetchedEvent;
				@Override
				public void onEventCreation(final EventInstanceModel newEvent) {
					fetchedEvent = newEvent;
				}
				
				@Override
				public void onEventChange(EventInstanceModel newEvent) {
					// TODO: It's very unlikely that the event changes while watched, but this is the place for updates.
				}

				@Override
				public void onLoadCompleted() {
					
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							loadEvent(fetchedEvent);						
						}
					});		
				}
			};
			
			// fetch the event instances searching for the edit event
			DatabaseManager.getEventsInstances(Integer.parseInt(ID), startTime, endTime, this, eventListener);
		}
	

	}

	/**
	 * This function loads an event received from the DatabaseManager callback into the event to edit and fills the UI accordingly
	 * @param eventToLoad
	 */
	private synchronized void loadEvent(EventInstanceModel eventToLoad){
		if(eventToLoad == null){
			GiveMeLogger.log("FATAL ERROR: Editor launched with a non-existing id");
		}
		// when found, assign the corresponding event
		eventToEdit = eventToLoad;
		// retrieve all data in order to display them on screen
		
		
		//Setting the UI
		//Setting title 
		editEventTitle.setText(eventToEdit.getEvent().getName());
		//Setting Location
		if (eventToEdit.getEvent().getPlace() == null){
			textLocation.setText("Location not set");
		} else {
			textLocation.setText(eventToEdit.getEvent().getPlace().getName());
		}
		//Setting Category
		if (eventToEdit.getEvent().getCategory() == null) {
			// loading an external application generated event
			// set amusement, better than work
			spinnerCategory.setSelection(2);
		} else {
			spinnerCategory.setSelection(items.indexOf(eventToEdit.getEvent().getCategory().getName()));
			
		}
		
		//Setting deadLine
		// the deadline is always true, payment required
					switchDeadline.setChecked(true); 

		// check if the event has repetitions
		if (eventToEdit.getEvent().isRecursive()){
			//TODO set spinner repetition with correct text
		} else {
			spinnerRepetition.setSelection(0); // it the no repetition choice
		}
		
		// check if it is an all day event
		//TODO: implement this using allDay coloumn from CalendarFetcher (check even if it's present on EventModel)
		int allDayInt = getIntent().getIntExtra("AllDayEvent", 0);
		boolean isAllDay = (allDayInt == 1);
		switchAllDay.setChecked(isAllDay);
		
			// if all day events, disable all the others
			if (!switchAllDay.isChecked()){
				setSpinnerVisibility(View.VISIBLE);
			} else {
				setSpinnerVisibility(View.GONE);
			}
		setSpinnerData(eventToEdit.getEvent().getSeriesStartingDateTime(), eventToEdit.getEvent().getSeriesEndingDateTime());
		
		//Setting isMovable
		switchIsMovable.setChecked(eventToEdit.getEvent().getIsMovable());
		switchDoNotDisturb.setChecked(eventToEdit.getEvent().getDoNotDisturb());
		//Setting constraints
		if (eventToEdit.getEvent().getConstraints() != null) {
			switchIsMovable.setEnabled(true);
			fragmentConstraints.setConstraintList(eventToEdit.getEvent().getConstraints());
		}
		
	}
	
	/**
	 * Gets all UI components' references
	 */
	private synchronized void getUiContent(){
		// set the toolbar 
        toolbar = (Toolbar) findViewById(R.id.toolbar_edit_event);
        if (toolbar != null){
        	// set the toolbar as the action bar
        	setSupportActionBar(toolbar);    	
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
	
	
	private synchronized void setUiListeners(){
		
		//Setting Location Button onClick
		buttonLocation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (fragmentLocations.isHidden()) {
					showFragment(fragmentLocations);
					buttonLocation.setText("Hide");
				} else {
					hideFragment(fragmentLocations);
					buttonLocation.setText("Show");
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
					 eventToEdit.getEvent().setAllDay(0);
				 } else {
					 System.out.println("invisible");
					 setSpinnerVisibility(View.GONE);
					 eventToEdit.getEvent().setAllDay(1);
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
					eventToEdit.getEvent().setCategory(DatabaseManager.getCategoryByName(categoryName));
					eventToEdit.getEvent().setDoNotDisturb(doNotDisturb);
					eventToEdit.getEvent().setIsMovable(isMovable);
				} else {
					// TODO creation of new category
					spinnerCategory.setSelection(0);
					Dialog.genericAlertDialog(EventEditorActivity.this, R.string.Not_available, R.string.pay_categories);
			}
			}
			public void onNothingSelected(android.widget.AdapterView<?> arg0) {};
				
		});
		switchDeadline.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					textDeadLine.setText(getString(R.string.editor_hasdeadline_text));
					eventToEdit.getEvent().setHasDeadline(true);
				} else {
					Dialog.genericAlertDialog(EventEditorActivity.this, R.string.Not_available, R.string.pay_deadline);
					switchDeadline.setChecked(true);
//					textDeadLine.setText("This event has no deadline");
//					setSpinnerVisibility(0);
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
					 if (!isChecked){
						hideFragment(fragmentConstraints);
						eventToEdit.getEvent().setIsMovable(false);
					 } else {
						 showFragment(fragmentConstraints);
						 eventToEdit.getEvent().setIsMovable(true);
					 }
					
				}
			});
			
			//Setting isMovable switch behaviour
			switchDoNotDisturb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						eventToEdit.getEvent().setDoNotDisturb(isChecked);
				}
			});
			
			editEventTitle.setOnEditorActionListener(new OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					eventToEdit.getEvent().setName(v.getText().toString());
					return true;
				}
			});
			
			
	}

	/**
	 * Sets all ui and model default data
	 */
	private synchronized void setDefaults(){
		//Default hasDeadline
		switchDeadline.setEnabled(true);
		eventToEdit.getEvent().setHasDeadline(true);
		//Default Category
			// set amusement, better than work
		int defaultPosition = 2;
		spinnerCategory.setSelection(defaultPosition);
		eventToEdit.getEvent().setCategory(DatabaseManager.getCategoryByName(items.get(defaultPosition)));
		textLocation.setText("Location not set");
		eventToEdit.getEvent().setPlace(null);
		//Setting deadLine
		// the deadline is always true, payment required
		switchDeadline.setChecked(true);
		eventToEdit.getEvent().setHasDeadline(true);
		//Setting repetition default
		spinnerRepetition.setSelection(0); // it the no repetition choice
		switchAllDay.setChecked(false);
		eventToEdit.getEvent().setAllDay(0);
		editEventTitle.setText("New Event");
		eventToEdit.getEvent().setName(editEventTitle.getText().toString());
		
			Time start = new Time();
			start.setToNow();
			CalendarUtils.approximateMinutes(start);
			Time end = new Time(start);
			if(end.hour != 23){
				end.hour++;
				//Have to find a clever way to set default times
			} else {
				end.hour = 0;
			}
			// setting default data to event
			eventToEdit.setStartingTime(start);
			eventToEdit.setEndingTime(end);
			// set data inside the spinner
			setSpinnerData(start, end);
			// get the constraint list associated to the event
			fragmentConstraints.setConstraintList(eventToEdit.getEvent().getConstraints());
	}
	
	private void saveEvent(){
		//Here update all data on the EventDescriptionModel and EventInstanceModel
		eventToEdit.getEvent().setName(editEventTitle.getText().toString());
		eventToEdit.getEvent().setCalendarId(UserKeyRing.getCalendarId(this));
		// retrieve the name of the category selected and the default data of the switch associated

		// set the constraint List inside the event
		eventToEdit.getEvent().setConstraints(fragmentConstraints.getConstraintList());

		// if the event is recursive, set the duration
		// set the RRULE to let googleCalendar display the view
		if (spinnerRepetition.getSelectedItemPosition() != 0){
			//Event is recursive
			//TODO: fix starting dateTime of Event
		eventToEdit.getEvent().setRRULE(spinnerRepetition.getSelectedItem(), eventToEdit.getStartingTime(), eventToEdit.getEndingTime());
		// duration is an empty string
		eventToEdit.computeDuration();
		}
		
		if(editOrNew.equals("New")){
			DatabaseManager.addEvent(this, eventToEdit);
		
		} else {
			DatabaseManager.updateEvent(this, eventToEdit);
		}
		// finally add the event to the db 
		
	//	EventListFragment.getWeekViewInstance().notifyDatasetChanged();
	}
	
	private void hideFragment(Fragment fragment){
		FragmentManager fm = getSupportFragmentManager();
		fm.beginTransaction()
		          .setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out)
		          .hide(fragment)
		          .commit();
	}
	private void showFragment(Fragment fragment){
		FragmentManager fm = getSupportFragmentManager();
		fm.beginTransaction()
		          .setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out)
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
		// attach the selected place model to the event to add or edit in the UI
		eventToEdit.getEvent().setPlace(place);
	}
	
	
}
