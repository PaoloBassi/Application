package it.unozerouno.givemetime.controller.fetcher.old;

import java.util.ArrayList;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.controller.fetcher.TokenTask;
import it.unozerouno.givemetime.model.UserKeyRing;
import it.unozerouno.givemetime.utils.TaskListener;

import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

/**
 * This Activity manages Google Account Login and storage of the user email in UserKeyRing.
 * Also the relative token is generated, saved in UserKeyRing and returned by Result
 * Legal Intent Actions:
 *  - "ACCOUNT_LOGIN": Perform a request for logging a user. If no account is stored or there are more then 2 account from which chose it shows an Account Picker. 
 * @author Edoardo Giacomello
 *
 */
public class AccountController extends Activity{
	
	String mEmail; // Received from newChooseAccountIntent(); passed to getToken()
	static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
	static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;
	private static final String SCOPE_PROFILE ="https://www.googleapis.com/auth/userinfo.profile";
	private static ArrayList<String> scopes;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Get cached data if present
		mEmail = UserKeyRing.getUserEmail(this);
		scopes = new ArrayList<String>();
		scopes.add(SCOPE_PROFILE);
		if (getIntent().getAction()=="ACCOUNT_LOGIN"){
			if (getIntent().hasExtra("scopes")){
			for (String currentScope : getIntent().getExtras().getStringArray("scopes")) {
				scopes.add(currentScope);
			}
			}
			getUsername();
		}
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
	            getUsername();
	        } else if (resultCode == RESULT_CANCELED) {
	            // The account picker dialog closed without selecting an account.
	            // Notify users that they must pick an account to proceed.
	            Toast.makeText(this, R.string.pick_account, Toast.LENGTH_SHORT).show();
	        }
	        
	    } else if ((requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR)
	            && resultCode == RESULT_OK) {
	        // Receiving a result that follows a GoogleAuthException, try auth again
	        getUsername();
	    }
	}
	/**
	 * Attempts to retrieve the username.
	 * If the account is not yet known, invoke the picker. Once the account is known,
	 * start an instance of the AsyncTask to get the auth token and do work with it.
	 */
	private void getUsername() {
	    if (mEmail == null) {
	        pickUserAccount();
	    } else {
	        if (isDeviceOnline()) {
	        	//Debug
	        	System.out.println("Is Online");
	        	TokenTask profileTokenTask = new TokenTask(this, mEmail, scopes.toArray(new String[scopes.size()]));
	        	//Binding a Listener for getting the result
	            profileTokenTask.setListener(new TaskListener<String>(this) {
					@Override
					public void onTaskResult(String... results) {
						//When a token is get, cache it
						for (String token : results) {
							UserKeyRing.setUserToken(AccountController.this, token);
							System.out.println("Found Profile Token: " + token);
						}
						//All token are stored successfully
						AccountController.this.setResult(RESULT_OK);
						AccountController.this.finish();
					}
				});
	            
	            profileTokenTask.execute();
	            
	        
	        } else {
	            Toast.makeText(this, R.string.not_online, Toast.LENGTH_LONG).show();
	        }
	    }
	}
	
	/**
	 * checks whether the Device is connected to Internet
	 * @return
	 */
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
	
	
}


