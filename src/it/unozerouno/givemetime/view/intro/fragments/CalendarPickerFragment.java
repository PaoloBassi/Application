package it.unozerouno.givemetime.view.intro.fragments;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.controller.fetcher.ApiController;
import it.unozerouno.givemetime.controller.fetcher.CalendarFetcher;
import it.unozerouno.givemetime.controller.fetcher.PlayController;

public class CalendarPickerFragment extends Fragment{
	ProgressBar progressBar;
	ListView calendarList;
	Button apiTestButton;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_calendar_picker, container, false);
        //Hiding ProgressBar
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        //Getting Calendar List from UI
        calendarList = (ListView) rootView.findViewById(R.id.list_calendar);
        //Setting API_TEST_BUTTON onclick
        apiTestButton = (Button) rootView.findViewById(R.id.api_test_btn);
        apiTestButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	initializeApi(v);
            }
         });
        
        //Setting FETCH_CALENDAR_BUTTON onClick
        Button fetchTestButton = (Button) rootView.findViewById(R.id.fetch_calendar_btn);
        fetchTestButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				getCalendarList();				
			}
		});
        return rootView;
    }
    
    /**
     * Launch the API Controller
     */
    public void initializeApi(View v){
    	startActivity(new Intent(this.getActivity(), PlayController.class));
    	Intent initIntent = new Intent(this.getActivity(),ApiController.class);
    	initIntent.setAction("API_INIT");
    	ApiController initializer = new ApiController(this.getActivity(), progressBar);
    	initializer.execute(initIntent);
    }
    
    public void getCalendarList(){
    	CalendarFetcher fetcher = new CalendarFetcher(this.getActivity(), calendarList);
    	Intent fetchIntent = new Intent("FETCH_CALENDARS");
    	fetcher.execute(fetchIntent);
    }
  
}
