package it.unozerouno.givemetime.controller.fetcher.old;

import it.unozerouno.givemetime.controller.fetcher.TokenTask;
import it.unozerouno.givemetime.model.UserKeyRing;
import it.unozerouno.givemetime.utils.TaskListener;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
/**
 * This Task initializes API Clients and generate relative tokens storing them in UserKeyRing
 * You have to provide an Intent as parameter, specifying one of the following actions:
 * -"API_INIT": initialize all API clients and relative tokens for the application
 * -"CALENDAR_INIT": initialize calendar API and relative token
 * 
 * Note that you can select multiple Actions providing multiple intents as execute() parameters.
 * Is it possible to manage a progressbar provided to the constructor
 * @author edoardo
 *
 */
public final class ApiController extends AsyncTask<Intent, ProgressBar,Void> {
	
	private static com.google.api.services.calendar.Calendar calendarClient;
	private final String CALENDAR_SCOPE_URI = "https://www.googleapis.com/auth/calendar";
	private ArrayList<String> scopeList;
	final HttpTransport transport = AndroidHttp.newCompatibleTransport();
	final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
	
	//these flags indicate which apis are to activate, since tokens must be fetched on the UI thread
	private boolean initCalendars = false;
	
	private Activity caller;
	private ProgressBar progressBar;

	 
	  private void initializeCalendarClient(){
		// Calendar client
		  GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(caller, scopeList);
		  credential.setSelectedAccountName(UserKeyRing.getUserEmail(caller));
		  System.out.println("Setting credential: " + credential.getSelectedAccountName() + " with scope:" + credential.getScope() );
		  calendarClient = new com.google.api.services.calendar.Calendar.Builder(transport, jsonFactory, credential).setApplicationName("UnoZeroUno-GiveMeTime/0.1a").build();
		    System.out.println("Calendar API Initialized");
	  }
	  
	  //Unused
	  private void initializeCalendarToken(){
		  TokenTask calendarTokenTask = new TokenTask(caller, UserKeyRing.getUserEmail(caller), scopeList.toArray(new String[scopeList.size()]));
		  calendarTokenTask.setListener(new TaskListener<String>(caller) {
			@Override
			public void onTaskResult(String... results) {
				for (String token : results) {
					//Save Token on UserKeyRing
					UserKeyRing.setCalendarToken(caller, token);
					System.out.println("Found Calendar Token: " + token);
				}
			}
		});
		  calendarTokenTask.execute();
	  }
	   
	  
	public static com.google.api.services.calendar.Calendar getCalendarClient() {
		return calendarClient;
	}
		
	public ApiController(Activity caller, ProgressBar progressBar) {
		this.caller = caller;
		this.progressBar = progressBar;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		scopeList = new ArrayList<String>();
		
		progressBar.setVisibility(View.VISIBLE);
		
		
	}

	@Override
	protected Void doInBackground(Intent... intents) {
		for (Intent currentIntent : intents) {
			String action = currentIntent.getAction();
			if ((action =="API_INIT" )||(action =="CALENDAR_INIT")){
				//Mark "initialize calendar" as work to be done. Actual call to initializeCalendar() will be place on postExecute method.
				initCalendars = true;
				scopeList.add(CALENDAR_SCOPE_URI);
			}
		}
		return null;
	}
	@Override
	protected void onPostExecute(Void noParam) {
		super.onPostExecute(noParam);
		
				
		//Starting Login Process
				System.out.println("Login attempt");
		    	Intent loginIntent = new Intent(caller, AccountController.class);
		    	loginIntent.putExtra("scopes", scopeList.toArray(new String[scopeList.size()]));
		    	loginIntent.setAction("ACCOUNT_LOGIN");
		    	caller.startActivity(loginIntent);
		
		
		
		if (initCalendars){
			initializeCalendarClient();
		}
		
		
		//Hiding Progressbar from caller
		if(progressBar!=null){
		progressBar.setVisibility(View.INVISIBLE);
		}
	}
}
