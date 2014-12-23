package it.unozerouno.givemetime.controller.fetcher.sample;

import it.unozerouno.givemetime.utils.AsyncTaskWithListener;

import java.io.IOException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class TokenTask extends AsyncTaskWithListener<Void, Exception, String>{
    Activity mActivity;
    String mScope;
    String mEmail;

    public TokenTask(Activity activity, String accountEmail, String... scopeUrls /*Scopes Without oauth:*/) {
        this.mActivity = activity;
        setScopes(scopeUrls);
        this.mEmail = accountEmail;
    }
    
    public void setScopes(String... scopeUrls){
    	StringBuffer scopes = new StringBuffer();
    	scopes.append("oauth2:");
    	for (String currentScope : scopeUrls) {
			scopes.append(currentScope);
			scopes.append(" ");
		}
    	scopes.deleteCharAt(scopes.lastIndexOf(" "));
    	mScope = scopes.toString();
    }

    /**
     * Executes the asynchronous job. This runs when you call execute()
     * on the AsyncTask instance.
     */
    @Override
    protected String doInBackground(Void... params) {
        try {
            String token = fetchToken();
            if (token != null) {
                //Token Received, sending back to caller
            	setResult(token);
            }
        } catch (IOException e) {
            // The fetchToken() method handles Google-specific exceptions,
            // so this indicates something went wrong at a higher level.
            // TIP: Check for network connectivity before starting the AsyncTask.
          
        }
        return null;
    }

    /**
     * Gets an authentication token from Google and handles any
     * GoogleAuthException that may occur.
     */
    protected String fetchToken() throws IOException {
        try {
        	System.out.println("Trying to get Token for " + mEmail + " with scope " +mScope);
            return GoogleAuthUtil.getToken(mActivity, mEmail, mScope);
        } catch (UserRecoverableAuthException userRecoverableException) {
            // GooglePlayServices.apk is either old, disabled, or not present
            // so we need to show the user some UI in the activity to recover.
           publishProgress(userRecoverableException);
        } catch (GoogleAuthException fatalException) {
            // Some other type of unrecoverable exception has occurred.
            // Report and log the error as appropriate for your app.
            //TODO: Manage this exception
        	fatalException.printStackTrace();
        }
        return null;
    }
    
   @Override
protected void onProgressUpdate(Exception... values) {
	// TODO Auto-generated method stub
	super.onProgressUpdate(values);
	for (Exception exception : values) {
		exception.printStackTrace();
		handleException(exception);
	}
}
   
   /**
	 * This method is a hook for background threads and async tasks that need to
	 * provide the user a response UI when an exception occurs.
	 */
	public void handleException(final Exception e) {
		final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;
	    // Because this call comes from the AsyncTask, we must ensure that the following
	    // code instead executes on the UI thread.
	            if (e instanceof GooglePlayServicesAvailabilityException) {
	                // The Google Play services APK is old, disabled, or not present.
	                // Show a dialog created by Google Play services that allows
	                // the user to update the APK
	                int statusCode = ((GooglePlayServicesAvailabilityException)e)
	                        .getConnectionStatusCode();
	                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode, mActivity, REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
	                dialog.show();
	            } else if (e instanceof UserRecoverableAuthException) {
	                // Unable to authenticate, such as when the user has not yet granted
	                // the app access to the account, but the user can fix this.
	                // Forward the user to an activity in Google Play services.
	                Intent intent = ((UserRecoverableAuthException)e).getIntent();
	                mActivity.startActivityForResult(intent, REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
	            }
	        }

    
}
