package it.unozerouno.givemetime.view.utilities;

import it.unozerouno.givemetime.model.UserKeyRing;
import android.content.Context;


public class LoginPreferences {
    /*
    return true if it is the first time the app has been launched, false otherwise
     */
    public static boolean isFirst(Context context){
       // if the precedence is true, set it to false and then return it
    	boolean first = UserKeyRing.isFirstTimeLogin(context);
        if(first){
            UserKeyRing.setFirstLogin(context, false);
        }
        return first;
    }
}
