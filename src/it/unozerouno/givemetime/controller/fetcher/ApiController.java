
package it.unozerouno.givemetime.controller.fetcher;

import it.unozerouno.givemetime.model.UserKeyRing;
import it.unozerouno.givemetime.utils.GiveMeLogger;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.Contacts.People;

import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.plus.Account;
import com.google.android.gms.plus.People.LoadPeopleResult;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.Person.Name;
import com.google.android.gms.plus.model.people.PersonBuffer;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;

public class ApiController {
	
	private static GoogleApiClient playServicesApiClient; //This Client manages all play services APIs
	private static Calendar calendarClient; //Calendar API Client
	private static GoogleAccountCredential credential; //Manages credentials for non-PlayService APIs
	final HttpTransport transport = AndroidHttp.newCompatibleTransport();
	final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
	
	private Activity caller;
	private ConnectionCallbacks connectionCallBacks;
	private OnConnectionFailedListener connectionFailedListener;
	private static List<Api> apiToUse; //Api to use in PlayServices client
	private static List<Scope> playServicesScopes;
		
	public ApiController(Activity caller, ConnectionCallbacks connectionCallBacks, OnConnectionFailedListener connectionFailedListener) {
		this.caller = caller;
		this.connectionCallBacks = connectionCallBacks;
		this.connectionFailedListener = connectionFailedListener;
		if(apiToUse == null){apiToUse = new ArrayList<Api>();
		playServicesScopes = new ArrayList<Scope>();
		}
		
		
	}
	
	public GoogleApiClient getApiClient(){
		return playServicesApiClient;
	}
	/**
	 * Use all API requested to GiveMeTime
	 */
	public void useDefaultApi(){
		apiToUse = new ArrayList<Api>();
		apiToUse.add(Plus.API);
		playServicesScopes.add(Plus.SCOPE_PLUS_PROFILE);
		playServicesScopes.add(Plus.SCOPE_PLUS_LOGIN);
		playServicesScopes.add(new Scope("https://www.googleapis.com/auth/calendar"));
	}
	/**
	 * Adds custom API to list
	 * @param api
	 */
	public void addPlayServicesApi(Api api){
		apiToUse.add(api);
	}
	/**
	 * Adds custom scopes to list (create a new Scope(Scopes.SOMETHING))
	 * @param scope
	 */
	public void addPlayServicesScope(Scope scope){
		playServicesScopes.add(scope);
	}
	
	/**
	 * begin connection of ApiClient.
	 * @return
	 */
	public boolean connect(){
		if (playServicesApiClient != null) {
			playServicesApiClient.connect();
			return true;
		}
		else 
			return false;
	}
	
	public static boolean isConnecting(){
		if(playServicesApiClient != null){
			return playServicesApiClient.isConnecting();
		} else return false;
	}
	public static boolean isConnected(){
		if(playServicesApiClient != null){
			return playServicesApiClient.isConnected();
		} else return false;
	}
	/**
	 * Disconnects ApiClient.
	 * @return
	 */
	public static boolean disconnect(){
		if (playServicesApiClient != null){
			playServicesApiClient.disconnect();
			return true;
		}	else
			return false;
	}
	
	@SuppressWarnings({ "unchecked" })
	public void initializePlayServicesClient(){
		Builder apiClientBuilder = new GoogleApiClient.Builder(caller, connectionCallBacks, connectionFailedListener);
		//Adding selected apis
		for (Api currentApi : apiToUse) {
			apiClientBuilder.addApi(currentApi);
		}
		//Adding selected scopes
		for (Scope scope : playServicesScopes) {
			apiClientBuilder.addScope(scope);
		}
		//Building PlayServices client
		playServicesApiClient = apiClientBuilder.build();
	}

	public boolean storeUserProfile() {
		
		if (playServicesApiClient.isConnected()){
					String userAccountName = Plus.AccountApi.getAccountName(playServicesApiClient);
					UserKeyRing.setUserEmail(caller, userAccountName);
					Plus.PeopleApi.load(playServicesApiClient, "me").setResultCallback(new ResultCallback<LoadPeopleResult>() {
						
						@Override
						public void onResult(LoadPeopleResult result) {
							if (result == null) {System.out.println("null..");}
							
							else
							{	
								PersonBuffer pbuffer = result.getPersonBuffer();
								if(pbuffer != null) {Person me = pbuffer.get(0);
								if(me != null){
							 	Name userName = me.getName();
								UserKeyRing.setUserName(caller, userName.getGivenName());
								UserKeyRing.setUserSurname(caller, userName.getFamilyName());
								}
								}
							}		
						}
					});
			
			
			
			return true;
		}
		else return false;	
	}

	/**
	 * Initializes non-PlayServices API (i.e. Calendars). Since a valid account has to be selected in order to initialize them, this function MUST BE CALLED AFTER "storeUserProfile()"
	 */
	public void initializeOtherApis(){
		String email = UserKeyRing.getUserEmail(caller);
		//Setting up credentials for non-PlayServices APIs
		credential = GoogleAccountCredential.usingOAuth2(caller, Collections.singleton(CalendarScopes.CALENDAR)).setSelectedAccountName(email);
		for (android.accounts.Account currentAccount : credential.getGoogleAccountManager().getAccountManager().getAccounts()) {
			GiveMeLogger.log("Name: " + currentAccount.name + " type: " + currentAccount.type);
		}
	//Building Clients
	calendarClient = new com.google.api.services.calendar.Calendar.Builder(transport, jsonFactory, credential).setApplicationName("UnoZeroUno-GiveMeTime/1.0").build();
	}
	
	public static Calendar getCalendarClient(){
		return calendarClient;
	}
	
	
	public boolean isDeviceOnline(){
		 ConnectivityManager connMgr = (ConnectivityManager) 
			        caller.getSystemService(Context.CONNECTIVITY_SERVICE);
			    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			    if (networkInfo != null && networkInfo.isConnected()) {
			        return true;
			    } else {
			       return false;
			    }
	}
	
}
