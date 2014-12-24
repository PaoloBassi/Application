package it.unozerouno.givemetime.model;

import it.unozerouno.givemetime.view.main.SettingsActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;


/**
 * This class manages the Shared Preferences and cached Auth Tokens for GiveMeTime Application.
 * Provides access to preferences, user data and auth token via getters/setters. 
 * @author Edoardo Giacomello
 *
 */
public final class UserKeyRing {
	private static SharedPreferences prefs;
	private static SharedPreferences.Editor editor;
	private static final String SHARED_PREF_GENERAL = "givemetime_preferences";
	
	private static final String firstTimeLoginPref = "first_time_login";		
	private static final String userEmailPref = "user_email";
	private static final String userTokenPref = "token_user";
	private static final String calendarTokenPref = "token_calendar";
	
	@SuppressLint("CommitPrefEdits") 
	private static void setSharedPreferences(Context context){
		prefs = context.getSharedPreferences(SHARED_PREF_GENERAL, Context.MODE_PRIVATE);
		editor = prefs.edit();
	}
	
	/**
	 * This method will wipe all shared preferences for the application
	 * @param context
	 */
	public static void resetSharedPreferences(Context context){
		setSharedPreferences(context);
		editor.clear();
		editor.commit();
	}
	
	
	public static void setUserEmail(Context context, String userEmail) {
		setSharedPreferences(context);
		editor.putString(userEmailPref, userEmail);
		editor.commit();
	}
	
	public static void setUserToken(Context context, String userToken) {
		setSharedPreferences(context);
		editor.putString(userTokenPref, userToken);
		editor.commit();
	}
	
	public static void setCalendarToken(Context context, String calendarToken) {
		setSharedPreferences(context);
		editor.putString(calendarTokenPref, calendarToken);
		editor.commit();
	}
	
	public static void setFirstLogin(Context context, Boolean value) {
		setSharedPreferences(context);
		editor.putBoolean(firstTimeLoginPref, value);
		editor.commit();
	}
	public static String getUserEmail(Context context) {
		setSharedPreferences(context);
		return prefs.getString(userEmailPref, null);
		}
	public static String getUserToken(Context context) {
		setSharedPreferences(context);
		return prefs.getString(userTokenPref, null);
	}
	public static String getCalendarToken(Context context) {
		setSharedPreferences(context);
		return prefs.getString(calendarTokenPref, null);
	}
	public static boolean isFirstTimeLogin(Context context){
		setSharedPreferences(context);
		return prefs.getBoolean(firstTimeLoginPref, true);
	}
	
	
	
}
