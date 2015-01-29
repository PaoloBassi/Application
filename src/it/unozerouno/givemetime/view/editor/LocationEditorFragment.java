package it.unozerouno.givemetime.view.editor;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.controller.fetcher.DatabaseManager;
import it.unozerouno.givemetime.controller.fetcher.places.PlaceFetcher;
import it.unozerouno.givemetime.controller.fetcher.places.PlaceFetcher.PlaceResult;
import it.unozerouno.givemetime.model.places.PlaceModel;
import it.unozerouno.givemetime.view.utilities.OnDatabaseUpdatedListener;

import java.util.ArrayList;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class LocationEditorFragment extends Fragment {
	CommonLocationFragment fragmentLocationList;
	OnSelectedPlaceModelListener mListener;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.editor_edit_locations, container);
		fragmentLocationList= (CommonLocationFragment) getChildFragmentManager().findFragmentById(R.id.editor_edit_loactions_common);
		  AutoCompleteTextView autoCompView = (AutoCompleteTextView) view.findViewById(R.id.editor_edit_locations_autocomplete);
		  //Setting autocomplete  
		  autoCompView.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.element_list_location));
		    autoCompView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
					PlaceResult placeSelected = (PlaceResult) adapterView.getItemAtPosition(position);		
					LocationEditorFragment.this.addPlaceToFavourites(placeSelected);
				}
			});
		    
		    //Setting Location onClick on CommonLocationFragment
		    fragmentLocationList.setPlaceOnclick(new LocationClickListener() {
				
				@Override
				public void doSomething(PlaceModel placeSelected) {
					mListener.onSelectedPlaceModel(placeSelected);
				}
			});
		
		
		return view;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
        try {
            mListener = (OnSelectedPlaceModelListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnSelectedPlaceModelListener");
        }
    }
	
	
	private void addPlaceToFavourites(final PlaceResult place){
		OnDatabaseUpdatedListener updateListener = new OnDatabaseUpdatedListener() {
			@Override
			protected void onUpdateFinished(Object updatedItem) {
				//Tell the fragment to update the list
				fragmentLocationList.fetchCommonLocations();
				if(updatedItem != null && updatedItem instanceof PlaceModel){
					PlaceModel newPlace = (PlaceModel) updatedItem;
					//Selecting the new inserted element
					mListener.onSelectedPlaceModel(newPlace);
				}
				
			}
		};
		DatabaseManager.addPlaceAndFetchInfo(place, updateListener);
	}
	
	private class PlacesAutoCompleteAdapter extends ArrayAdapter<PlaceResult> implements Filterable {
	    private ArrayList<PlaceResult> resultList;

	    public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
	        super(context, textViewResourceId);
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
			        
			        PlaceResult placeItem = getItem(position);
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
	    
	    @Override
	    public int getCount() {
	        return resultList.size();
	    }

	    @Override
	    public PlaceResult getItem(int index) {
	        return resultList.get(index);
	    }

	    @Override
	    public Filter getFilter() {
	        Filter filter = new Filter() {
	            @Override
	            protected FilterResults performFiltering(CharSequence constraint) {
	                FilterResults filterResults = new FilterResults();
	                if (constraint != null) {
	                    // Retrieve the autocomplete results.
	                    resultList = PlaceFetcher.autocomplete(constraint.toString());

	                    // Assign the data to the FilterResults
	                    filterResults.values = resultList;
	                    filterResults.count = resultList.size();
	                }
	                return filterResults;
	            }

	            @Override
	            protected void publishResults(CharSequence constraint, FilterResults results) {
	                if (results != null && results.count > 0) {
	                    notifyDataSetChanged();
	                }
	                else {
	                    notifyDataSetInvalidated();
	                }
	            }};
	        return filter;
	    }
	}

	public interface OnSelectedPlaceModelListener{
		public void onSelectedPlaceModel(PlaceModel place);
	}
}
