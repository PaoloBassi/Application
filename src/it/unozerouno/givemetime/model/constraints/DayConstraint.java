package it.unozerouno.givemetime.model.constraints;

import android.text.format.Time;

/**
 * Represents an interval between two week days (ex "From Monday to Friday")
 * @author Edoardo Giacomello <edoardo.giacomello1990@gmail.com>
 *
 */
public class DayConstraint extends Constraint {
	private int startingDay;
	private int endingDay;
	public static final String TYPE = "DayConstraint";
	
	/**
	 * Create a new interval between two weekdays, starting from startingDay (included) and ending at endingDay (included)
	 * Days are represented by integers where SUNDAY is 0 and SATURDAY is 6.  
	 * (Please refer to android.text.Time constants)
	 * @param startingDay
	 * @param endingDay
	 * 
	 */
	public DayConstraint(int startingDay, int endingDay) {
		this.startingDay=startingDay;
		this.endingDay = endingDay;
	}
	
	@Override
	public Boolean isActive() {
		Time now = new Time();
		now.setToNow();
		int today =now.weekDay;
		return isActive(today);
	}
	public Boolean isActive(int when){
		return ((when >= startingDay) && (when <= endingDay));
	}

	public int getStartingDay() {
		return startingDay;
	}

	public int getEndingDay() {
		return endingDay;
	}
	@Override
	public String toString() {
		return "from " + startingDay + " to " + endingDay;
	}
	@Override
	public DayConstraint clone() {
		DayConstraint copy = new DayConstraint(startingDay, endingDay);
		return copy;
	}
	
}
