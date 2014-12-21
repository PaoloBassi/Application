package it.unozerouno.givemetime.controller.fetcher;

import java.util.Collections;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.calendar.CalendarScopes;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;
/**
 * Check if google play services are installed and the user login
 * @author Edoardo Giacomello
 *
 */
public class PlayController extends Activity {
	private static final int REQUEST_GOOGLE_PLAY_SERVICES = 0;
	private static final int REQUEST_ACCOUNT_PICKER = 1;
	private static final String PREFERRED_ACCOUNT_NAME = "DefaultAccount";
	private static GoogleAccountCredential credential;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		  // Google Accounts
		  if(credential == null){
			  credential = GoogleAccountCredential.usingOAuth2(this, Collections.singleton(CalendarScopes.CALENDAR));
		  }
		    SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
		    credential.setSelectedAccountName(settings.getString(PREFERRED_ACCOUNT_NAME, null));
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    if (checkGooglePlayServicesAvailable()) {
	      haveGooglePlayServices();
	    }
	  }
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		
		 case REQUEST_GOOGLE_PLAY_SERVICES:
		        if (resultCode == Activity.RESULT_OK) {
		          haveGooglePlayServices();
		        } else {
		          checkGooglePlayServicesAvailable();
		        }
		        break;
		      case REQUEST_ACCOUNT_PICKER:
		        if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
		          String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
		          if (accountName != null) {
		            credential.setSelectedAccountName(accountName);
		            SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
		            SharedPreferences.Editor editor = settings.edit();
		            editor.putString(PREFERRED_ACCOUNT_NAME, accountName);
		            editor.commit();
		            Toast toast = Toast.makeText(getApplicationContext(), "Account set", Toast.LENGTH_LONG);
		            setResult(RESULT_OK);
			    	toast.show();
		          }
		        }
		        break;

		default:
			break;
		}
	}
	
	/** Check that Google Play services APK is installed and up to date. */
	  private boolean checkGooglePlayServicesAvailable() {
	    final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
	      showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
	      return false;
	    }
	    return true;
	  }
	  
	  private void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
		    runOnUiThread(new Runnable() {
		      public void run() {
		        Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
		            connectionStatusCode, PlayController.this, REQUEST_GOOGLE_PLAY_SERVICES);
		        dialog.show();
		      }
		    });
		  }
	  
	  private void haveGooglePlayServices() {
		    // check if there is already an account selected
		    if (credential.getSelectedAccountName() == null) {
		      // ask user to choose account
		      chooseAccount();
		    } else {
		    	Toast toast = Toast.makeText(getApplicationContext(), "Account Set", Toast.LENGTH_LONG);
		    	 setResult(RESULT_OK);
		    	toast.show();
		    }
		  }
	
	  private void chooseAccount() {
		    startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
		  }
	public static GoogleAccountCredential getCredential() {
		return credential;
	}
}
