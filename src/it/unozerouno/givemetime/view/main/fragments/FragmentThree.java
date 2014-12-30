package it.unozerouno.givemetime.view.main.fragments;

import it.unozerouno.givemetime.R;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentThree extends Fragment{
	
	private TextView tvItemName;
	
	public static final String ITEM_NAME = "item_name";

	public FragmentThree() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_three_layout, container, false);
		
		tvItemName = (TextView) view.findViewById(R.id.frag3_text);
		tvItemName.setText(R.string.frag3_text);
		
		return view;
	}
}
