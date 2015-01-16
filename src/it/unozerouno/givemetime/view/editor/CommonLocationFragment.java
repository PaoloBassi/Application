package it.unozerouno.givemetime.view.editor;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.internal.nu;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.model.CalendarModel;
import it.unozerouno.givemetime.model.places.PlaceModel;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class CommonLocationFragment extends Fragment {
	private ListView locationView;
	private EventEditorActivity caller;
	private ArrayList<PlaceModel> placeList;
	private LocationsListAdapter listAdapter;
	private Button addBtn;
	private Button editBtn;
	
 @Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
	View view = inflater.inflate(R.layout.fragment_event_editor_locations, container);
	locationView = (ListView) view.findViewById(R.id.editor_edit_event_location_listview_locations);
	locationView.setVisibility(View.INVISIBLE);
	placeList = new ArrayList<PlaceModel>();
	listAdapter = new LocationsListAdapter(getActivity(), R.layout.element_list_location, placeList);
	locationView.setAdapter(listAdapter);
	//Setting onClick for Locations
	locationView.setOnItemClickListener(new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
			// reset color of all elements
			for (int i = 0; i < adapter.getChildCount(); i++) {
				adapter.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
			}
			// change the background color of the selected element
			view.setBackgroundColor(Color.LTGRAY);
			// save the element selected
			PlaceModel placeSelected = (PlaceModel) adapter.getItemAtPosition(position);
			CommonLocationFragment.this.locationView.setSelection(position);
			placeSelected(placeSelected);
		}
	});
	
	//Button Management
	addBtn = (Button) view.findViewById(R.id.editor_edit_event_location_btn_add);
	editBtn = (Button) view.findViewById(R.id.editor_edit_event_location_btn_edit);
	addBtn.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO open Location Editor	
		}
	});
	editBtn.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO open Location Editor	
		}
	});
	
	//Fetching known locations
	fetchCommonLocations();
	return view;
}
 
 private void placeSelected(PlaceModel place){
	 caller.setSelectedPlaceModel(place);
 }
 
 private void fetchCommonLocations(){
	 //TODO: Fetch locations here from DatabaseManager
	 
	 //Here's a test
	 PlaceModel testItem =new PlaceModel("0", "TestLocation", null);
	 placeList.add(testItem);
	 listAdapter.notifyDataSetChanged();
 }
 
 public void setCaller(EventEditorActivity eventEditorActivity) {
		this.caller = eventEditorActivity;
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
			        viewHolder.info = (TextView)convertView.findViewById(R.id.element_list_location_info);
			        convertView.setTag(viewHolder);
		        }
		        else
		        {
		        	viewHolder = (ViewHolder) convertView.getTag();
		        }
			        
			        PlaceModel placeItem = getItem(position);
			        viewHolder.name.setText(placeItem.getName());
			        viewHolder.info.setText(placeItem.getInfo());
			       
			        
			        return convertView;
		}
		/**
		 * Avoids the inflatation of every single element on list scroll
		 * @author Edoardo Giacomello
		 *
		 */
		 private class ViewHolder {
		        public TextView name;
		        public TextView info;
		    }
 }


}
