package it.unozerouno.givemetime.controller.fetcher.places;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.model.constraints.ComplexConstraint;
import it.unozerouno.givemetime.model.places.PlaceModel;
import it.unozerouno.givemetime.view.utilities.ApiKeys;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.analytics.j;
import com.google.android.gms.internal.js;
import com.google.android.gms.internal.na;

import android.app.AlertDialog;
import android.content.Context;
import android.location.Location;
import android.util.Log;

public final class PlaceFetcher {
	private static final String LOG_TAG = "GiveMeTime";

	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String TYPE_DETAILS = "/details";
	private static final String OUT_JSON = "/json";


	public static ArrayList<PlaceResult> autocomplete(String input) {
	    ArrayList<PlaceResult> resultList = null;

	    HttpURLConnection conn = null;
	    StringBuilder jsonResults = new StringBuilder();
	    try {
	        StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
	        sb.append("?key=" + ApiKeys.getKey());
	        sb.append("&input=" + URLEncoder.encode(input, "utf8"));

	        URL url = new URL(sb.toString());
	        conn = (HttpURLConnection) url.openConnection();
	        InputStreamReader in = new InputStreamReader(conn.getInputStream());

	        // Load the results into a StringBuilder
	        int read;
	        char[] buff = new char[1024];
	        while ((read = in.read(buff)) != -1) {
	            jsonResults.append(buff, 0, read);
	        }
	    } catch (MalformedURLException e) {
	        Log.e(LOG_TAG, "Error processing Places API URL", e);
	        return resultList;
	    } catch (IOException e) {
	        Log.e(LOG_TAG, "Error connecting to Places API", e);
	        return resultList;
	    } finally {
	        if (conn != null) {
	            conn.disconnect();
	        }
	    }

	    try {
	        // Create a JSON object hierarchy from the results
	        JSONObject jsonObj = new JSONObject(jsonResults.toString());
	        JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

	        // Extract the Place descriptions from the results
	        resultList = new ArrayList<PlaceResult>(predsJsonArray.length());
	        String status = jsonObj.getString("status");
	        
	        for (int i = 0; i < predsJsonArray.length(); i++) {
	        	PlaceResult newResult = new PlaceResult(predsJsonArray.getJSONObject(i),status);
	        	resultList.add(newResult);
	        }
	     
	    } catch (JSONException e) {
	        Log.e(LOG_TAG, "Cannot process JSON results", e);
	    }
	    return resultList;
	}
	
	/**
	 * This function fetches additional infos about a place and stores them in the PlaceModel object itself
	 * Fetched info:
	 * -formattedAddress
	 * -phoneNumber
	 * -location
	 * -icon
	 * -opening time
	 * @param place
	 * @return
	 */
	public static PlaceModel getAdditionalInfo(PlaceModel place){
		
		    HttpURLConnection conn = null;
		    StringBuilder jsonResults = new StringBuilder();
		    try {
		        StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_DETAILS + OUT_JSON);
		        sb.append("?placeid=" + place.getPlaceId());
		        sb.append("&key=" + ApiKeys.getKey());

		        URL url = new URL(sb.toString());
		        conn = (HttpURLConnection) url.openConnection();
		        InputStreamReader in = new InputStreamReader(conn.getInputStream());

		        // Load the results into a StringBuilder
		        int read;
		        char[] buff = new char[1024];
		        while ((read = in.read(buff)) != -1) {
		            jsonResults.append(buff, 0, read);
		        }
		    } catch (MalformedURLException e) {
		        Log.e(LOG_TAG, "Error processing Places API URL", e);
		        return place;
		    } catch (IOException e) {
		        Log.e(LOG_TAG, "Error connecting to Places API", e);
		        return place;
		    } finally {
		        if (conn != null) {
		            conn.disconnect();
		        }
		    }

		    try {
		        // Create a JSON object hierarchy from the results
		        JSONObject result = new JSONObject(jsonResults.toString());
		        String formattedAddress = result.getString("formatted_address");
		        String phoneNumber = result.getString("international_phone_number");
		        JSONObject jsonLocation = result.getJSONObject("geometry").getJSONObject("location");
		        String latitude = jsonLocation.getString("lat");
		        String longitude = jsonLocation.getString("lng");
		        Location location = new Location("Places");
		        location.setLatitude(Double.parseDouble(latitude));
		        location.setLongitude(Double.parseDouble(longitude));
		        String icon = result.getString("icon");
		        JSONArray openingTimes = result.getJSONObject("opening_hours").getJSONArray("periods");
		        
		        //Setting collected info on PlaceModel
		        
		        place.setFormattedAddress(formattedAddress);
		        place.setPhoneNumber(phoneNumber);
		        place.setLocation(location);
		        place.setIcon(icon);
		        place.setOpeningTime(ComplexConstraint.parseJSONResult(openingTimes));

		     
		     
		    } catch (JSONException e) {
		        Log.e(LOG_TAG, "Cannot process JSON results", e);
		    }
		return place;
	}
	
	
	
	
	/**
	 * Represents a Result from Places Autocomplete
	 * @author Edoardo Giacomello <edoardo.giacomello1990@gmail.com>
	 *
	 */
	public static class PlaceResult{
		//Places strings
		String description;
		String placeID;
		String status;
		ArrayList<String> terms;
		
		//Generated strings
		String name;
		String address;
		String country;
		
		public PlaceResult(String id, String placeID, String description) {
			super();
			this.placeID = placeID;
			this.description = description;
		}
		
		public PlaceResult(JSONObject jsonObject, String requestStatus) throws JSONException{
			this.status = requestStatus;
			this.description=jsonObject.getString("description");
			this.placeID = jsonObject.getString("place_id");
			JSONArray resultTerms = jsonObject.getJSONArray("terms");
			terms = new ArrayList<String>();
			for (int i = 0; i < resultTerms.length(); i++) {
				terms.add(resultTerms.getJSONObject(i).getString("value"));
			}
			parseTerms();
		}
		
		
		private void parseTerms() {
			if (terms.size() == 2) {
				//Probably that's a capital city result
				name = terms.get(0);
				address = terms.get(0);
				country = terms.get(1);
				return;
			}
			if (terms.size() == 3){
				//Probably that's a periferical town result
				name = terms.get(0);
				address = terms.get(0) +", " + terms.get(1);
				country = terms.get(2);
				return;
			}
			if (terms.size() == 4){
				//Probably that's a road or address result
				name = terms.get(0);
				address = terms.get(1) +", " + terms.get(2);
				country = terms.get(3);
				return;
			}
			if (terms.size() == 5){
				//Probably that's a business or establishment result
				name = terms.get(0);
				address = terms.get(1) +", " + terms.get(2) +", " + terms.get(3);
				country = terms.get(3) + ", " + terms.get(4);
				return;
			}

		}
		
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getPlaceID() {
			return placeID;
		}
		public void setPlaceID(String placeID) {
			this.placeID = placeID;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public ArrayList<String> getTerms() {
			return terms;
		}
		public void setTerms(ArrayList<String> terms) {
			this.terms = terms;
		}
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

		public void showErrors(Context context){
			if(status == "OVER_QUERY_LIMIT"){
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
				alertBuilder.setTitle("Error").setMessage(R.string.place_autocomplete_quota_exceeded).show();
			} else if (status == "REQUEST_DENIED") {
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
				alertBuilder.setTitle("Error").setMessage(R.string.place_autocomplete_key_outdated).show();
			}
		}
		@Override
		public String toString() {
			return name;
		}
	}
}
