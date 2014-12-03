package paoledo.app.theunknownepisode.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * This class contains the SharedPreferences to be used to skip the intro pages
 * when the application will be run after the first time.
 */
public class LoginPreferences {

    private final static String MY_PREFERENCES = "my_preferences";

    /*
    return true if it is the first time the app has been launched, false otherwise
     */
    public static boolean isFirst(Context context){
        // acquire the preferences in private mode
        final SharedPreferences prefs = context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        // acquire the preference
        final boolean first = prefs.getBoolean("first_time", true);
        // if the precedence is true, set it to false and then return it
        if(first){
            final SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("first_time", false);
            editor.commit();
        }
        return first;
    }


}
