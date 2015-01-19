package it.unozerouno.givemetime.model.events;

import it.unozerouno.givemetime.model.constraints.ComplexConstraint;
import it.unozerouno.givemetime.model.constraints.Constraint;
import it.unozerouno.givemetime.model.places.PlaceModel;
import it.unozerouno.givemetime.utils.CalendarUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.location.Location;
import android.text.format.Time;
/**
 * Represent an Event as abstract entity. Contains information which every Instance must refer to.
 * startingDateTime and endingDateTime refers to start and ed of the series of event, they match with the instance start and end only in the case of non repeating events. 
 * @author Edoardo Giacomello <edoardo.giacomello1990@gmail.com>
 *
 */
public class EventDescriptionModel extends EventModel{
	//Attributes inferred by Google Calendars
	private String ID; //This is the id from Google calendar event
	private String calendarId;
	private String name; 
	private Time startingDateTime;
	private Time endingDateTime;
	private String duration;
	private String RRULE;
	private String RDATE;
	private int color;
	private int allDay;
	
	//GiveMeTime specific attributes
	private List<ComplexConstraint> constraints; 
	private PlaceModel place;
	private Boolean doNotDisturb = false;
	private Boolean hasDeadline = true;
	private Boolean isMovable = false;
	private EventCategory category;
	private List<EventListener<EventDescriptionModel>> listeners;
	
	
	public EventDescriptionModel(String _id, String _name, long sTime, long eTime){
		ID = _id;
		name = _name;
		startingDateTime = CalendarUtils.longToTime(sTime);
		endingDateTime = CalendarUtils.longToTime(eTime);
		listeners = new ArrayList<EventListener<EventDescriptionModel>>();
		constraints = new ArrayList<ComplexConstraint>();
	}
	public EventDescriptionModel(String _id, String _name) {
		ID = _id;
		name = _name;
		listeners = new ArrayList<EventListener<EventDescriptionModel>>();
		constraints = new ArrayList<ComplexConstraint>();
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

	public Time getSeriesStartingDateTime() {
		return startingDateTime;
	}

	public void setSeriesStartingDateTime(Time startingDateTime) {
		this.startingDateTime = startingDateTime;
	}

	public Time getSeriesEndingDateTime() {
		return endingDateTime;
	}

	public void setSeriesEndingDateTime(Time endingDateTime) {
		this.endingDateTime = endingDateTime;
	}

	public String getDuration(){
		return duration;
	}
	
	public void setDuration(String duration) {
		this.duration = duration;
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

	public List<ComplexConstraint> getConstraints() {
		return constraints;
	}

	public void setConstraints(List<ComplexConstraint> constraints) {
		this.constraints = constraints;
	}
	
	
	
	
	public String getCalendarId() {
		return calendarId;
	}
	public void setCalendarId(String calendarId) {
		this.calendarId = calendarId;
	}
	public Boolean getHasDeadline() {
		return hasDeadline;
	}
	public void setHasDeadline(Boolean hasDeadline) {
		this.hasDeadline = hasDeadline;
	}
	public Boolean getIsMovable() {
		return isMovable;
	}
	public void setIsMovable(Boolean isMovable) {
		this.isMovable = isMovable;
	}
	public List<EventListener<EventDescriptionModel>> getListeners() {
		return listeners;
	}

	public void setListeners(List<EventListener<EventDescriptionModel>> listeners) {
		this.listeners = listeners;
	}

	public void addListener(EventListener<EventDescriptionModel> listener){
		listeners.add(listener);
	}
	public void removeAllListeners(){
		listeners.clear();
	}
	public void removeListener(EventListener<EventDescriptionModel> listener){
		listeners.remove(listener);
	}
	/**
	 * Notify all listeners associated with this Event that this event has been changed.
	 */
	public void setUpdated(){
	for (EventListener<EventDescriptionModel> listener : listeners) {
		listener.notifyChange(this);
	}
	}
	/**
	 * Notify all listeners associated with this Event that this event has been created.
	 */
	public void setCreated(){
		for (EventListener<EventDescriptionModel> listener : listeners) {
			listener.notifyCreation(this);
		}
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

	public boolean isRecursive(){
		return (RDATE!=null || RRULE!=null);
	}
	
	public int isAllDayEvent(){
		return allDay;
	}
	
	public void setAllDay(int allDay) {
		this.allDay = allDay;
	}
	
	public Location getLocation(){
		return place.getLocation();
	}
	
	@Override
	public String toString() {
		return "Id: " + this.ID + " Name: " + this.name  + " Start: " + startingDateTime  + " End: " + endingDateTime  + " RRULE: " + RRULE  + " RDATE: " + RDATE  + " Color: " + color  + " DND: " +doNotDisturb  + " Category: " + category  + " Recursive: " + isRecursive();
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof EventDescriptionModel)) return false;
		EventDescriptionModel other = (EventDescriptionModel) o;
		if (this.ID == null || other.getID() == null) return false;
		return (this.ID == other.ID);
	}
}
