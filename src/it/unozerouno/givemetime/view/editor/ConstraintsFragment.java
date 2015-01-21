package it.unozerouno.givemetime.view.editor;

import java.util.ArrayList;
import java.util.List;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.model.constraints.ComplexConstraint;
import it.unozerouno.givemetime.view.editor.constraints.SingleConstraintFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ConstraintsFragment extends Fragment{
	ArrayList<SingleConstraintFragment> constraintFragments;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_event_editor_constraints, container);
		constraintFragments = new ArrayList<SingleConstraintFragment>();
		return view;
	}
	public void setConstraintList(List<ComplexConstraint> constraints){
		for (ComplexConstraint complexConstraint : constraints) {
			addFragment(complexConstraint);
			
		}
	}
	public List<ComplexConstraint> getConstrintList(){
		List<ComplexConstraint> newConstraintList = new ArrayList<ComplexConstraint>();
		for (SingleConstraintFragment singleConstraintFragment : constraintFragments) {
			ComplexConstraint newConstraint = singleConstraintFragment.getConstraint();
			newConstraintList.add(newConstraint);
		}
		return newConstraintList;
	}
	private void addFragment(ComplexConstraint complexConstraint){
		SingleConstraintFragment complexConstraintFragment = new SingleConstraintFragment();
		getFragmentManager().beginTransaction().add(R.id.constraint_container,complexConstraintFragment).commit();
		complexConstraintFragment.setConstraint(complexConstraint);
	}
	private void removeFragment(){
		if(!constraintFragments.isEmpty()){
		SingleConstraintFragment fragmentToDelete = constraintFragments.remove(constraintFragments.size()-1);
		getFragmentManager().beginTransaction().remove(fragmentToDelete);
		}
	}
}
