package it.unozerouno.givemetime.model.events;

import it.unozerouno.givemetime.model.constraints.Constraint;
import it.unozerouno.givemetime.model.places.PlaceModel;
import it.unozerouno.givemetime.utils.CalendarUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import android.graphics.Color;
import android.text.format.Time;

public class EventModel {
	private String ID; //This is the id from Google calendar event
	private String name; 
	private Time startingDateTime;
	private Time endingDateTime;
	private int color;
	private Set<Constraint> recursions;
	private Set<Constraint> constraints; 
	private PlaceModel place;
	private Boolean doNotDisturb;
	private EventCategory category;
	private EventModelListener listener;
	
	public EventModel(String _id, String _name) {
		ID = _id;
		name = _name;
	}
	
	public EventModel(String _id, String _name, long sTime, long eTime){
		ID = _id;
		name = _name;
		startingDateTime = CalendarUtils.longToTime(sTime);
		endingDateTime = CalendarUtils.longToTime(eTime);
		
	}
	
	public Time getStartingDateTime() {
		return startingDateTime;
	}

	public void setStartingDateTime(Time startingDateTime) {
		this.startingDateTime = startingDateTime;
	}

	public Time getEndingDateTime() {
		return endingDateTime;
	}

	public void setEndingDateTime(Time endingDateTime) {
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
	
	public void addListener(EventModelListener listener){
		this.listener = listener;
	}
	/**
	 * Notify all listeners associated with this Event that this event has been changed.
	 */
	public void setUpdated(){
		listener.notifyChange(this);
	}
	
}
