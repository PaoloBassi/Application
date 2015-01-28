package it.unozerouno.givemetime.view.questions.fragments;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.model.questions.FreeTimeQuestion;
import it.unozerouno.givemetime.model.questions.LocationMismatchQuestion;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class FreeTimeFragment  extends Fragment{
	OnFreeTimeQuestionResponse mListener;
	TextView eventSuggestion;
	Button updateButton;
	Button cancelButton;
	Button createButton;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.question_free_time, container, false);
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
            mListener = (OnFreeTimeQuestionResponse) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFreeTimeQuestionResponse");
        }
    }
	
	/**
	 * Finds all the ui components
	 * @param view
	 */
	private void getUI(View view){
		eventSuggestion = (TextView) view.findViewById(R.id.question_free_time_event_suggestion);
		updateButton = (Button) view.findViewById(R.id.question_freetime_btn_update);
		cancelButton = (Button) view.findViewById(R.id.question_freetime_btn_cancel);
		createButton = (Button) view.findViewById(R.id.question_freetime_btn_create);
	}
	
	/**
	 * Gets the question from parent activity and fills the ui with proper data
	 */
	private void loadQuestion(){
		FreeTimeQuestion question = mListener.loadFreeTimeQuestion();
		eventSuggestion.setText(question.getClosestEvent().getEvent().getName());
	}
	public interface OnFreeTimeQuestionResponse {
		/**
		 * Called from the fragment when it tries to load a question
		 * @return
		 */
		FreeTimeQuestion loadFreeTimeQuestion();
		/**
		 * Called when the user decides to update the location of the question into the event
		 * @param question
		 */
		void onUpdateClicked(FreeTimeQuestion question);
		/**
		 * Called when the user decides to do not make any action
		 */
		void onCancelClicked(FreeTimeQuestion question);
		/**
		 * Called when the user decides to make a new event because it was not attending specified event
		 */
		void onCreateClicked(FreeTimeQuestion question);
	}
}
