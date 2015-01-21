package it.unozerouno.givemetime.view.editor;

import java.util.ArrayList;
import java.util.List;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.model.constraints.ComplexConstraint;
import it.unozerouno.givemetime.view.editor.constraints.ConstraintHeaderFragment;
import it.unozerouno.givemetime.view.editor.constraints.SingleConstraintFragment;
import it.unozerouno.givemetime.view.editor.constraints.SingleConstraintFragment.OnRemoveButtonClickedListener;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class ConstraintsFragment extends Fragment{
	ArrayList<SingleConstraintFragment> constraintFragments;
	ConstraintHeaderFragment header;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_event_editor_constraints, container);
		constraintFragments = new ArrayList<SingleConstraintFragment>();
		header = new ConstraintHeaderFragment();
		
		getFragmentManager().beginTransaction().add(R.id.constraint_container,header,"header").commit();
		header.setOnAddButtonOnClick(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addFragment(null);
			}
		});
		return view;
	}

	public void setConstraintList(List<ComplexConstraint> constraints){
		for (ComplexConstraint complexConstraint : constraints) {
			addFragment(complexConstraint);
			
		}
	}
	public List<ComplexConstraint> getConstraintList(){
		List<ComplexConstraint> newConstraintList = new ArrayList<ComplexConstraint>();
		for (SingleConstraintFragment singleConstraintFragment : constraintFragments) {
			ComplexConstraint newConstraint = singleConstraintFragment.getConstraint();
			newConstraintList.add(newConstraint);
		}
		return newConstraintList;
	}
	private void addFragment(ComplexConstraint complexConstraint){
		SingleConstraintFragment complexConstraintFragment = new SingleConstraintFragment();
		complexConstraintFragment.setListener(new OnRemoveButtonClickedListener() {
			
			@Override
			public void onRemoveButtonClicked(SingleConstraintFragment fragment) {
				removeFragment(fragment);
			}
		});
		getFragmentManager().beginTransaction().add(R.id.constraint_container,complexConstraintFragment).commit();
	}
	
	private void removeFragment(SingleConstraintFragment fragment){
		if(!constraintFragments.isEmpty()){
		constraintFragments.remove(fragment);
		getFragmentManager().beginTransaction().remove(fragment).commit();
		}
	}
}
