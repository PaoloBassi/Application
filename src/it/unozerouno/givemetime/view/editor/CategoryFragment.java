package it.unozerouno.givemetime.view.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.controller.fetcher.DatabaseManager;
import it.unozerouno.givemetime.model.events.EventCategory;
import it.unozerouno.givemetime.utils.Dialog;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class CategoryFragment extends Fragment {
	private Spinner spinnerCategory;
	private List<String> categories; 
	private String categoryName;
	private EventCategory selectedCategory;
	private OnSelectedCategory listener;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.editor_categories, container, false);
		spinnerCategory = (Spinner) view.findViewById(R.id.category_fragment_spinner);
		initializeSpinner();
		return view;
	}
	
	private void initializeSpinner(){
		 // retrieve the name of all categories
		 categories = new ArrayList<String>();
		 for (EventCategory category : DatabaseManager.getCategories()) {
			categories.add(category.getName());
		 }
		 Collections.reverse(categories);
		 // at the end, add the "Add category" option
		 categories.add("Add Category");
		 ArrayAdapter<String> spinnerAdapterCategory = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categories);
		 spinnerAdapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		 spinnerCategory.setAdapter(spinnerAdapterCategory);
		 // set the first item in the list as first selection
		 spinnerCategory.setSelection(0);
		 
		 
		 
		// set the spinner listener
			spinnerCategory.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					// if the position is not the last, save the name of the category and load the switch values associated
					if (position != (categories.size() - 1)){
						categoryName = categories.get(position);
						selectedCategory = DatabaseManager.getCategoryByName(categoryName);
						if (listener != null) listener.onSelectedCategory(selectedCategory);
					} else {
						// TODO creation of new category
						spinnerCategory.setSelection(0);
						Dialog.genericAlertDialog(getActivity(), R.string.Not_available, R.string.pay_categories);
				}
				}
				public void onNothingSelected(android.widget.AdapterView<?> arg0) {
				};
					
			});
			//Setting default selection
			setDefault();
	}
	/**
	 * set the default selection for this spinner
	 */
	public void setDefault(){
		spinnerCategory.setSelection(2);
	}
	/**
	 * Set the selected EventCategory
	 * @param category
	 */
	public void setSelection(EventCategory category){
		int categoryPosition = categories.indexOf(category.getName());
		if(categoryPosition != -1){
			selectedCategory = category;
		spinnerCategory.setSelection(categoryPosition);
		}
	}
	public EventCategory getCategorySelected(){
		return selectedCategory;
	}
	/**
	 * Set the callback for category selection
	 * @param listener
	 */
	public void setListener(OnSelectedCategory listener){
		this.listener = listener;
	}

	/**
	 * Callback for category selection
	 * @author Edoardo Giacomello <edoardo.giacomello1990@gmail.com>
	 */
	public interface OnSelectedCategory{
		public void onSelectedCategory (EventCategory category);
	}
}
