package it.unozerouno.givemetime.view.intro.fragments;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.controller.fetcher.CalendarFetcher;
import it.unozerouno.givemetime.controller.fetcher.DatabaseManager;
import it.unozerouno.givemetime.model.CalendarModel;
import it.unozerouno.givemetime.model.UserKeyRing;
import it.unozerouno.givemetime.utils.Results;
import it.unozerouno.givemetime.utils.TaskListener;
import it.unozerouno.givemetime.view.utilities.ApiLoginInterface;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CalendarPickerFragment extends Fragment {
	ProgressBar progressBar;
	ListView calendarListView;
	ArrayList<CalendarModel> calendars;
	CalendarListAdapter listAdapter;
	CalendarFetcher listFetcher;
	Button newCalendarButton, confirmCalendarButton;
	SQLiteDatabase db;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_calendar_picker, container, false);
      
        //Hiding ProgressBar
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        //Getting Calendar List from UI
        calendarListView = (ListView) rootView.findViewById(R.id.list_calendar);
        calendars = new ArrayList<CalendarModel>();
        listAdapter = new CalendarListAdapter(getActivity(), R.layout.element_list_calendar, calendars);
        calendarListView.setAdapter(listAdapter);
        //Setting onClick for calendars
        calendarListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				// reset color of all elements
				for (int i = 0; i < adapter.getChildCount(); i++) {
					adapter.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
				}
				// change the background color of the selected element
				view.setBackgroundColor(Color.LTGRAY);
				// save the element selected
				CalendarModel calendarSelected = (CalendarModel) adapter.getItemAtPosition(position);
				CalendarPickerFragment.this.calendarListView.setSelection(position);
				//Saving selected calendar ID
				UserKeyRing.setCalendarId(getActivity(), calendarSelected.getId());
				UserKeyRing.setCalendarName(getActivity(), calendarSelected.getName());
			}
        	
		});
        
            
        
        
        //Log the user in
        login();
        
        //Setting new calendar button onClick
        newCalendarButton = (Button) rootView.findViewById(R.id.calendar_list_new_btn);
        newCalendarButton.setEnabled(false);
        newCalendarButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				createNewCalendar();
			}
		});
        
        // set the button confirming the selection of the db
        confirmCalendarButton = (Button) rootView.findViewById(R.id.continueButton);
        confirmCalendarButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO get all events from google and put them inside the db
				db = DatabaseManager.getDatabaseInstance(getActivity());
				
				
			}
		});
        
        
        return rootView;
        
        
        
    }
    
    
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       	super.onActivityResult(requestCode, resultCode, data);
       	if (requestCode == ApiLoginInterface.RequestCode.LOGIN && resultCode == ApiLoginInterface.ResultCode.DONE){
       		getCalendarList();
       	}
    }
    
    
    /**
     * Starts the ApiController and let the user pick an account
     */
    public void login(){
    	Intent loginIntent = new Intent(this.getActivity(),ApiLoginInterface.class);
    	startActivityForResult(loginIntent, ApiLoginInterface.RequestCode.LOGIN);
    }
    
    public void createNewCalendar(){
    	CalendarFetcher calendarFetcher = new CalendarFetcher(this.getActivity(),progressBar);
    	calendarFetcher.setAction(CalendarFetcher.Actions.ADD_NEW_CALENDAR);
    	calendarFetcher.setListener(new TaskListener<String[]>(this.getActivity()) {

			@Override
			public void onTaskResult(String[]... results) {
				if (results[0] == Results.RESULT_OK){
					calendars.clear();
					getCalendarList();
				}

			}
    		
		});
    	calendarFetcher.execute();
    }
    
 
    /**
     * Retrieves calendar list calling the fetcher
     */
    public void getCalendarList(){
    	
    	//EXAMPLE: This is an example on how to use TaskListeners and CalendarFetcher in "Internal query mode" 
    	listFetcher = new CalendarFetcher(this.getActivity(), progressBar);
    	//Setting what the fetcher has to do (Fetch calendar list and build the model)
    	listFetcher.setAction(CalendarFetcher.Actions.CALENDARS_TO_MODEL);    	
    	//Adding TaskListener for getting results from AsyncTask
    	listFetcher.setListener(new TaskListener<String[]>(this.getActivity()) {
			@Override
			public void onTaskResult(String[]... results) {
				if (results[0] == Results.RESULT_OK){
				calendars.addAll(CalendarFetcher.getCalendarList());
				//UI update MUST be run from UI Thread
				CalendarPickerFragment.this.getActivity().runOnUiThread(new Runnable(){
			        public void run(){
			        	CalendarPickerFragment.this.listAdapter.notifyDataSetChanged();
			        	CalendarPickerFragment.this.newCalendarButton.setEnabled(true);
			        }
			    });
				}				
			}
		});
    	//Executing the fetcher
    	listFetcher.execute();
        }
 
    /**
     * Adapter for the calendar list view
     * @author Edoardo Giacomello
     *
     */
    private class CalendarListAdapter extends ArrayAdapter<CalendarModel>{

		public CalendarListAdapter(Context context, int resource, List<CalendarModel> objects) {
			super(context, resource, objects);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getViewOptimize(position, convertView, parent);
		}
		
		
		public View getViewOptimize(int position, View convertView, ViewGroup parent) {
			   ViewHolder viewHolder = null;
		        if (convertView == null) {
			  LayoutInflater inflater = (LayoutInflater) getContext()
			             .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			        convertView = inflater.inflate(R.layout.element_list_calendar, null);
			        viewHolder = new ViewHolder();
			        viewHolder.name = (TextView)convertView.findViewById(R.id.calendar_name);
			        viewHolder.owner = (TextView)convertView.findViewById(R.id.calendar_owner);
			        viewHolder.color = convertView.findViewById(R.id.calendar_color);
			        convertView.setTag(viewHolder);
		        }
		        else
		        {
		        	viewHolder = (ViewHolder) convertView.getTag();
		        }
			        
			        CalendarModel calendarItem = getItem(position);
			        viewHolder.name.setText(calendarItem.getName());
			        viewHolder.owner.setText(calendarItem.getOwner());
			        viewHolder.color.setBackgroundColor(calendarItem.getColor());
			       
			        
			        return convertView;
		}
		/**
		 * Avoids the inflatation of every single element on list scroll
		 * @author Edoardo Giacomello
		 *
		 */
		 private class ViewHolder {
		        public TextView name;
		        public TextView owner;
		        public View color;
		    }
    }

	
}
