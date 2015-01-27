package it.unozerouno.givemetime.view.main.fragments;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.controller.fetcher.DatabaseManager;
import it.unozerouno.givemetime.model.events.EventInstanceModel;
import it.unozerouno.givemetime.model.events.EventListener;
import it.unozerouno.givemetime.utils.GiveMeLogger;
import it.unozerouno.givemetime.view.editor.EventEditorActivity;
import it.unozerouno.givemetime.view.main.SettingsActivity;
import it.unozerouno.givemetime.view.utilities.WeekView;
import it.unozerouno.givemetime.view.utilities.WeekView.EventClickListener;
import it.unozerouno.givemetime.view.utilities.WeekView.EventLongPressListener;
import it.unozerouno.givemetime.view.utilities.WeekView.MonthChangeListener;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class EventListFragment extends Fragment implements MonthChangeListener, EventClickListener, EventLongPressListener{
	
	ListView eventListView;
	ArrayList<EventInstanceModel> eventList;
	
	private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int weekViewType = TYPE_THREE_DAY_VIEW;
    private static WeekView weekView;
	
	public static final String ITEM_NAME = "item_name";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// tell the fragment that there is a new action bar menu
		setHasOptionsMenu(true);
		
		View view = inflater.inflate(R.layout.fragment_event_list, container, false);
		
		// get a reference to the week view in the layout
		weekView = (WeekView)view.findViewById(R.id.weekView);
		
		// show a toast message about the touched event
		weekView.setOnEventClickListener(this);
		
		// The week view has infinite scrolling horizontally, so we have to provide the events
		// of a month every time the month changes on the week view
		weekView.setMonthChangeListener(this);
		
		// set long press listeners for events
		weekView.setEventLongPressListener(this);
		
		// intialize event list
       eventList = new ArrayList<EventInstanceModel>();
       
        Time start = new Time();
        start.set(1, 1, 2014);
        Time end = new Time();
        end.set(31, 12, 2016);
        
        EventListener<EventInstanceModel> eventListener = new EventListener<EventInstanceModel>() {
			
			@Override
			public void onEventCreation(final EventInstanceModel newEvent) {
				//This is called from the Fetcher thread, so we had to swap to the UI thread
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
							EventListFragment.this.eventList.add(newEvent);
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
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						GiveMeLogger.log("Event Loading complete - refreshing event view");
						weekView.notifyDatasetChanged();
					}
				});			
				
			}
		};
        
		//Getting events from Db
        DatabaseManager.getInstance(getActivity()).getEventsInstances(start, end, getActivity(), eventListener);
        
  		return view;
	}
	
	public static WeekView getWeekViewInstance(){
		return weekView;
	}
	
	@Override
    public List<EventInstanceModel> onMonthChange(int newYear, int newMonth) {
		//TODO: At the time only single events are shown
		weekView.getEventRects().clear();
		return eventList;	
	}
	
	/**
	 * This function inflate a new action bar menu specific for this fragment only
	 * @param menu
	 * @param inflater
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_calendar_events, menu);
	}
	
	/**
	 * options in the action bar of this fragment
	 * @param item
	 * @return
	 */
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		int itemId = item.getItemId();
		if(itemId == R.id.action_today){
			weekView.goToToday();
			return true;
		} else if(itemId == R.id.action_day_view){
			if(weekViewType != TYPE_DAY_VIEW){
				item.setChecked(!item.isChecked());
				weekViewType = TYPE_DAY_VIEW;
				weekView.setNumberOfVisibleDays(1);
				
				// let's change some dimension to best fit the view
				weekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                weekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                weekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
			}
			return true;
		} else if(itemId == R.id.action_three_day_view){
			if(weekViewType != TYPE_THREE_DAY_VIEW){
				item.setChecked(!item.isChecked());
				weekViewType = TYPE_THREE_DAY_VIEW;
				weekView.setNumberOfVisibleDays(3);
				
				// let's change some dimension to best fit the view
				weekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                weekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                weekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
			}
			return true;
		} else if(itemId == R.id.action_week_view){
			if(weekViewType != TYPE_WEEK_VIEW){
				item.setChecked(!item.isChecked());
				weekViewType = TYPE_WEEK_VIEW;
				weekView.setNumberOfVisibleDays(7);
				
				// let's change some dimension to best fit the view
				weekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                weekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                weekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
			}
			return true;
		} else if (itemId == R.id.action_settings) {
				// create the intent
				Intent settings = new Intent(getActivity(), SettingsActivity.class);
				// launch it
				startActivity(settings);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

    @Override
    public void onEventClick(EventInstanceModel event, RectF eventRect) {
        Toast.makeText(this.getActivity(), "Clicked " + event.getEvent().getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEventLongPress(EventInstanceModel event, RectF eventRect) {
        Toast.makeText(this.getActivity(), "Long pressed event: " + event.getEvent().getName(), Toast.LENGTH_SHORT).show();
        Intent editIntent = new Intent(this.getActivity(), EventEditorActivity.class);
        // tell the activity that it is an edit call
        editIntent.putExtra("EditOrNew", "Edit"); 
        // pass to the edit the fundamental infos
        editIntent.putExtra("EventID", event.getEvent().getID());
        editIntent.putExtra("EventName", event.getEvent().getName());
        // add magic number to ensure that the event is correctly fetched
        editIntent.putExtra("StartTime", event.getEvent().getSeriesStartingDateTime().toMillis(false) - (long)10000);
        editIntent.putExtra("EndTime", event.getEvent().getSeriesEndingDateTime().toMillis(false) + (long)10000);
        
        
        editIntent.putExtra("Title", event.getEvent().getName());
        // TODO has deadline and other fields
        editIntent.putExtra("AllDayEvent", event.getEvent().isAllDayEvent());
        editIntent.putExtra("Duration", event.getEvent().getDuration());
        editIntent.putExtra("RRULE", event.getEvent().getRRULE());
        editIntent.putExtra("RDATE", event.getEvent().getRDATE());
        
        
        //TODO: Update this
        startActivityForResult(editIntent, 0);
    }
	
}
