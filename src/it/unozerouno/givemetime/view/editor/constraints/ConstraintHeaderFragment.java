package it.unozerouno.givemetime.view.editor.constraints;

import it.unozerouno.givemetime.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class ConstraintHeaderFragment extends Fragment{
	Button addBtn;
	OnClickListener addListener;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.editor_constraint_header_fragment, null);
		addBtn = (Button) view.findViewById(R.id.editor_constraint_header_btn_add);
		addBtn.setOnClickListener(addListener);
		return view;
	}
	
	public void setOnAddButtonOnClick(OnClickListener listener){
		addListener = listener;
	}
}
