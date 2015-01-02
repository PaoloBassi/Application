package it.unozerouno.givemetime.controller.fetcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.common.api.Result;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;

import it.unozerouno.givemetime.model.CalendarModel;
import it.unozerouno.givemetime.model.UserKeyRing;
import it.unozerouno.givemetime.utils.AsyncTaskWithListener;
import it.unozerouno.givemetime.utils.TaskListener;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
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
	
	
	
	
	/**
	 * Overview of the possible actions performable from CalendarFetcher
	 * @author Edoardo Giacomello <edoardo.giacomello1990@gmail.com>
	 *
	 */
	public static class Actions{
		public static final int NO_ACTION = -1;
		public static final int LIST_CALENDARS = 0;
		public static final int CALENDARS_TO_MODEL = 1;
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
		//...
	}
	public static class Results{
		public static String[] RESULT_OK = {"OK"};
		public static String[] RESULT_ERROR = {"ERROR"};
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
		case Actions.LIST_CALENDARS:
			getCalendars(projection);
			break;
		case Actions.CALENDARS_TO_MODEL:
			calendarList = getCalendarsModel();
			setResult(Results.RESULT_OK);
			break;
		
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
	 * Fetch calendar list and returns each result to the TaskListener attached to CalendarFetcher
	 * @param projection: Coloumn names 
	 * @see Calendars
	 */
	private void getCalendars(String[] projection){
	// Run query
	Cursor cur = null;
	ContentResolver cr = caller.getContentResolver();
	Uri uri = Calendars.CONTENT_URI;   
	
	//For Identifying as SyncAdapter, User must already be logged)
	//uri = asSyncAdapter(uri, UserKeyRing.getUserEmail(caller), CalendarContract.ACCOUNT_TYPE_LOCAL);
	
	
	// Submit the query and get a Cursor object back. 
	cur = cr.query(uri, projection, null, null, null);
	
	// Use the cursor to step through the returned records
	while (cur.moveToNext()) {
	   String[] result = new String[projection.length];
	   for (int i = 0; i < result.length; i++) {
		result[i] = cur.getString(i);
	}
	   //Provide result to TaskListener
	   setResult(result);
	}
	}
	
	/**
	 * Fetches Calendar List from CalendarAPI instead of CalendarProvider
	 */
	private void calendarAPI(){
		try {
		Calendar client= ApiController.getCalendarClient();
			String pageToken = null;
		do {
		  CalendarList calendarList;
		  calendarList = client.calendarList().list().setPageToken(pageToken).execute();
		  List<CalendarListEntry> items = calendarList.getItems();

		  for (CalendarListEntry calendarListEntry : items) {
		    System.out.println(calendarListEntry.getSummary());
		  }
		  pageToken = calendarList.getNextPageToken();
		} while (pageToken != null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * This function fetches the calendars managing needed projections in order to provide a ready-to-use CalendarModel list.
	 */
	private List<CalendarModel> getCalendarsModel(){
		List<CalendarModel> calendarList = new ArrayList<CalendarModel>();
		String[] projection = Projections.CALENDAR_ID_OWNER_NAME_COLOUR;
		// Run query
		Cursor cur = null;
		ContentResolver cr = caller.getContentResolver();
		Uri uri = Calendars.CONTENT_URI;   
		
		//For Identifying as SyncAdapter, User must already be logged)
		//uri = asSyncAdapter(uri, UserKeyRing.getUserEmail(caller), CalendarContract.ACCOUNT_TYPE_LOCAL);
		
		
		// Submit the query and get a Cursor object back. 
		cur = cr.query(uri, projection, null, null, null);
		
		// Use the cursor to step through the returned records
		while (cur.moveToNext()) {
		   CalendarModel newCalendar = new CalendarModel(cur.getString(0),cur.getString(1),cur.getString(2), Integer.parseInt(cur.getString(3)));
		   calendarList.add(newCalendar);
		}
		return calendarList;
		}
	public static List<CalendarModel> getCalendarList(){
		return calendarList;
	}
	}
