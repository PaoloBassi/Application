package it.unozerouno.givemetime.controller.fetcher.sample;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.model.UserKeyRing;
import it.unozerouno.givemetime.utils.TaskListener;

import java.util.ArrayList;

import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

public class UnifiedController extends Activity {

	String mEmail; // Received from newChooseAccountIntent(); passed to getToken()
	static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
	static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;
	private static final String SCOPE_PROFILE ="https://www.googleapis.com/auth/userinfo.profile";
	private static final String SCOPE_CALENDAR = "https://www.googleapis.com/auth/calendar";
	private static ArrayList<String> scopes;
	private static GoogleAccountCredential credential;
	private static com.google.api.services.calendar.Calendar calendarClient;
	private static final HttpTransport transport = AndroidHttp.newCompatibleTransport();
	private static final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
	private static boolean initCalendar = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.auth_controller_dialog);
		scopes = new ArrayList<String>();
		//Get stored data if present
		mEmail = UserKeyRing.getUserEmail(this);
		
		//TODO: Scope Management
		if (getIntent().getAction() == "FULL_INIT") {
			scopes.add(SCOPE_PROFILE);
			initCalendar = true;
			scopes.add(SCOPE_CALENDAR);
		} 
		login();
	}
	
	
	/**
	 * Has to be called only after user il logged
	 */
	private void apiInit(){
		if(initCalendar){initializeCalendarClient();}
		this.setResult(RESULT_OK);
		finish();
	}
	
	/**
	 * When this code executes, a dialog appears for the user to pick an account. When the user selects the account, your activity receives the result in the onActivityResult() callback.
	 * NOTE:
	 * 
	 */
	private void pickUserAccount() {
	    String[] accountTypes = new String[]{"com.google"};
	    /* These parameters indicate that:
	     * There is no currently selected account.
	     * There is no restricted list of accounts.
	     * The dialog should list only accounts from the "com.google" domain.
	     * Don't prompt the user to pick an account if there's only one available account (just use that one). However, even if only one account currently exists, the dialog may include an option for the user to add a new account.
	     * There is no custom title for the dialog.
	     * There is no specific auth token type required.
	     * There are no restrictions based on account features.
	     * There are no authenticator-specific options.
	     */
	    Intent intent = AccountPicker.newChooseAccountIntent(null, null,
	            accountTypes, false, null, null, null, null);
	    startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
	}
	
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
	        // Receiving a result from the AccountPicker
	        if (resultCode == RESULT_OK) {
	            mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
	            //Store user email in UserKeyRing 
	            UserKeyRing.setUserEmail(this, mEmail);
	            // With the account name acquired, go get the auth token
	            login();
	        } else if (resultCode == RESULT_CANCELED) {
	            // The account picker dialog closed without selecting an account.
	            // Notify users that they must pick an account to proceed.
	            Toast.makeText(this, R.string.pick_account, Toast.LENGTH_SHORT).show();
	        }
	        
	    } else if ((requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR)
	            && resultCode == RESULT_OK) {
	        // Receiving a result that follows a GoogleAuthException, try auth again
	        login();
	    }
	}
	/**
	 * Attempts to retrieve the username.
	 * If the account is not yet known, invoke the picker. Once the account is known,
	 * start an instance of the AsyncTask to get the auth token and do work with it.
	 */
	private void login() {
	    if (mEmail == null) {
	        pickUserAccount();
	    } else {
	        if (isDeviceOnline()) {
	        	//Debug
	        	System.out.println("Is Online");
	        	//invokeTokenFetcher(); //For using old auth mode
	        	credential = GoogleAccountCredential.usingOAuth2(this, scopes);
	        	credential.setSelectedAccountName(UserKeyRing.getUserEmail(this));
	   		  /*debug*/  System.out.println("Setting credential: " + credential.getSelectedAccountName() + " with scope:" + credential.getScope() );
	        	
	   		  //Login Done, Proceeding with other tasks
	   		  apiInit();
	        } else {
	            Toast.makeText(this, R.string.not_online, Toast.LENGTH_LONG).show();
	        }
	    }
	}
	
	private void invokeTokenFetcher(){
		TokenTask profileTokenTask = new TokenTask(this, mEmail, scopes.toArray(new String[scopes.size()]));
    	//Binding a Listener for getting the result
        profileTokenTask.setListener(new TaskListener<String>(this) {
			@Override
			public void onTaskResult(String... results) {
				//When a token is get, cache it
				for (String token : results) {
					UserKeyRing.setUserToken(UnifiedController.this, token);
					System.out.println("Found Profile Token: " + token);
				}
				//All token are stored successfully
				UnifiedController.this.setResult(RESULT_OK);
				UnifiedController.this.finish();
			}
		});
        
        profileTokenTask.execute();
        
		
	}
	
	private void initializeCalendarClient(){
		// Calendar client
		  
	   	  calendarClient = new com.google.api.services.calendar.Calendar.Builder(transport, jsonFactory, credential).setApplicationName("UnoZeroUno-GiveMeTime/0.1a").build();
		    System.out.println("Calendar API Initialized");
		    initCalendar = false;
	  }	
	public static com.google.api.services.calendar.Calendar getCalendarClient() {
		return calendarClient;
	}
	public boolean isDeviceOnline(){
		 ConnectivityManager connMgr = (ConnectivityManager) 
			        getSystemService(Context.CONNECTIVITY_SERVICE);
			    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			    if (networkInfo != null && networkInfo.isConnected()) {
			        return true;
			    } else {
			       return false;
			    }
	}
	
	/**
	 * This method is a hook for background threads and async tasks that need to
	 * provide the user a response UI when an exception occurs.
	 */
	public void handleException(final Exception e) {
	    // Because this call comes from the AsyncTask, we must ensure that the following
	    // code instead executes on the UI thread.
	    runOnUiThread(new Runnable() {
	        @Override
	        public void run() {
	            if (e instanceof GooglePlayServicesAvailabilityException) {
	                // The Google Play services APK is old, disabled, or not present.
	                // Show a dialog created by Google Play services that allows
	                // the user to update the APK
	                int statusCode = ((GooglePlayServicesAvailabilityException)e)
	                        .getConnectionStatusCode();
	                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,UnifiedController.this,
	                        REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
	                dialog.show();
	            } else if (e instanceof UserRecoverableAuthException) {
	                // Unable to authenticate, such as when the user has not yet granted
	                // the app access to the account, but the user can fix this.
	                // Forward the user to an activity in Google Play services.
	                Intent intent = ((UserRecoverableAuthException)e).getIntent();
	                startActivityForResult(intent,
	                        REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
	            }
	        }
	    });
	}
	
}
