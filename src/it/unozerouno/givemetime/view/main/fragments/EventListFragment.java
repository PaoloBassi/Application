package it.unozerouno.givemetime.view.main.fragments;

import java.util.ArrayList;
import java.util.List;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.controller.fetcher.CalendarFetcher;
import it.unozerouno.givemetime.model.events.EventModel;
import it.unozerouno.givemetime.utils.TaskListener;
import android.os.Bundle;
import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class EventListFragment extends Fragment{
	
	ListView eventListView;
	ArrayList<EventModel> events;
	EventListAdapter listAdapter;
	
	public static final String ITEM_NAME = "item_name";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_event_list, container, false);
		
        //Getting Calendar List from UI
        eventListView = (ListView) view.findViewById(R.id.list_event);
        events = new ArrayList<EventModel>();
        listAdapter = new EventListAdapter(getActivity(), R.layout.element_list_event, events);
        eventListView.setAdapter(listAdapter);
        
        getEventList();
		
		return view;
	}
	
	
    /**
     * Retrieves event list calling the fetcher
     */
    public void getEventList(){
    	
    	//EXAMPLE: This is an example on how to use TaskListeners and CalendarFetcher in "Internal query mode" 
    	CalendarFetcher listFetcher = new CalendarFetcher(this.getActivity());
    	//Setting what the fetcher has to do (Fetch calendar list and build the model)
    	listFetcher.setAction(CalendarFetcher.Actions.EVENTS_TO_MODEL);    	
    	//Adding TaskListener for getting results from AsyncTask
    	listFetcher.setListener(new TaskListener<String[]>(this.getActivity()) {
			@Override
			public void onTaskResult(String[]... results) {
				if (results[0] == CalendarFetcher.Results.RESULT_OK){
				events.addAll(CalendarFetcher.getEventList());
				//UI update MUST be run from UI Thread
				EventListFragment.this.getActivity().runOnUiThread(new Runnable(){
			        public void run(){
			        	EventListFragment.this.listAdapter.notifyDataSetChanged();
			        }
			    });
				}				
			}
		});
    	//Executing the fetcher
    	listFetcher.execute();
        }
    
    
    /**
     * Adapter for the event list view
     * @author Paolo Bassi
     *
     */
    private class EventListAdapter extends ArrayAdapter<EventModel>{

		public EventListAdapter(Context context, int resource, List<EventModel> objects) {
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
			        convertView = inflater.inflate(R.layout.element_list_event, null);
			        viewHolder = new ViewHolder();
			        viewHolder.id = (TextView)convertView.findViewById(R.id.event_id);
			        viewHolder.name = (TextView)convertView.findViewById(R.id.event_name);
			        convertView.setTag(viewHolder);
		        }
		        else
		        {
		        	viewHolder = (ViewHolder) convertView.getTag();
		        }
			        
			        EventModel eventItem = getItem(position);
			        viewHolder.id.setText(eventItem.getID());
			        viewHolder.name.setText(eventItem.getName());
			   
			        return convertView;
		}
		/**
		 * Avoids the inflatation of every single element on list scroll
		 * @author Paolo Bassi
		 *
		 */
		 private class ViewHolder {
		        public TextView id;
		        public TextView name;
		    }
    }
	
}
