package it.unozerouno.givemetime.model.events;

import it.unozerouno.givemetime.model.constraints.Constraint;
import it.unozerouno.givemetime.model.places.PlaceModel;
import it.unozerouno.givemetime.utils.CalendarUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;




import android.location.Location;
import android.text.format.Time;
/**
 * Represent an instance of an event which is descripted by an {@link EventDescriptionModel}.
 * @author Edoardo Giacomello <edoardo.giacomello1990@gmail.com>
 *
 */
public class EventInstanceModel extends EventModel{
	private EventDescriptionModel event;
	private List<EventListener<EventInstanceModel>> listeners;
	private Time startingTime;
	private Time endingTime;
	
	public EventInstanceModel(EventDescriptionModel parentEvent, Time start, Time end) {
		event = parentEvent;
		listeners = new ArrayList<EventListener<EventInstanceModel>>();
		startingTime = start;
		endingTime=end;
			}
		
	public EventDescriptionModel getEvent() {
		return event;
	}

	public void setEvent(EventDescriptionModel event) {
		this.event = event;
	}

	public Time getStartingTime() {
		return startingTime;
	}

	/**
	 * compute the duration of recursive events and set the duration
	 */
	public void setStartingTime() {
		if (event.isRecursive()){
			Time difference = new Time();
			difference.setJulianDay(Time.EPOCH_JULIAN_DAY);
			difference.hour = endingTime.hour - startingTime.hour;
			difference.minute = endingTime.minute - startingTime.minute;
			difference.second = endingTime.second - startingTime.second;
			String durationRFC = "P"  + (difference.hour * 3600 + difference.minute * 60 + difference.second) + "S"; 
			event.setDuration(durationRFC);
		} else {
			event.setDuration("");
		}
	}

	public Time getEndingTime() {
		return endingTime;
	}

	public void setEndingTime(Time endingTime) {
		this.endingTime = endingTime;
	}

	public List<EventListener<EventInstanceModel>> getListeners() {
		return listeners;
	}

	public void setListeners(List<EventListener<EventInstanceModel>> listeners) {
		this.listeners = listeners;
	}

	public void addListener(EventListener<EventInstanceModel> listener){
		listeners.add(listener);
	}
	public void removeAllListeners(){
		listeners.clear();
	}
	public void removeListener(EventListener<EventInstanceModel> listener){
		listeners.remove(listener);
	}
	/**
	 * Notify all listeners associated with this Event that this event has been changed.
	 */
	public void setUpdated(){
	for (EventListener<EventInstanceModel> listener : listeners) {
		listener.notifyChange(this);
	}
	}
	/**
	 * Notify all listeners associated with this Event that this event has been created.
	 */
	public void setCreated(){
		for (EventListener<EventInstanceModel> listener : listeners) {
			listener.notifyCreation(this);
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof EventInstanceModel)) return false;
		EventInstanceModel other = (EventInstanceModel) o;
		if (this.getEvent() == null || other.getEvent() == null) return false;
		return (this.getEvent() == other.getEvent());
	}
	
}
