package it.unozerouno.givemetime.controller.fetcher;




import it.unozerouno.givemetime.controller.fetcher.sample.UnifiedController;
import it.unozerouno.givemetime.model.UserKeyRing;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Debug;
import android.widget.ListView;
import android.widget.Toast;




public final class CalendarFetcher extends AsyncTask<Intent, Exception, Boolean> {
	private ListView calendarList;
	private Activity caller;
	private static com.google.api.services.calendar.Calendar calendarClient;
	private String response;
	public CalendarFetcher(Activity caller, ListView calendarList) {
		this.caller = caller;
		this.calendarList = calendarList;
	}
	@Override
	protected Boolean doInBackground(Intent... intent) {
		calendarClient = UnifiedController.getCalendarClient();
		if (calendarClient == null) {
			response = "Error: Calendar Client has not been initialized";
			return false;
		}
		
		for (Intent currentIntent : intent) {
			if (currentIntent.getAction()=="FETCH_CALENDARS"){
				fetchCalendars();
			}
		}
		return true;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if (response != null){
		Toast responseToast = Toast.makeText(caller, response, Toast.LENGTH_SHORT);
    	responseToast.show();
		}
	}
	
	
	private void fetchCalendars(){
		try {
		String pageToken = null;
			do {
			  CalendarList calendarFetchedList;
			  calendarFetchedList = calendarClient.calendarList().list().setPageToken(pageToken).execute();
			
			  List<CalendarListEntry> items = calendarFetchedList.getItems();
			  for (CalendarListEntry calendarListEntry : items) {
				  System.out.println("I've Fetched SOmething!!");
				  response = response + calendarListEntry.getSummary();
			  }
			  pageToken = calendarFetchedList.getNextPageToken();
			} while (pageToken != null);
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			publishProgress(e);
		}
		
	}
	
	
	/**
	 * This will send to the UI all exceptions generated during the process
	 */
	@Override
	protected void onProgressUpdate(Exception... values) {
		super.onProgressUpdate(values);
		UnifiedController handler = new UnifiedController();
		for (Exception exception : values) {
			handler.handleException(exception);
		}
		
		
	}
	
	private void saveToLocalDB(){
		
	}
	private void updateRemoteCalendar(){
		
	}
	
	  
	}
