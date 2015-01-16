package it.unozerouno.givemetime.view.editor;

import com.google.android.gms.games.event.Events.LoadEventsResult;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.model.events.EventDescriptionModel;
import it.unozerouno.givemetime.model.events.EventInstanceModel;
import it.unozerouno.givemetime.model.events.EventModel;
import it.unozerouno.givemetime.model.places.PlaceModel;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
	Spinner spinnerStartDay;
	Spinner spinnerEndDay;
	Spinner spinnerStartTime;
	Spinner spinnerEndTime;
	Spinner spinnerRepetition;
	Switch switchIsMovable;
	ConstraintsFragment fragmentConstraints;
	Spinner spinnerCategory;
	Switch switchDoNotDisturb;
	Button buttonCancel;
	Button buttonSave;
	
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
		 editEventLocation = (EditText) findViewById(R.id.editor_edit_text_location);
		 //TODO: get the fragment reference
		 fragmentLocations= (CommonLocationFragment) getFragmentManager().findFragmentById(R.id.editor_edit_event_fragment_locations_container);
		 switchDeadline= (Switch) findViewById(R.id.editor_edit_event_switch_deadline);
		 textDeadLine= (TextView) findViewById(R.id.editor_edit_event_text_deadline);
		 switchAllDay= (Switch) findViewById(R.id.editor_edit_event_switch_allday);
		 spinnerStartDay= (Spinner) findViewById(R.id.editor_edit_event_spinner_start_day);
		 spinnerEndDay= (Spinner) findViewById(R.id.editor_edit_event_spinner_end_day);
		 spinnerStartTime= (Spinner) findViewById(R.id.editor_edit_event_spinner_start_time);
		 spinnerEndTime= (Spinner) findViewById(R.id.editor_edit_event_spinner_end_time);
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
		
		//Passing this reference to the frgments
		fragmentLocations.setCaller(this);
		
		
		//focusing on location will show the common location fragment
		//TODO: Change this listener, we have 2 choices: an autocompletion (with a button to add/edit locations) or make this a button and open a fragment that shows common locations.
		editEventLocation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO: Show location fragment	
				showFragment(fragmentLocations);
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
