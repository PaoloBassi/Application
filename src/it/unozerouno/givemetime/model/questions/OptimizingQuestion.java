package it.unozerouno.givemetime.model.questions;

import it.unozerouno.givemetime.model.events.EventDescriptionModel;
import android.content.Context;

/**
 * This type of question is made when there are events with missing information
 * @author Edoardo Giacomello <edoardo.giacomello1990@gmail.com>
 *
 */
public class OptimizingQuestion extends QuestionModel{
	private EventDescriptionModel event;
	private boolean missingPlace; //If place is unknown
	private boolean missingCategory; //If category has not been set
	private boolean missingConstraints; //If event is marked as movable but no constraints are defined
	public OptimizingQuestion(Context context, EventDescriptionModel event,
			boolean missingPlace, boolean missingCategory,
			boolean missingConstraints) {
		super(context);
		this.event = event;
		this.missingPlace = missingPlace;
		this.missingCategory = missingCategory;
		this.missingConstraints = missingConstraints;
	}
	public EventDescriptionModel getEvent() {
		return event;
	}
	public void setEvent(EventDescriptionModel event) {
		this.event = event;
	}
	public boolean isMissingPlace() {
		return missingPlace;
	}
	public void setMissingPlace(boolean missingPlace) {
		this.missingPlace = missingPlace;
	}
	public boolean isMissingCategory() {
		return missingCategory;
	}
	public void setMissingCategory(boolean missingCategory) {
		this.missingCategory = missingCategory;
	}
	public boolean isMissingConstraints() {
		return missingConstraints;
	}
	public void setMissingConstraints(boolean missingConstraints) {
		this.missingConstraints = missingConstraints;
	}
	
	
	
	
	
	
	

}
