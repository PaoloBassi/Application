
package it.unozerouno.givemetime.controller.fetcher;

import it.unozerouno.givemetime.model.UserKeyRing;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
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
import com.google.android.gms.plus.Account;
import com.google.android.gms.plus.People.LoadPeopleResult;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.Person.Name;
import com.google.android.gms.plus.model.people.PersonBuffer;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

public class ApiController {
	
	private static GoogleApiClient apiClient;
	private Activity caller;
	private ConnectionCallbacks connectionCallBacks;
	private OnConnectionFailedListener connectionFailedListener;
	private static List<Api> apiToUse;
	private static List<Scope> scopes;
		
	public ApiController(Activity caller, ConnectionCallbacks connectionCallBacks, OnConnectionFailedListener connectionFailedListener) {
		this.caller = caller;
		this.connectionCallBacks = connectionCallBacks;
		this.connectionFailedListener = connectionFailedListener;
		if(apiToUse == null){apiToUse = new ArrayList<Api>();
		scopes = new ArrayList<Scope>();
		}
		
	}
	
	public GoogleApiClient getApiClient(){
		return apiClient;
	}
	/**
	 * Use all API requested to GiveMeTime
	 */
	public void useDefaultApi(){
		apiToUse = new ArrayList<Api>();
		apiToUse.add(Plus.API);
		scopes.add(Plus.SCOPE_PLUS_PROFILE);
		scopes.add(Plus.SCOPE_PLUS_LOGIN);
	}
	/**
	 * Adds custom API to list
	 * @param api
	 */
	public void addApi(Api api){
		apiToUse.add(api);
	}
	/**
	 * Adds custom scopes to list (create a new Scope(Scopes.SOMETHING))
	 * @param scope
	 */
	public void addScope(Scope scope){
		scopes.add(scope);
	}
	
	/**
	 * begin connection of ApiClient.
	 * @return
	 */
	public boolean connect(){
		if (apiClient != null) {
			apiClient.connect();
			return true;
		}
		else 
			return false;
	}
	
	public static boolean isConnecting(){
		if(apiClient != null){
			return apiClient.isConnecting();
		} else return false;
	}
	public static boolean isConnected(){
		if(apiClient != null){
			return apiClient.isConnected();
		} else return false;
	}
	/**
	 * Disconnects ApiClient.
	 * @return
	 */
	public static boolean disconnect(){
		if (apiClient != null){
			apiClient.disconnect();
			return true;
		}	else
			return false;
	}
	
	@SuppressWarnings({ "unchecked" })
	public void initializeApiClient(){
		Builder apiClientBuilder = new GoogleApiClient.Builder(caller, connectionCallBacks, connectionFailedListener);
		//Adding selected apis
		for (Api currentApi : apiToUse) {
			apiClientBuilder.addApi(currentApi);
		}
		//Adding selected scopes
		for (Scope scope : scopes) {
			apiClientBuilder.addScope(scope);
		}
		apiClient = apiClientBuilder.build();
	}

	public boolean storeUserProfile() {
		
		if (apiClient.isConnected()){
					String userAccountName = Plus.AccountApi.getAccountName(apiClient);
					UserKeyRing.setUserEmail(caller, userAccountName);
					Plus.PeopleApi.load(apiClient, "me").setResultCallback(new ResultCallback<LoadPeopleResult>() {
						
						@Override
						public void onResult(LoadPeopleResult result) {
							if (result == null) {System.out.println("null..");}
							
							else
							{	
								PersonBuffer pbuffer = result.getPersonBuffer();
								Person me = pbuffer.get(0);
							 	Name userName = me.getName();
								UserKeyRing.setUserName(caller, userName.getGivenName());
								UserKeyRing.setUserSurname(caller, userName.getFamilyName());
							}		
						}
					});
			
			
			
			return true;
		}
		else return false;	
	}

	
	
}
