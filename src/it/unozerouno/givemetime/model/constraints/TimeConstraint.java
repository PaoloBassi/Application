package it.unozerouno.givemetime.model.constraints;

import android.text.format.Time;
/**
 * Represents a TIME interval. Date and Day of week are not considered. (ex: from 12:00 to 13:00)
 * @author Edoardo Giacomello
 *
 */
public class TimeConstraint extends Constraint{
	private Time startingTime;
	private Time endingTime;
	
	public TimeConstraint(Time startingTime, Time endingTime) {
		this.startingTime = startingTime;
		this.endingTime = endingTime;
	}
	
	
	@Override
	/**
	 * Returns True if this constraint is Active. A timeConstraint is active if current hour, minutes and seconds are within the interval of this object.
	 */
	public Boolean isActive() {
		Time now = new Time();
		now.setToNow();
		if ((now.hour >= startingTime.hour) && (now.hour <= endingTime.hour)){
			//TODO: Implement this method
		}
		return false;
		
	}
	
}
