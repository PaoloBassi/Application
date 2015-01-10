package it.unozerouno.givemetime.controller.fetcher;

import it.unozerouno.givemetime.model.events.EventModel;
import it.unozerouno.givemetime.model.events.EventModelListener;

/**
 * This is the entry Point for the model. It fetches all stored app data from DB and generates Model.
 * It also keeps the internal GiveMeTime db and the CalendarProvider synchronized by fetching updates from Google calendar and updating the internal db.
 * @author Edoardo Giacomello
 *
 */
public class DatabaseManager {
	public static class Results{
		public static String[] RESULT_OK = {"OK"};
		public static String[] RESULT_ERROR = {"ERROR"};
	}
	/**
	 * Example
	 * @param date
	 * @return
	 */
	public static EventModel getEvents(){
		//TODO: Complete this function
		//Here fetch from DB
		String id = "fixme";
		String name = "fixme";
		Long time = (long) 1;
		int color = 0;
		
		//Creating model
		
		EventModel newEvent = new EventModel(id,name,time,time,color);
		//Adding listener for updating Google Calendar
		newEvent.addListener(new EventModelListener() {
			
			@Override
			public void onEventChange(EventModel changedEvent) {
				// TODO: HERE PUSH TO GOOGLE CALENDAR
			}
		});
		return newEvent;
	}
		
	
	/**
	 * Pulls all new events from Google Calendar
	 */
	public static boolean synchronize(){
		//Return true if all is ok
		return true;
		//TODO: synchronization
	}
	
	
	
	public 
	
	
	/**
	 * This class creates the GiveMeTime database
	 * @author Edoardo Giacomello <edoardo.giacomello1990@gmail.com>
	 *
	 */
	class DatabaseCreator {
		
		public void createDatabase(){
			//TODO: here create all the tables
		}
		
		
		
		
	}
}
