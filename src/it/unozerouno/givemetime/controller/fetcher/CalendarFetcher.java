package it.unozerouno.givemetime.controller.fetcher;

import it.unozerouno.givemetime.model.CalendarModel;
import it.unozerouno.givemetime.model.UserKeyRing;
import it.unozerouno.givemetime.model.events.EventModel;
import it.unozerouno.givemetime.utils.AsyncTaskWithListener;
import it.unozerouno.givemetime.utils.Results;
import it.unozerouno.givemetime.utils.TaskListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Instances;
import android.view.View;
import android.widget.ProgressBar;
/**
 * Fetcher for device stored calendars.
 * It supports both "external queries" [takes as input an Action and a projection and return each row in a specific TaskListener] and "internal" one [for fetching and Model creation, projections are managed
 * internally].
 * External Queries:  
 * Please specify a task with setAction() before calling .execute(), you can chose them from CalendarFetcher.Actions
 * You also have to specify the query projection when calling execute(), you can pick one from CalendarFetcher.Projections or specify your own.
 * Results are returned to attached TaskListener (use setListener)
 * Internal Queries: 
 * Just specify a compatible action (i.e. "CALENDARS_TO_MODEL") and call the relative getter (i.e. getCalendarList()) on the TaskListener onResult.
 * Note that in this case the result returned to the TaskListener is a string from the "Results" class.
 * 
 * In both cases it is possible to manage a ProgressBar on the calling activity (if provided).
 * @author Edoardo Giacomello <edoardo.giacomello1990@gmail.com>
 * @see TaskListener
 */
public class CalendarFetcher extends AsyncTaskWithListener<String, Void, String[]> {
	//NOTICE: For the developers - When adding new functions, please comply with the present structure.
	//I.E: Adding an actions: provide "Actions" integer with relative projections (if needed)
	//Add "case" in doInBackground()
	//Compute result in a separate function. See "getCalendarlist" for example.
	//Don't forget to call "setResult" whenever a single result (row) is ready!
	
	
	//Results
	private static List<CalendarModel> calendarList;
	private static List<EventModel> eventList;
	
	
	
	/**
	 * Overview of the possible actions performable from CalendarFetcher
	 * @author Edoardo Giacomello <edoardo.giacomello1990@gmail.com>
	 *
	 */
	public static class Actions{
		public static final int NO_ACTION = -1;
		public static final int CALENDARS_TO_MODEL = 1;
		public static final int LIST_OF_EVENTS = 2;
		public static final int ADD_NEW_CALENDAR = 3;
		public static final int LIST_EVENTS_ID_RRULE = 4;
		//... here other actions
	}
	/**
	 * Overview of recurrent projections to be used in CalendarFetcher
	 * @author Edoardo Giacomello <edoardo.giacomello1990@gmail.com>
	 *
	 */
	public static class Projections {
		//Calendar related
		public static String[] CALENDAR_ID_NAME_PROJ = {Calendars._ID, Calendars.NAME};
		public static String[] CALENDAR_ID_OWNER_NAME_COLOUR = {Calendars._ID, Calendars.OWNER_ACCOUNT, Calendars.NAME, Calendars.CALENDAR_COLOR};
		//Event related
		public static String[] EVENT_ID_RRULE_RDATE = {Events._ID,Events.RRULE,Events.RDATE};
		public static String[] EVENT_ID_TITLE = {Events._ID, Events.TITLE};
		public static String[] EVENT_INFOS = {Events._ID, Events.TITLE, Events.DTSTART, Events.DTEND, Events.EVENT_COLOR, Events.RRULE};
		public static String[] INSTANCES_INFOS = {Instances.EVENT_ID, Instances.BEGIN, Instances.END};
		//...
	}

	
	
	private static int task = Actions.NO_ACTION;
	Activity caller;
	ProgressBar progressBar;
	CalendarContract.Calendars calendars;
	
	public CalendarFetcher(Activity caller) {
		this.caller = caller;
	}
	
	public CalendarFetcher(Activity caller, ProgressBar progressBar){
		this(caller);
		this.progressBar =progressBar;
	}
	
	/**
	 * Set the action to perform in order to get expected result
	 * @param action
	 * @see CalendarFetcher
	 */
	public void setAction(int action){
		task = action;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		//If progressbar is present, than show it
		if (progressBar != null){
			progressBar.setVisibility(View.VISIBLE);
		}
	}
	@Override
	protected void onPostExecute(String[] result) {
		super.onPostExecute(result);
		//If progressbar is present, than hide it
		if (progressBar != null){
			progressBar.setVisibility(View.INVISIBLE);
		}
	}
	
