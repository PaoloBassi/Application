package it.unozerouno.givemetime.view.questions.fragments;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.model.events.EventCategory;
import it.unozerouno.givemetime.model.places.PlaceModel;
import it.unozerouno.givemetime.model.questions.OptimizingQuestion;
import it.unozerouno.givemetime.view.editor.CategoryFragment;
import it.unozerouno.givemetime.view.editor.ConstraintsFragment;
import it.unozerouno.givemetime.view.editor.LocationEditorFragment;
import it.unozerouno.givemetime.view.editor.LocationEditorFragment.OnSelectedPlaceModelListener;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MissingDataFragment extends Fragment{
	OnOptimizingQuestionResponse mListener;
	TextView eventNameText;
	Button btnSave;
	Button btnCancel;
	ConstraintsFragment constraintFragment;
	LocationEditorFragment locationFragment;
	CategoryFragment categoryFragment;
	PlaceModel selectedPlace;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.question_missingdata, container, false);
		getUI(view);
		return view;
	}
	@Override
	public void onStart() {
		super.onStart();
		 loadQuestion();
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
        try {
            mListener = (OnOptimizingQuestionResponse) activity;
            
           
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnOptimizingQuestionResponse");
        }
    }
	
	/**
	 * Finds all the ui components
	 * @param view
	 */
	private void getUI(View view){
		eventNameText = (TextView) view.findViewById(R.id.question_missing_data_event_name);
		btnSave = (Button) view.findViewById(R.id.question_missingdata_btn_update);
		btnCancel = (Button) view.findViewById(R.id.question_missingdata_btn_cancel);
	}
	
	/**
	 * Gets the question from parent activity and fills the ui with proper data
	 */
	private void loadQuestion(){
		final OptimizingQuestion question = mListener.loadOptimizingQuestion();
		eventNameText.setText(question.getEventInstance().getEvent().getName());
		if (question.isMissingCategory()){
			missingCategory(question);
		}
		if (question.isMissingConstraints()){
			missingConstraints(question);
		}
		if (question.isMissingPlace()){
			missingPlace(question);
		}
		//Setting button onClick
		btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (question.isMissingCategory()){
					question.getEventInstance().getEvent().setCategory(categoryFragment.getCategorySelected());
				}
				if (question.isMissingConstraints()){
					question.getEventInstance().getEvent().setConstraints(constraintFragment.getConstraintList());
				}
				if (question.isMissingPlace()){
					//PlaceHolder: place are sent beck to activity from the PlaceEditorFragment
				}
				mListener.onUpdateClicked(question);
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onCancelClicked(question);
			}
		});
	}
	
	private void missingCategory(OptimizingQuestion question){
		categoryFragment = new CategoryFragment();
	 	addFragment(categoryFragment, "categoryFragment");
	}
	private void missingConstraints(OptimizingQuestion question){
		constraintFragment = new ConstraintsFragment();
		constraintFragment.setConstraintList(question.getEventInstance().getEvent().getConstraints());
		addFragment(constraintFragment, "constraintFragment");
	}
	private void missingPlace(OptimizingQuestion question){
		locationFragment = new LocationEditorFragment();
		//A listener must be set, but it has to belong to activity
		addFragment(locationFragment, "locationFragment");
	};
	
	
	
	private void addFragment(Fragment fragment, String name){
		getFragmentManager().beginTransaction().add(R.id.question_missingdata_container,fragment,name).commit();
	}
	
	public void removePlaceFragment(){
		if(locationFragment != null) getFragmentManager().beginTransaction().remove(locationFragment).commit();
	}
	
	public interface OnOptimizingQuestionResponse {
		/**
		 * Called from the fragment when it tries to load a question
		 * @return
		 */
		OptimizingQuestion loadOptimizingQuestion();
		/**
		 * Called when the user decides to update the location of the question into the event
		 * @param question
		 */
		void onUpdateClicked(OptimizingQuestion question);
		/**
		 * Called when the user decides to do not make any action
		 */
		void onCancelClicked(OptimizingQuestion question);
	}
	
}
