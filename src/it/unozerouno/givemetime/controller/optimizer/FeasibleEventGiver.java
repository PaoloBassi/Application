package it.unozerouno.givemetime.controller.optimizer;

import it.unozerouno.givemetime.controller.fetcher.DatabaseManager;
import it.unozerouno.givemetime.model.events.EventDescriptionModel;
import it.unozerouno.givemetime.model.events.EventInstanceModel;
import it.unozerouno.givemetime.model.events.EventListener;
import it.unozerouno.givemetime.view.utilities.OnDatabaseUpdatedListener;

import java.util.ArrayList;

import android.content.Context;
import android.location.Location;
import android.text.format.Time;

public class FeasibleEventGiver {

	
	
	/**
	 * This function checks all event instances in the next month and return all feasible events (all events with active constraints)
	 * for a given time. If the given time is null, then the current time is used
	 * 
	 * @param context
	 */
	public static void getFeasibleEvents(Context context, final Time when, final OnDatabaseUpdatedListener<ArrayList<EventInstanceModel>> feasibleEventsCallback){
		//Getting feasible events
		Time today = new Time();
		today.setToNow();
		today.set(0, 0, 0, today.monthDay, today.month, today.year);
		Time nextMonth = new Time();
		long aMonth = (long)1000*(long)60*(long)60*(long)24*(long)30;
		nextMonth.set(today.toMillis(false) + aMonth);
		DatabaseManager.getInstance(context);
		DatabaseManager.getEventsInstances(today, nextMonth, context, new EventListener<EventInstanceModel>() {
			ArrayList<EventInstanceModel> feasibleEvents = new ArrayList<EventInstanceModel>();
			@Override
			public void onLoadCompleted() {
				feasibleEventsCallback.updateFinished(feasibleEvents);
			}
			
			@Override
			public void onEventCreation(EventInstanceModel newEvent) {
				EventDescriptionModel eventDescription = newEvent.getEvent();
				if (eventDescription.isFeasible(when)){
						feasibleEvents.add(newEvent);
				}
			}
			
			@Override
			public void onEventChange(EventInstanceModel newEvent) {
			}
		});
	}
	/**
	 * Returns through listener the closest feasible (=which have active constraints and it is movable) event to the given location at the given time. 
	 * If many events share the same location, the one with minor starting time is given. If they are still equals, the duration is checked.
	 * If no Feasible events or no location is set for the feasible events, the method returns null.
	 * @param context
	 * @param currentLocation
	 * @param when
	 * @param closestFeasibleEvent
	 */
	public static void getClosestFisibleEvent(Context context, final Location currentLocation, final Time when, final OnDatabaseUpdatedListener<EventInstanceModel> closestFeasibleEvent){
		getFeasibleEvents(context, when, new OnDatabaseUpdatedListener<ArrayList<EventInstanceModel>>() {
			
			@Override
			protected void onUpdateFinished(ArrayList<EventInstanceModel> updatedItem) {
				EventInstanceModel closestEvent = null;
				for (EventInstanceModel eventInstance : updatedItem) {
					EventDescriptionModel currentEvent = eventInstance.getEvent();
					
					if (closestEvent==null){
						if (currentEvent.getLocation()!=null) closestEvent = eventInstance;
					}  
					else if (currentEvent.getLocation()!=null){
						//Here both closestEvent and newEvent have a set location
						closestEvent = compareEventLocations(closestEvent, eventInstance, currentLocation, when);
					}
				}
			}
		});
	}
	
	/**
	 * Compares the distance between two locations and a given location. If the distance is the same, then the temporal distance is used.
	 * If the Time parameter is null, the current time is used instead 
	 */
	private static EventInstanceModel compareEventLocations(EventInstanceModel firstEvent, EventInstanceModel secondEvent, Location fromWhere, Time when){
		float distanceFirst = fromWhere.distanceTo(firstEvent.getEvent().getLocation());
		float distanceSecond = fromWhere.distanceTo(secondEvent.getEvent().getLocation());
		if(distanceFirst<distanceSecond)return firstEvent;
		if(distanceFirst>distanceSecond)return secondEvent;
		//Here the events has the same location, checking times
			if(when == null) {
				when = new Time();
				when.setToNow();
			}
			long firstMillisDistance = firstEvent.getStartingTime().toMillis(false) - when.toMillis(false);
			long secondMillisDistance = secondEvent.getStartingTime().toMillis(false) - when.toMillis(false);
			if(firstMillisDistance < secondMillisDistance) return firstEvent;
			if(firstMillisDistance > secondMillisDistance) return secondEvent;
			//Here the event has same location and same starting time, selecting the one with less duration
			firstMillisDistance = firstEvent.getEndingTime().toMillis(false) - when.toMillis(false);
			secondMillisDistance = secondEvent.getEndingTime().toMillis(false) - when.toMillis(false);
			if(firstMillisDistance < secondMillisDistance) return firstEvent;
			if(firstMillisDistance > secondMillisDistance) return secondEvent;
			//Here the event seems to be the same, return the first
			return firstEvent;
	}
	
	public static void getNextEvent(Context context, final OnDatabaseUpdatedListener<EventInstanceModel> result){
		Time now = new Time();
		now.setToNow();
		Time tomorrow = new Time();
		long aDay = (long)1000*(long)60*(long)60*(long)24;
		tomorrow.set(now.toMillis(false) + aDay);
		
		DatabaseManager.getInstance(context);
		DatabaseManager.getEventsInstances(now, tomorrow, context, new EventListener<EventInstanceModel>() {
			EventInstanceModel closestEvent =null;
			
			@Override
			public void onLoadCompleted() {
				result.updateFinished(closestEvent);
			}
			
			@Override
			public void onEventCreation(EventInstanceModel newEvent) {
				if ((closestEvent != null)){
					
					if (closestEvent.getStartingTime().toMillis(false) > newEvent.getStartingTime().toMillis(false) ){
						closestEvent=newEvent;
					}
				}
				else closestEvent = newEvent;
			}
			
			@Override
			public void onEventChange(EventInstanceModel newEvent) {
			}
		});
		
		
		
	}
}
