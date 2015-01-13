package it.unozerouno.givemetime.view.main.fragments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.google.android.gms.internal.co;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.controller.fetcher.CalendarFetcher;
import it.unozerouno.givemetime.controller.fetcher.DatabaseManager;
import it.unozerouno.givemetime.model.events.EventModel;
import it.unozerouno.givemetime.utils.GiveMeLogger;
import it.unozerouno.givemetime.utils.Results;
import it.unozerouno.givemetime.utils.TaskListener;
import it.unozerouno.givemetime.view.editor.AddNewEventActivity;
import it.unozerouno.givemetime.view.main.SettingsActivity;
import it.unozerouno.givemetime.view.utilities.WeekView;
import it.unozerouno.givemetime.view.utilities.WeekView.EventClickListener;
import it.unozerouno.givemetime.view.utilities.WeekView.EventLongPressListener;
import it.unozerouno.givemetime.view.utilities.WeekView.MonthChangeListener;
import android.os.Bundle;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EventListFragment extends Fragment implements MonthChangeListener, EventClickListener, EventLongPressListener{
	
	ListView eventListView;
	ArrayList<EventModel> events;
	
	private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int weekViewType = TYPE_THREE_DAY_VIEW;
    private WeekView weekView;
	
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
        events = new ArrayList<EventModel>();
        
        Time start = new Time();
        start.set(1, 1, 1970);
        Time end = new Time();
        end.set(31, 12, 2030);
        events = (ArrayList<EventModel>) DatabaseManager.getInstance(getActivity()).getEvents(start, end, getActivity());
        //getEventList();
        
        // TODO here size of list is 0, whyyyyyyyyyy?
        GiveMeLogger.log("Size at beginning: " + events.size());
		
		return view;
	}
	
	@Override
    public List<EventModel> onMonthChange(int newYear, int newMonth) {
		// clear the list of events displayed, in order to avoid redundancy 
		weekView.getEventRects().clear();
		return events;	
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
	
	
    /**
     * Retrieves event list calling the fetcher
     */
    public void getEventList(){
    	
    	//EXAMPLE: This is an example on how to use TaskListeners and CalendarFetcher in "Internal query mode" 
    	CalendarFetcher listFetcher = new CalendarFetcher(this.getActivity());
    	//Setting what the fetcher has to do (Fetch calendar list and build the model)
    	listFetcher.setAction(CalendarFetcher.Actions.LIST_OF_EVENTS);    	
    	//Adding TaskListener for getting results from AsyncTask
    	listFetcher.setListener(new TaskListener<String[]>(this.getActivity()) {
			@Override
			public void onTaskResult(String[]... results) {
				if (results[0] == Results.RESULT_OK){
					events.addAll(CalendarFetcher.getEventList());
				}				
			}
		});
    	//Executing the fetcher
    	listFetcher.execute();
        }
 

    private String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onEventClick(EventModel event, RectF eventRect) {
        Toast.makeText(this.getActivity(), "Clicked " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEventLongPress(EventModel event, RectF eventRect) {
        Toast.makeText(this.getActivity(), "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();
    }
	
}
