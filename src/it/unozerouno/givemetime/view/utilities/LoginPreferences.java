package it.unozerouno.givemetime.view.utilities;

import it.unozerouno.givemetime.model.UserKeyRing;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class LoginPreferences {
    /**
    return true if it is the first time the app has been launched, false otherwise
     */
    public static boolean isFirst(Context context){
    	boolean first = UserKeyRing.isFirstTimeLogin(context);
        return first;
    }
    /**
     * checks if the device is online 
     * @param caller
     * @return 
     */
	public static boolean isDeviceOnline(Activity caller){
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
