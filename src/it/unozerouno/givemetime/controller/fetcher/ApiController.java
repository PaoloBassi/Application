package it.unozerouno.givemetime.controller.fetcher;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.InputFilter.LengthFilter;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
/**
 * This class manages oAuth and clients for all the APIs used by GiveMeTime
 * You can initialize API Clients and accounts by specifying "API_INIT" as Action
 * Is it possible to manage a progressbar 
 * @author edoardo
 *
 */
public final class ApiController extends AsyncTask<Intent, ProgressBar, String> {
	
	private static com.google.api.services.calendar.Calendar calendarClient;
	final HttpTransport transport = AndroidHttp.newCompatibleTransport();
	final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
	private Activity caller;
	private ProgressBar progressBar;

	  
	  private void initializeApis(){
		
		    // Calendar client
		    calendarClient = new com.google.api.services.calendar.Calendar.Builder(
		        transport, jsonFactory, PlayController.getCredential()).setApplicationName(caller.getApplicationInfo().name)
		        .build();
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
		progressBar.setVisibility(View.VISIBLE);
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(Intent... params) {
		if (params[1].getAction()=="API_INIT"){
		initializeApis();
		return "OK";
		}
		return "ERROR";
		
		
	}
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		progressBar.setVisibility(View.INVISIBLE);
		Toast toast = Toast.makeText(caller.getApplicationContext(), result, Toast.LENGTH_SHORT);
		toast.show();
	}
}
