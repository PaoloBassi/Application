package it.unozerouno.givemetime.model.questions;

import android.content.Context;
import it.unozerouno.givemetime.model.constraints.TimeConstraint;
import it.unozerouno.givemetime.model.places.PlaceModel;


/**
 * This represent the answers to "Standard Questions". Result will be written in UserPreferences database table
 * @author Edoardo Giacomello
 *
 */
public class StandardQuestion extends QuestionModel {
	public StandardQuestion(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	private String account;
	private PlaceModel homeLocation;
	private TimeConstraint sleepTime;
	
	
	
	
	
}
