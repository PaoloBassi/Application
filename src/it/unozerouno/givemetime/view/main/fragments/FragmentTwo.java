package it.unozerouno.givemetime.view.main.fragments;

import it.unozerouno.givemetime.R;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentTwo extends Fragment{
	
	private TextView tvItemName;
	
	public static final String ITEM_NAME = "item_name";
	
	public FragmentTwo() {
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_two_layout, container, false);
		
		tvItemName = (TextView) view.findViewById(R.id.frag2_text);
		tvItemName.setText(R.string.frag2_text);
		
		return view;
	}
}