package it.unozerouno.givemetime.controller.fetcher.old;




import it.unozerouno.givemetime.controller.fetcher.UnifiedController;
import it.unozerouno.givemetime.model.UserKeyRing;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.calendar.Calendar;
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




public final class CalendarFetcherOld extends AsyncTask<Intent, Exception, Boolean> {
	private ListView calendarList;
	private Activity caller;
	private static com.google.api.services.calendar.Calendar calendarClient;
	private String response;
	private static final String ROOT_URL =  "https://www.googleapis.com/calendar/v3";
	public CalendarFetcherOld(Activity caller, ListView calendarList) {
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
			//	fetchCalendars();
				try {
					getCalendarListA();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
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
	
	
	private void getCalendarListA() throws IOException{
		System.out.println("Trying to fetch calendars");
		// Initialize Calendar service with valid OAuth credentials
		Calendar service = UnifiedController.getCalendarClient();

		// Iterate through entries in calendar list
		String pageToken = null;
		do {
		  CalendarList calendarList = service.calendarList().list().setPageToken(pageToken).execute();
		  List<CalendarListEntry> items = calendarList.getItems();

		  for (CalendarListEntry calendarListEntry : items) {
		    System.out.println(calendarListEntry.getSummary());
		  }
		  pageToken = calendarList.getNextPageToken();
		} while (pageToken != null);
		System.out.println("Calendar Fetching Done");
	}
	
	private void fetchCalendar(){
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
	 * Tester
	 * @throws IOException
	 * @throws JSONException
	 */
	private void fetchCalendarList() throws IOException, JSONException {
        String token = UserKeyRing.getUserToken(caller);
        if (token == null) {
          // error has already been handled in fetchToken()
          return;
        }
        URL url = new URL(ROOT_URL + "/users/me/calendarList" + "?access_token=" + token);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
          InputStream response = connection.getInputStream();
          String calendarId = ResponseParser.getCalendarId(response);
          System.out.println("Found calendar number:" + calendarId + "!");
          response.close();
          return;
        } else if (responseCode == 401) {
            GoogleAuthUtil.invalidateToken(caller, token);
            System.err.println("Server auth error, please try again.");
            System.out.println("Server auth error: " + ResponseParser.readResponse(connection.getErrorStream()));
            return;
        } else {
          System.err.println("Server returned the following error code: " + responseCode);
          return;
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
	
	private static class ResponseParser {
		/**
	     * Reads the response from the input stream and returns it as a string.
	     */
	    public static String readResponse(InputStream is) throws IOException {
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        byte[] data = new byte[2048];
	        int len = 0;
	        while ((len = is.read(data, 0, data.length)) >= 0) {
	            bos.write(data, 0, len);
	        }
	        return new String(bos.toByteArray(), "UTF-8");
	    }
	    
		  /**
	     * Parses the response and returns the first name of the user.
	     * @throws JSONException if the response is not JSON or if first name does not exist in response
		 * @throws IOException 
	     */
	    private static String getCalendarId(InputStream response) throws JSONException, IOException {
	      JSONObject profile = new JSONObject(readResponse(response));
	      return profile.getString("id");
	    }
		
		
	}
	  
	}
