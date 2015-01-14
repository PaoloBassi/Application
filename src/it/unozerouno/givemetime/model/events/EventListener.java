package it.unozerouno.givemetime.model.events;

public abstract class EventListener<EventModel> {
	public abstract void onEventChange(EventModel newEvent);
	public abstract void onEventCreation(EventModel newEvent);
	public void notifyChange(EventModel changedEvent){
		onEventChange(changedEvent);
	}
	public void notifyCreation(EventModel createdEvent){
		onEventCreation(createdEvent);
	}
}
