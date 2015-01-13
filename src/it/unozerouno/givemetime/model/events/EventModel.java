package it.unozerouno.givemetime.model.events;

import it.unozerouno.givemetime.model.constraints.Constraint;
import it.unozerouno.givemetime.model.places.PlaceModel;
import it.unozerouno.givemetime.utils.CalendarUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
	private String RRULE;
	private String RDATE;
	private int color;
	private Set<Constraint> constraints; 
	private PlaceModel place;
	private Boolean doNotDisturb = false;
	private EventCategory category;
	private List<EventModelListener> listeners;
	
	public EventModel(String _id, String _name) {
		ID = _id;
		name = _name;
		listeners = new ArrayList<EventModelListener>();
	}
	
	public EventModel(String _id, String _name, long sTime, long eTime){
		ID = _id;
		name = _name;
		startingDateTime = CalendarUtils.longToTime(sTime);
		endingDateTime = CalendarUtils.longToTime(eTime);
		listeners = new ArrayList<EventModelListener>();
		
		
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

	public String getRRULE() {
		return RRULE;
	}

	public void setRRULE(String rRULE) {
		RRULE = rRULE;
	}

	public String getRDATE() {
		return RDATE;
	}

	public void setRDATE(String rDATE) {
		this.RDATE = rDATE;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public Set<Constraint> getConstraints() {
		return constraints;
	}

	public void setConstraints(Set<Constraint> constraints) {
		this.constraints = constraints;
	}

	public PlaceModel getPlace() {
		return place;
	}

	public void setPlace(PlaceModel place) {
		this.place = place;
	}

	public Boolean getDoNotDisturb() {
		return doNotDisturb;
	}

	public void setDoNotDisturb(Boolean doNotDisturb) {
		this.doNotDisturb = doNotDisturb;
	}

	public EventCategory getCategory() {
		return category;
	}

	public void setCategory(EventCategory category) {
		this.category = category;
	}

	public List<EventModelListener> getListeners() {
		return listeners;
	}

	public void setListeners(List<EventModelListener> listeners) {
		this.listeners = listeners;
	}

	public void addListener(EventModelListener listener){
		listeners.add(listener);
	}
	public void removeAllListeners(){
		listeners.clear();
	}
	public void removeListener(EventModelListener listener){
		listeners.remove(listener);
	}
	/**
	 * Notify all listeners associated with this Event that this event has been changed.
	 */
	public void setUpdated(){
	for (EventModelListener listener : listeners) {
		listener.notifyChange(this);
	}
	}
	/**
	 * Notify all listeners associated with this Event that this event has been created.
	 */
	public void setCreated(){
		for (EventModelListener listener : listeners) {
			listener.notifyCreation(this);
		}
	}
	
	public boolean isRecursive(){
		return (RDATE!=null || RRULE!=null);
	}
	
	
	
	@Override
	public String toString() {
		return "Id: " + this.ID + " Name: " + this.name  + " Start: " + startingDateTime  + " End: " + endingDateTime  + " RRULE: " + RRULE  + " RDATE: " + RDATE  + " Color: " + color  + " DND: " +doNotDisturb  + " Category: " + category  + " Recursive: " + isRecursive();
	}
}
