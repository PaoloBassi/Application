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
	private static final String TYPE_SEARCH = "/nearbysearch";
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
			PlaceModel placeWithInfo = place.clone();
		    HttpURLConnection conn = null;
		    StringBuilder jsonResults = new StringBuilder();
		    try {
		        StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_DETAILS + OUT_JSON);
		        sb.append("?placeid=" + placeWithInfo.getPlaceId());
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
		        return placeWithInfo;
		    } catch (IOException e) {
		        Log.e(LOG_TAG, "Error connecting to Places API", e);
		        return placeWithInfo;
		    } finally {
		        if (conn != null) {
		            conn.disconnect();
		        }
		    }

		    try {
		        // Create a JSON object hierarchy from the results
		        
		    	JSONObject response = new JSONObject(jsonResults.toString());
		     if (response.has("result")){
		        JSONObject result = response.getJSONObject("result");
		        
		        if (result.has("formatted_address")){  
		        String formattedAddress = result.getString("formatted_address");
		        placeWithInfo.setFormattedAddress(formattedAddress);
		        }
		        if (result.has("international_phone_number")){ 
		        String phoneNumber = result.getString("international_phone_number");
		        placeWithInfo.setPhoneNumber(phoneNumber);
		        }
		        if (result.has("name")){ 
			        String name = result.getString("name");
			        placeWithInfo.setName(name);;
			        }
		        
		        if (result.has("geometry")){ 
		        	  JSONObject geometry = result.getJSONObject("geometry");
		        	  if (geometry.has("location")){
		        	  JSONObject jsonLocation= geometry.getJSONObject("location");
		        	  if (jsonLocation.has("lat") && jsonLocation.has("lng")){
				        String latitude = jsonLocation.getString("lat");
				        String longitude = jsonLocation.getString("lng");
				        Location location = new Location("Places");
				        location.setLatitude(Double.parseDouble(latitude));
				        location.setLongitude(Double.parseDouble(longitude));
				        placeWithInfo.setLocation(location);
		        	  }
		        	  }
		        }
		      
		        if (result.has("icon")){ 
		        String icon = result.getString("icon");
		        placeWithInfo.setIcon(icon);
		        }
		        if (result.has("opening_hours")){ 
		        JSONArray openingTimes = result.getJSONObject("opening_hours").getJSONArray("periods");
		        placeWithInfo.setOpeningTime(ComplexConstraint.parseJSONResult(openingTimes));
		        }
		     }
		     
		    } catch (JSONException e) {
		        Log.e(LOG_TAG, "Cannot process JSON results", e);
		    }
		return placeWithInfo;
	}
	
	public static PlaceModel getPlaceModelFromLocation(Location location){
		PlaceModel placeModel = null;
		

	    HttpURLConnection conn = null;
	    StringBuilder jsonResults = new StringBuilder();
	    try {
	        StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_SEARCH + OUT_JSON);
	        sb.append("?key=" + ApiKeys.getKey());
	        sb.append("&location=" +Double.toString(location.getLatitude())+","+Double.toString(location.getLongitude()));
	        sb.append("&radius=50");
	        sb.append("&type=route");
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
	        return placeModel;
	    } catch (IOException e) {
	        Log.e(LOG_TAG, "Error connecting to Places API", e);
	        return placeModel;
	    } finally {
	        if (conn != null) {
	            conn.disconnect();
	        }
	    }
	    ArrayList<PlaceResult> resultList = new ArrayList<PlaceResult>();
	    try {
	        // Create a JSON object hierarchy from the results
	        JSONObject jsonObj = new JSONObject(jsonResults.toString());
	        JSONArray resultJsonArray = jsonObj.getJSONArray("results");

	        // Extract the Place descriptions from the results
	        resultList = new ArrayList<PlaceResult>(resultJsonArray.length());
	        String status = jsonObj.getString("status");
	        if(status.equals("OK")){
	        for (int i = 0; i < resultJsonArray.length(); i++) {
	        	if (resultJsonArray.getJSONObject(i).has("place_id")){
	        	PlaceResult newResult = new PlaceResult(resultJsonArray.getJSONObject(i),status);
	        	resultList.add(newResult);
	        	}
	        }
	        }
	    } catch (JSONException e) {
	        Log.e(LOG_TAG, "Cannot process JSON results", e);
	    }
	    //Now we have the models for the results. Let's get the first, if any, and return it
	    if(!resultList.isEmpty()){
	    	placeModel = new PlaceModel(resultList.get(0));
	    	placeModel = getAdditionalInfo(placeModel);
	    } else {
	    	return null;
	    }
	    return placeModel;
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
		
		public PlaceResult(String placeID, String description) {
			super();
			this.placeID = placeID;
			this.description = description;
		}
		
		public PlaceResult(JSONObject jsonObject, String requestStatus) throws JSONException{
			this.status = requestStatus;
			if(jsonObject.has("place_id"))this.placeID = jsonObject.getString("place_id");
			if(jsonObject.has("description"))this.description=jsonObject.getString("description");
			if(jsonObject.has("terms")){
			JSONArray resultTerms = jsonObject.getJSONArray("terms");
			terms = new ArrayList<String>();
			for (int i = 0; i < resultTerms.length(); i++) {
				terms.add(resultTerms.getJSONObject(i).getString("value"));
			}
			parseTerms();
			}
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
