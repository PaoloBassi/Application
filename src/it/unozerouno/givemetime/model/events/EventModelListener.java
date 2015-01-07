package it.unozerouno.givemetime.model.events;

public abstract class EventModelListener {
	public abstract void onEventChange(EventModel newEvent);
	public void notifyChange(EventModel changedEvent){
		onEventChange(changedEvent);
	}
}
