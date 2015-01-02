package it.unozerouno.givemetime.view.utilities;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.controller.fetcher.ApiController;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

public class ApiLoginInterface extends FragmentActivity implements ConnectionCallbacks, OnConnectionFailedListener {
	public class RequestCode{
		public static final int LOGIN = 0;
	}
	public class ResultCode{
		public static final int DONE = 0;
		public static final int NOT_CONNECTED =1;
	}
	private ApiController apiController; //This controls active APIs
	private Boolean mResolvingError = false; //true if this activity is already attempting to resolve a connection error
    private static final int REQUEST_RESOLVE_ERROR = 1001; // Request code to use when launching the resolution activity
    private static final String DIALOG_ERROR = "dialog_error";  // Unique tag for the error dialog fragment
    private static final String STATE_RESOLVING_ERROR = "resolving_error"; //Used when activity restarts while resolving an error (due, for example, to screen rotation)
	private ProgressBar progressBar;
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.dialog_login);
		initApiController();
		showProgressBar();
		apiController.connect();
		//Restore the state from previous instances
		 mResolvingError = savedInstanceState != null
		            && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);
	}
    
    private void initApiController(){

    	//Instantiating a new ApiController
    			apiController = new ApiController(this, this, this);
    			apiController.useDefaultApi();
    			apiController.initializePlayServicesClient();
    }
    
    
    private void showProgressBar(){
    	progressBar = (ProgressBar) findViewById(R.id.login_progressbar);
    	progressBar.setVisibility(View.VISIBLE);
    }
    /**
     * Stores the user data in UserKeyRing
     */
    private void getUserData(){
    	if(!apiController.storeUserProfile()){
    		setResult(ResultCode.NOT_CONNECTED);
    	} else
    		setResult(ResultCode.DONE);
    }
    
   
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		 if (mResolvingError) {
	            // Already attempting to resolve an error.
	            return;
	        } else if (result.hasResolution()) {
	            try {
	                mResolvingError = true;
	                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
	            } catch (SendIntentException e) {
	                // There was an error with the resolution intent. Try again.
	            	apiController.connect();
	            }
	        } else {
	            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
	            showErrorDialog(result.getErrorCode());
	            mResolvingError = true;
	        }
		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
	    
	}

	@Override
	public void onConnected(Bundle arg0) {
		System.out.println("Play sercives Connected!");
		setResult(ResultCode.DONE);
		//Now fetching user Data
		getUserData();
		//Now that we have the user profile, we can initialize other APIs
		apiController.initializeOtherApis();
		finish();
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		System.out.println("Play services suspended");
		
	}
	
	
	@Override
	/*Once the user completes the resolution provided by startResolutionForResult() or GooglePlayServicesUtil.getErrorDialog(), 
	 * your activity receives the onActivityResult() callback with the RESULT_OK result code. You can then call connect() again. 
	 * (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int, android.content.Intent)
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_RESOLVE_ERROR) {
	        mResolvingError = false;
	        if (resultCode == RESULT_OK) {
	            // Make sure the app is not already connected or attempting to connect
	            if (!ApiController.isConnecting() &&
	                    !ApiController.isConnected()) {
	                apiController.connect();
	            }
	        }
	    }
	}
	
	
	
	

	
	   // The rest of this code is all about building the error dialog

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode,
                    this.getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((ApiLoginInterface)getActivity()).onDialogDismissed();
        }
    }
    
    
}
