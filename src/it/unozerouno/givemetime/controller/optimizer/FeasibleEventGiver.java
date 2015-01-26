package it.unozerouno.givemetime.controller.optimizer;

import android.content.Context;
import android.text.format.Time;
import it.unozerouno.givemetime.controller.fetcher.DatabaseManager;
import it.unozerouno.givemetime.model.events.EventInstanceModel;
import it.unozerouno.givemetime.model.events.EventListener;
import it.unozerouno.givemetime.view.utilities.OnDatabaseUpdatedListener;

public class FeasibleEventGiver {

	
	private FeasibleEventGiver() {
	}
	
	public static void getClosestFeasibleEvent(Context context){
		//Getting feasible events
		
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
