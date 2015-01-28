package it.unozerouno.givemetime.view.questions.fragments;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.model.questions.LocationMismatchQuestion;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class LocationMismatchFragment extends Fragment{
	OnLocationMismatchQuestionResponse mListener;
	TextView eventNameText;
	TextView eventPlaceText;
	TextView questionLocationText;
	Button updateButton;
	Button cancelButton;
	Button refuseButton;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.question_locationmismatch, container);
		getUI(view);
		return view;
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
        try {
            mListener = (OnLocationMismatchQuestionResponse) activity;
            loadQuestion();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnLocationMismatchQuestionResponse");
        }
    }
	/**
	 * Finds all the ui components
	 * @param view
	 */
	private void getUI(View view){
		eventNameText = (TextView) view.findViewById(R.id.question_location_mismatch_event_name);
		eventPlaceText = (TextView) view.findViewById(R.id.question_location_mismatch_event_set_location);
		questionLocationText = (TextView) view.findViewById(R.id.question_location_mismatch_question_location);
		updateButton = (Button) view.findViewById(R.id.question_location_btn_update);
		cancelButton = (Button) view.findViewById(R.id.question_location_btn_cancel);
		refuseButton = (Button) view.findViewById(R.id.question_location_btn_refuse);
	}
	
	/**
	 * Gets the question from parent activity and fills the ui with proper data
	 */
	private void loadQuestion(){
		LocationMismatchQuestion question = mListener.loadLocationMismatchQuestion();
		if(question.getEvent().getEvent().getPlace() == null){
			//TODO: Event has no place set, show the proper UI (hide buttons, etc)
		} else {
			//TODO: Event has place set
		}
	}
	
	public interface OnLocationMismatchQuestionResponse {
		LocationMismatchQuestion loadLocationMismatchQuestion();
		
	}
}
