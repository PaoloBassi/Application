package it.unozerouno.givemetime.view.intro.fragments;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.controller.fetcher.CalendarFetcher;
import it.unozerouno.givemetime.controller.fetcher.PlayServicesController;
import it.unozerouno.givemetime.utils.TaskListener;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract.Calendars;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

public class CalendarPickerFragment extends Fragment{
	ProgressBar progressBar;
	ListView calendarListView;
	ArrayList<String> calendars;
	ArrayAdapter<String> listAdapter;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_calendar_picker, container, false);
      
        //Hiding ProgressBar
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        //Getting Calendar List from UI
        calendarListView = (ListView) rootView.findViewById(R.id.list_calendar);
        calendars = new ArrayList<String>();
        listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,calendars);
        calendarListView.setAdapter(listAdapter);
        
        
        
        //TODO: SyncAdapter is currently disabled (fetching occurs while login is still in progress so result is empty. Not using SyncAdapter don't requires to specify an account)
        //Log the user in
        login();
        //fetch calendar list
        getCalendarList();
        
        return rootView;
        
        
        
    }
    
    /**
     * Start the PlayServicesController and let the user pick an account
     */
    public void login(){
    	Intent loginIntent = new Intent(this.getActivity(), PlayServicesController.class);
    	loginIntent.setAction(PlayServicesController.Actions.ACCOUNT_SELECTION);
    	startActivity(loginIntent);
    }
    
    /**
     * Update the data set containing calendar list
     * @param calendarName
     */
    public void addCalendarToList(String calendarName){
    	calendars.add(calendarName);
    }
    /**
     * Update calendar ListView through the listAdapter. Must be called from the UI thread. 
     */
    public void updateList(){
    	listAdapter.notifyDataSetChanged();
    }
 
    /**
     * Retrives calendar list calling the fetcher
     */
    public void getCalendarList(){
    	
    	//NOTICE: This is an example on how to use TaskListeners and CalendarFetcher
    	CalendarFetcher listFetcher = new CalendarFetcher(this.getActivity(), progressBar);
    	//Setting what the fetcher has to do
    	listFetcher.setAction(CalendarFetcher.Actions.LIST_CALENDARS);    	
    	//Column names expected as result - Equivalent to String[] projection = {Calendars._ID, Calendars.NAME}; 
    	String[] projection = CalendarFetcher.Projections.CALENDAR_ID_NAME_PROJ;
    	//Adding TaskListener for getting results from AsyncTask
    	listFetcher.setListener(new TaskListener<String[]>(this.getActivity()) {
			@Override
			public void onTaskResult(String[]... results) {
				// Only one row at a time is given. Columns corresponds to the ones specified in projection 
				//Adding calendar name to the ArrayList
				CalendarPickerFragment.this.addCalendarToList(results[0][1]);
				//UI update MUST be run from UI Thread
				CalendarPickerFragment.this.getActivity().runOnUiThread(new Runnable(){
			        public void run(){
			        	CalendarPickerFragment.this.updateList();
			        }
			    });
								
			}
		});
    	//Executing the fetcher with chosen projection
    	listFetcher.execute(projection);
        }
  
}
