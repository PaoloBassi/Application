package it.unozerouno.givemetime.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;


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
	
	//Login and tokens
	private static final String firstTimeLoginPref = "first_time_login";		
	private static final String userEmailPref = "user_email";
	private static final String userTokenPref = "token_user";
	
	//Calendar Selection preferences
	private static final String selectedCalendarId = "calendar_selected_id";
	private static final String selectedCalendarName = "calendar_selected_id";
	
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
	
	public static void setToken(Context context, String token) {
		setSharedPreferences(context);
		editor.putString(userTokenPref, token);
		editor.commit();
	}
	
	public static void setFirstLogin(Context context, Boolean value) {
		setSharedPreferences(context);
		editor.putBoolean(firstTimeLoginPref, value);
		editor.commit();
	}
	
	public static void setCalendarId(Context context, String calendarID){
		setSharedPreferences(context);
		editor.putString(selectedCalendarId, calendarID);
		editor.commit();
	}
	public static void setCalendarName(Context context, String calendarName){
		setSharedPreferences(context);
		editor.putString(selectedCalendarName, calendarName);
		editor.commit();
	}
	
	
	
	
	
	
	public static String getUserEmail(Context context) {
		setSharedPreferences(context);
		return prefs.getString(userEmailPref, null);
		}
	public static String getToken(Context context) {
		setSharedPreferences(context);
		return prefs.getString(userTokenPref, null);
	}
	
	public static boolean isFirstTimeLogin(Context context){
		setSharedPreferences(context);
		return prefs.getBoolean(firstTimeLoginPref, true);
	}
	
	public static String getCalendarId(Context context) {
		setSharedPreferences(context);
		return prefs.getString(selectedCalendarId, "0");
	}
	public static String getCalendarName(Context context) {
		setSharedPreferences(context);
		return prefs.getString(selectedCalendarName, "");
	}
	
}
