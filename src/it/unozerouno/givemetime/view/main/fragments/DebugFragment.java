package it.unozerouno.givemetime.view.main.fragments;

import it.unozerouno.givemetime.R;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class DebugFragment extends Fragment{
	
	private static TextView debugTextView;
	
	public static final String ITEM_NAME = "item_name";
	
	public DebugFragment() {
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.fragment_debug_layout, container, false);
		debugTextView = (TextView) view.findViewById(R.id.debug_textview);
		setBtnOnclick(view);
		return view;
	}
	
	private void setBtnOnclick(View v){
		Button locationBtn = (Button) v.findViewById(R.id.btn_locations);
		Button freetimeBtn = (Button) v.findViewById(R.id.btn_freetime);
		Button editCalendarBtn = (Button) v.findViewById(R.id.btn_calendar_view);
		
		
		locationBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
									
				}
		});
		freetimeBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
							
									
		}});
		editCalendarBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
						Fragment calendarFragment = new EventListFragment();
						openFragment(calendarFragment);
		}});
		
		
	}
	
	public void openFragment(Fragment fragment){
		if (fragment != null){
	        // insert the fragment into the view, replacing the existing one
	        // obtain the fragment manager
	        FragmentManager fragmentManager = getFragmentManager();
	        // start transaction, replace the fragment, commit
	        fragmentManager.beginTransaction()
	                        .replace(R.id.content_frame, fragment)
	                        .commit();
	        
        }
	}
	
	public static void log(String msg){
		if (debugTextView == null) return;
		debugTextView.setText(msg);
		return;
	}
	
	
}