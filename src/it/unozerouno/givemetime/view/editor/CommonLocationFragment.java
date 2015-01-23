package it.unozerouno.givemetime.view.editor;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.controller.fetcher.DatabaseManager;
import it.unozerouno.givemetime.model.places.PlaceModel;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class CommonLocationFragment extends Fragment {
	private ListView placeView;
	private ArrayList<PlaceModel> placeList;
	private LocationsListAdapter listAdapter;
	
 @Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
	View view = inflater.inflate(R.layout.fragment_event_editor_locations, container);
	placeView = (ListView) view.findViewById(R.id.editor_edit_event_location_listview_locations);
	placeList = new ArrayList<PlaceModel>();
	listAdapter = new LocationsListAdapter(getActivity(), R.layout.element_list_location, placeList);
	placeView.setAdapter(listAdapter);

	
	//Fetching known locations
	fetchCommonLocations();
	return view;
}
 
 
 public void fetchCommonLocations(){
	 List<PlaceModel> newList = DatabaseManager.getPlaces();
	 placeList.clear();
	 placeList.addAll(newList);
	 listAdapter.notifyDataSetChanged();
 }
 
 public void setPlaceOnclick(LocationClickListener listener){
	 placeView.setOnItemClickListener(listener);
 }
 
 
 /**
  * Adapter for the locations list view
  * @author Edoardo Giacomello
  *
  */
 private class LocationsListAdapter extends ArrayAdapter<PlaceModel>{

		public LocationsListAdapter(Context context, int resource, List<PlaceModel> objects) {
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
			        convertView = inflater.inflate(R.layout.element_list_location, null);
			        viewHolder = new ViewHolder();
			        viewHolder.name = (TextView)convertView.findViewById(R.id.element_list_location_name);
			        viewHolder.address = (TextView)convertView.findViewById(R.id.element_list_location_address);
			        viewHolder.country = (TextView)convertView.findViewById(R.id.element_list_location_country);
			        convertView.setTag(viewHolder);
		        }
		        else
		        {
		        	viewHolder = (ViewHolder) convertView.getTag();
		        }
			        
			        PlaceModel placeItem = getItem(position);
			        viewHolder.name.setText(placeItem.getName());
			        viewHolder.address.setText(placeItem.getAddress());
			        viewHolder.country.setText(placeItem.getCountry());
			        
			        return convertView;
		}
		/**
		 * Avoids the inflatation of every single element on list scroll
		 * @author Edoardo Giacomello
		 *
		 */
		 private class ViewHolder {
		        public TextView name;
		        public TextView address;
		        public TextView country;
		    }
 }

 
}
