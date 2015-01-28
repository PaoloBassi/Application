package it.unozerouno.givemetime.view.questions.fragments;


import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.model.events.EventInstanceModel;
import it.unozerouno.givemetime.model.questions.FreeTimeQuestion;
import it.unozerouno.givemetime.model.questions.LocationMismatchQuestion;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
	Button createButton;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.question_locationmismatch, container, false);
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
            mListener = (OnLocationMismatchQuestionResponse) activity;
            
           
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
		createButton = (Button) view.findViewById(R.id.question_location_btn_create);
	}
	
	/**
	 * Gets the question from parent activity and fills the ui with proper data
	 */
	private void loadQuestion(){
		final LocationMismatchQuestion question = mListener.loadLocationMismatchQuestion();
		if(question.getEvent().getEvent().getPlace() == null){
			//Event has no place set, show the proper UI (hide buttons, etc)
			eventPlaceText.setText(R.string.location_not_set);
			createButton.setEnabled(false);
		} else {
			//Event has place set
			eventPlaceText.setText(question.getEvent().getEvent().getPlace().getName());
		}
		eventNameText.setText(question.getEvent().getEvent().getName());
		String placeName = question.getPlace().getName();
		if(placeName == null){
			placeName = question.getPlace().getFormattedAddress();
		}
		
		questionLocationText.setText(placeName);
		
		//Setting buttons onClick
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onCancelClicked(question);
			}
		});
		updateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onUpdateClicked(question);
			}
		});
		createButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onCreateClicked(question);
			}
		});
	}
	
	public interface OnLocationMismatchQuestionResponse {
		/**
		 * Called from the fragment when it tries to load a question
		 * @return
		 */
		LocationMismatchQuestion loadLocationMismatchQuestion();
		/**
		 * Called when the user decides to update the location of the question into the event
		 * @param question
		 */
		void onUpdateClicked(LocationMismatchQuestion question);
		/**
		 * Called when the user decides to do not make any action
		 */
		void onCancelClicked(LocationMismatchQuestion question);
		/**
		 * Called when the user decides to make a new event because it was not attending specified event
		 */
		void onCreateClicked(LocationMismatchQuestion question);
	}
}
