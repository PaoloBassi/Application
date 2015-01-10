package it.unozerouno.givemetime.model.constraints;

import java.util.Calendar;

/**
 * Represents an intervall between two week days (ex "From Monday to Friday")
 * @author Edoardo Giacomello <edoardo.giacomello1990@gmail.com>
 *
 */
public class DayConstraint extends Constraint {
	private int startingDay;
	private int endingDay;
	
	
	public DayConstraint(int startingDay, int endingDay) {
		this.startingDay=startingDay;
		this.endingDay = endingDay;
	}
	



	@Override
	public Boolean isActive() {
		// TODO Auto-generated method stub
		return null;
	}}
