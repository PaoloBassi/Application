package it.unozerouno.givemetime.model.events;

import it.unozerouno.givemetime.model.constraints.Constraint;
import it.unozerouno.givemetime.model.places.PlaceModel;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import android.graphics.Color;
import android.text.format.Time;

public class EventModel {
	private String ID;
	private String name; 
	private Calendar startingDateTime;
	private Calendar endingDateTime;
	private int color;
	private Set<Constraint> constraints;
	private PlaceModel place;
	private Boolean doNotDisturb;
	private EventCategory category;
	
	public EventModel(String _id, String _name) {
		ID = _id;
		name = _name;
	}
	
	public EventModel(String _id, String _name, long sTime, long eTime, int _color){
		ID = _id;
		name = _name;
		startingDateTime = longToCalendar(sTime);
		endingDateTime = longToCalendar(eTime);
		color = _color;
	}
	
	/**
	 * Transform a long representation of a Date into a Calendar
	 * @param dateLong: date in milliseconds from epoch
	 * @return calendar object representing the date
	 */
	private Calendar longToCalendar(long dateLong){
		Date date = new Date(dateLong);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

	public Calendar getStartingDateTime() {
		return startingDateTime;
	}

	public void setStartingDateTime(Calendar startingDateTime) {
		this.startingDateTime = startingDateTime;
	}

	public Calendar getEndingDateTime() {
		return endingDateTime;
	}

	public void setEndingDateTime(Calendar endingDateTime) {
		this.endingDateTime = endingDateTime;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	
}
