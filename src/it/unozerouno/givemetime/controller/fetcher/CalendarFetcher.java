package it.unozerouno.givemetime.controller.fetcher;




import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.Toast;




public final class CalendarFetcher extends AsyncTask<Intent, Void, Boolean> {
	private ListView calendarList;
	private Activity caller;
	private static com.google.api.services.calendar.Calendar calendarClient;
	public CalendarFetcher(Activity caller, ListView calendarList) {
		this.caller = caller;
		this.calendarList = calendarList;
	}
	@Override
	protected Boolean doInBackground(Intent... intent) {
		GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(caller.getApplicationContext(), Collections.singleton(CalendarScopes.CALENDAR));
		calendarClient = ApiController.getCalendarClient();
		if (calendarClient == null) {
			Toast error = Toast.makeText(caller.getApplicationContext(), "Error: Calendar Client has not been initialized", Toast.LENGTH_LONG);
			error.show();
			cancel(true);
			return false;
		}
		
		for (Intent currentIntent : intent) {
			if (currentIntent.getAction()=="FETCH_CALENDARS"){
				fetchCalendars();
			}
		}
		return true;
	}
	
	private void fetchCalendars(){
		try {
		String pageToken = null;
			do {
			  CalendarList calendarFetchedList;
			  calendarFetchedList = calendarClient.calendarList().list().setPageToken(pageToken).execute();
			
			  List<CalendarListEntry> items = calendarFetchedList.getItems();
			  for (CalendarListEntry calendarListEntry : items) {
				  Toast result = Toast.makeText(caller, calendarListEntry.getSummary(), Toast.LENGTH_LONG);
				  result.show();
			  }
			  pageToken = calendarFetchedList.getNextPageToken();
			} while (pageToken != null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void saveToLocalDB(){
		
	}
	private void updateRemoteCalendar(){
		
	}
	
	  
	}
