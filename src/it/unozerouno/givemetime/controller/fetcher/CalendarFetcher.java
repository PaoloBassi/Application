package it.unozerouno.givemetime.controller.fetcher;

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
 * Please specify a task with setAction() before calling .execute(), you can chose them from CalendarFetcher.Actions
 * You also have to specify the query projection when calling execute(), you can pick one from CalendarFetcher.Projections or specify your own.
 * Results are returned to attached TaskListener (use setListener)
 * can also manage a progressBar
 * @author <edoardo.giacomello1990@gmail.com>
 * @see TaskListener
 */
public class CalendarFetcher extends AsyncTaskWithListener<String, Void, String[]> {
	//NOTICE: For the developers - When adding new functions, please comply with the present structure.
	//I.E: Adding an actions: provide "Actions" integer with relative projections (if needed)
	//Add "case" in doInBackground()
	//Compute result in a separate function. See "getCalendarlist" for example.
	//Don't forget to call "setResult" whenever a single result (row) is ready!
	
	/**
	 * Overview of the possible actions performable from CalendarFetcher
	 * @author Edoardo Giacomello <edoardo.giacomello1990@gmail.com>
	 *
	 */
	public static class Actions{
		public static final int NO_ACTION = -1;
		public static final int LIST_CALENDARS = 0;
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
		//Event related
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
		case Actions.LIST_CALENDARS:
			getCalendars(projection);
			break;
		//Add here new actions
		default:
			break;
		}
		
		return null;
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

	

	

	
	
	
}
