package it.unozerouno.givemetime.view.main.fragments;

import it.unozerouno.givemetime.R;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentOne extends Fragment{
	
	private TextView tvItemName;
	
	public static final String ITEM_NAME = "item_name";
	
	public FragmentOne() {
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_one_layout, container, false);
		
		tvItemName = (TextView) view.findViewById(R.id.frag1_text);
		tvItemName.setText(R.string.frag1_text);
		
		return view;
	}
}
