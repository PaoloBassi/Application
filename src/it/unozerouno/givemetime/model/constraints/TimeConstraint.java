package it.unozerouno.givemetime.model.constraints;

import android.text.format.Time;




/**
 * Represents a TIME interval. Date and Day of week are not considered. (ex: from 12:00 to 13:00)
 * Conventional Day is set to EPOCH_JULIAN_TIME
 * @author Edoardo Giacomello
 *
 */
public class TimeConstraint extends Constraint{
	private Time startingTime;
	private Time endingTime;
	private boolean containsMidnight = false;
	
	/**
	 * Creates a Time interval (hour-minute-seconds). Day-Month-Year of parameters will be ignored. 
	 * @param startTime
	 * @param endTime
	 */
	public TimeConstraint(Time startTime, Time endTime) {
		
		startingTime = new Time();
		endingTime = new Time();
		//Normalizing Day
		startingTime.setJulianDay(Time.EPOCH_JULIAN_DAY);
		endingTime.setJulianDay(Time.EPOCH_JULIAN_DAY);
		
		
		//Copying time
			startingTime.hour = startTime.hour;
			startingTime.minute = startTime.minute;
			startingTime.second = startTime.second;
			
			endingTime.hour = endTime.hour;
			endingTime.minute = endTime.minute;
			endingTime.second = endTime.second;
			
			if(endingTime.before(startingTime)){
				//The interval contains midnight, shifting endTime the next day for keeping integrity
				endingTime.set(endingTime.second, endingTime.minute, endingTime.hour, endingTime.monthDay+1, endingTime.month, endingTime.year);
				containsMidnight=true;
		}
	}
	
	public TimeConstraint(int sHour, int sMinute,int sSecond, int eHour, int eMinute, int eSecond){
		startingTime = new Time();
		endingTime = new Time();
		//Normalizing Day
		startingTime.setJulianDay(Time.EPOCH_JULIAN_DAY);
		endingTime.setJulianDay(Time.EPOCH_JULIAN_DAY);
		
		//Copying time
		startingTime.hour = sHour;
		startingTime.minute = sMinute;
		startingTime.second = sSecond;
		
		endingTime.hour = eHour;
		endingTime.minute = eMinute;
		endingTime.second = eSecond;
		
		if(endingTime.before(startingTime)){
			//The interval contains midnight, shifting endTime the next day for keeping integrity
			endingTime.set(endingTime.second, endingTime.minute, endingTime.hour, endingTime.monthDay+1, endingTime.month, endingTime.year);
			containsMidnight=true;
	}
	}
	
	
	
	@Override
	/**
	 * Returns True if this constraint is Active now. A timeConstraint is active if current hour, minutes and seconds are within the interval of this object.
	 */
	public Boolean isActive() {
		Time now = new Time();
		now.setToNow();
		Time normalizedNow = new Time();
		normalizedNow.setJulianDay(Time.EPOCH_JULIAN_DAY);
		if (!containsMidnight){
			normalizedNow.set(now.second, now.minute, now.hour, normalizedNow.monthDay, normalizedNow.month, normalizedNow.year);
		}else{
			//If the interval contains midnight we have to shift the input to the next day
			normalizedNow.set(now.second, now.minute, now.hour, normalizedNow.monthDay+1, normalizedNow.month, normalizedNow.year);
		}
			return (startingTime.before(normalizedNow)&&endingTime.after(normalizedNow));
	}
	
	public Boolean isActive (Time when){
		Time normalizedNow = new Time();
		normalizedNow.setJulianDay(Time.EPOCH_JULIAN_DAY);
		if (!containsMidnight){
			normalizedNow.set(when.second, when.minute, when.hour, normalizedNow.monthDay, normalizedNow.month, normalizedNow.year);
		}else{
			//If the interval contains midnight we have to shift the input to the next day
			normalizedNow.set(when.second, when.minute, when.hour, normalizedNow.monthDay+1, normalizedNow.month, normalizedNow.year);
		}
			return (startingTime.before(normalizedNow)&&endingTime.after(normalizedNow));
	}
	
	public Boolean isActive(Long when){
		Time whenTime = new Time();
		whenTime.set(when);
		return isActive(whenTime);
	}
	
	public Boolean isActive(int hour, int minute, int second){
		Time when = new Time();
		when.set(second, minute, hour, when.monthDay, when.month, when.year);
		return isActive(when);		
	}
	
	
	
	public Time getStartingTime() {
		return startingTime;
	}

	public Time getEndingTime() {
		return endingTime;
	}

	@Override
	public String toString() {
		return "from " + startingTime.hour +":" + startingTime.minute +  " to " +endingTime.hour+":"+endingTime.minute;
	}
	@Override
	public TimeConstraint clone() {
		Time newStart = new Time(startingTime);
		Time newEnd = new Time(endingTime);
		TimeConstraint copy = new TimeConstraint(newStart, newEnd);
		return copy;
	}
}
