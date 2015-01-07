package it.unozerouno.givemetime.model.events;

import it.unozerouno.givemetime.model.constraints.Constraint;
import it.unozerouno.givemetime.model.places.PlaceModel;

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
		Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
		cal.setTime(date);
		return cal;
	}

	/**
	 * Parse the convetional format date into a calendar representation for EventModel
	 * @param formatDate: date retrieved from repeated events in the format yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
	 * @return calendar object containing the formatDate information
	 * @throws ParseException
	 */
	
	private Calendar formatDateToCalendar(String formatDate) throws ParseException{
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
		sdf.setCalendar(calendar);
		calendar.setTime(sdf.parse(formatDate));
		return calendar;
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
