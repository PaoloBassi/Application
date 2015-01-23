package it.unozerouno.givemetime.model.events;

public abstract class EventListener<EventModel> {
	public abstract void onEventChange(EventModel newEvent);
	/**
	 * This function is created when the event has been loaded and it is ready to be used
	 * @param newEvent
	 */
	public abstract void onEventCreation(EventModel newEvent);
	/**
	 * This function is called when all the requested event have been loaded successfully
	 */
	public abstract void onLoadCompleted();
	public void notifyChange(EventModel changedEvent){
		onEventChange(changedEvent);
	}
	public void notifyCreation(EventModel createdEvent){
		onEventCreation(createdEvent);
	}
	public void loadingComplete() {
		onLoadCompleted();
	}
}