	@Override
	protected String[] doInBackground(String... projection) {
		switch (task) {
		case Actions.NO_ACTION:
			break;
		case Actions.CALENDARS_TO_MODEL:
			calendarList = getCalendarModel();
			setResult(Results.RESULT_OK);
			break;
		case Actions.LIST_OF_EVENTS:
			getEvents();
			break;
		case Actions.ADD_NEW_CALENDAR:
			createCalendar();
			setResult(Results.RESULT_OK);
		case Actions.LIST_EVENTS_ID_RRULE:
			fetchEventList();
			
		//Add here new actions
		default:
			break;
		}
		
		return null;
	}
	
	/**
	 * Build the uri as sync adapter in order to gain more write access
	 * @param uri
	 * @param account
	 * @param accountType
	 * @return
	 */
	static Uri asSyncAdapter(Uri uri, String account, String accountType) {
	    return uri.buildUpon()
	        .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER,"true")
	        .appendQueryParameter(Calendars.ACCOUNT_NAME, account)
	        .appendQueryParameter(Calendars.ACCOUNT_TYPE, accountType).build();
	 }
	
		
	/**
	 * Fetch event list and returns each result to the Task Listener attached to CalendarFetcher
	 * @param projection: Columns Names
	 */
	private void getEvents(){
		Cursor eventCursor = null;
		ContentResolver cr = caller.getContentResolver();
		Uri uri = Events.CONTENT_URI;
		String[] projection = Projections.EVENT_INFOS;
		
		//For Identifying as SyncAdapter, User must already be logged)
		uri = asSyncAdapter(uri, UserKeyRing.getUserEmail(caller), "com.google");
		
		// execute the query, get a Cursor back
		eventCursor = cr.query(uri, projection, Events.CALENDAR_ID + " = " + UserKeyRing.getCalendarId(caller), null, null);
		
		// step through the records
		while(eventCursor.moveToNext()){
			String[] result = new String[projection.length];
			for (int i = 0; i < result.length; i++) {
				result[i] = eventCursor.getString(i);
			}
			// provide result to TaskListener
			setResult(result);
		}
		eventCursor.close();
	}
	
	private void fetchEventList(){
		Cursor cur = null;
		ContentResolver cr = caller.getContentResolver();
		Uri uri = Events.CONTENT_URI;
		
		String WHERE_CLAUSE = Events.CALENDAR_ID +" = " + UserKeyRing.getCalendarId(caller);
		// execute the query, get a Cursor back
		cur = cr.query(uri, Projections.EVENT_ID_RRULE_RDATE, WHERE_CLAUSE, null, null);
		
		// step through the records
		while(cur.moveToNext()){
			String[] result = new String[Projections.EVENT_ID_RRULE_RDATE.length];
			for (int i = 0; i < result.length; i++) {
				result[i] = cur.getString(i);
			}
			// provide result to TaskListener
			setResult(result);
		}
		cur.close();
	}
	
	
	/**
	 * This function fetches the calendars managing needed projections in order to provide a ready-to-use CalendarModel list.
	 */
	private List<CalendarModel> getCalendarModel(){
		List<CalendarModel> calendarList = new ArrayList<CalendarModel>();
		// Run query
		Cursor cur = null;
		ContentResolver cr = caller.getContentResolver();
		Uri uri = Calendars.CONTENT_URI;
		String[] projection = Projections.CALENDAR_ID_OWNER_NAME_COLOUR;
		
		//For Identifying as SyncAdapter, User must already be logged)
		uri = asSyncAdapter(uri, UserKeyRing.getUserEmail(caller), "com.google");
		
		String WHERE_CLAUSE = CalendarContract.Calendars.OWNER_ACCOUNT + " = '" + UserKeyRing.getUserEmail(caller) + 
				"' OR " + CalendarContract.Calendars.OWNER_ACCOUNT + " LIKE '%group.calendar.google.com%'";
		
		// Submit the query and get a Cursor object back. 
		cur = cr.query(uri, projection, WHERE_CLAUSE ,null, null);
		
		// Use the cursor to step through the returned records
		while (cur.moveToNext()) {
		   CalendarModel newCalendar = new CalendarModel(cur.getString(0), cur.getString(1), cur.getString(2), Integer.parseInt(cur.getString(3)));
		   calendarList.add(newCalendar);
		}
		cur.close();
		return calendarList;
		}
	
	/**
	 * This function fetches the events managing needed projections in order to provide a ready-to-use EventModel list.
	 * If the events has instances that repeats through time, they will be added to the list and displayed in the view
	 */
	private List<EventModel> getEventModel(){
		List<EventModel> eventList = new ArrayList<EventModel>();
		// Run query for events
		Cursor eventCursor = null;
		ContentResolver cr = caller.getContentResolver();
		Uri uri = Events.CONTENT_URI;   
		String[] projection = Projections.EVENT_INFOS;
		//For Identifying as SyncAdapter, User must already be logged)
		uri = asSyncAdapter(uri, UserKeyRing.getUserEmail(caller), "com.google");
		
		// Submit the query on the selected calendar and get a Cursor object back. 
		eventCursor = cr.query(uri, projection, Events.CALENDAR_ID + " = " + UserKeyRing.getCalendarId(caller), null, null);
		
		// run query for instances
		String[] instancesProjection = Projections.INSTANCES_INFOS;
		
		// Use the cursor to step through the returned records
		while (eventCursor.moveToNext()) {
			EventModel newEvent = new EventModel(eventCursor.getString(0), eventCursor.getString(1), eventCursor.getLong(2), eventCursor.getLong(3));
			eventList.add(newEvent);
			// if the event is recurrent
			if (eventCursor.getString(5) != null){
				// set the begin and the end of the event based on meaningful dates
				java.util.Calendar beginTime = java.util.Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
				beginTime.set(2010, 1, 1);
				long startMillis = beginTime.getTimeInMillis();
				java.util.Calendar endTime = java.util.Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
				endTime.set(2020,12,31);
				long endMillis = endTime.getTimeInMillis();
					
				// prepare the time slice in where the event has to take place
				Uri.Builder instancesUri = Instances.CONTENT_URI.buildUpon();
				ContentUris.appendId(instancesUri, startMillis);
				ContentUris.appendId(instancesUri, endMillis);
				// run the query on the Instances table an get the cursor with all the Instances related to the event
				Cursor instanceCursor = null;
				instanceCursor = cr.query(instancesUri.build(), instancesProjection, null, null, null);

				while(instanceCursor.moveToNext()){
					// TODO change the index in order to have unique IDs
					if (instanceCursor.getString(0).equals(eventCursor.getString(0))){
						EventModel newInstance = new EventModel(instanceCursor.getString(0), eventCursor.getString(1), instanceCursor.getLong(1), instanceCursor.getLong(2));
						eventList.add(newInstance);
					}
				}
				instanceCursor.close();
			}
		}
		eventCursor.close();
		
		return eventList;
		}
	
	/**
	 * Insert a new calendar into CalendarProvider
	 * @return id of the new calendar
	 */
	public void createCalendar(){
		//Setting calendar data
		ContentValues values = new ContentValues();
		values.put(Calendars.ACCOUNT_NAME,UserKeyRing.getUserEmail(caller));
		values.put(Calendars.ACCOUNT_TYPE, "com.google");
		values.put(Calendars.NAME, "GiveMeTime Calendar");
		values.put(Calendars.CALENDAR_DISPLAY_NAME, "GiveMeTime Calendar");
		values.put(Calendars.CALENDAR_COLOR, Color.GREEN);
		values.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
		values.put(Calendars.OWNER_ACCOUNT, UserKeyRing.getUserEmail(caller));
		values.put(Calendars.SYNC_EVENTS, 1);
		
		//TODO: Set here the timezone
		//values.put(Calendars.CALENDAR_TIME_ZONE, Locale.getDefault().t);
		
		ContentResolver cr = caller.getContentResolver();
		Uri uri = Calendars.CONTENT_URI;
		
		//For Identifying as SyncAdapter, User must already be logged)
				uri = asSyncAdapter(uri, UserKeyRing.getUserEmail(caller), "com.google");
				cr.insert(uri, values);
	}
	
	Activity getCaller(){return caller;}
	
	public static List<CalendarModel> getCalendarList(){
		return calendarList;
	}
	
	public static List<EventModel> getEventList(){
		return eventList;
	}
	}
