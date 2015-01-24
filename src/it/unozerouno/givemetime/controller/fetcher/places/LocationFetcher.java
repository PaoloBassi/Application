package it.unozerouno.givemetime.controller.fetcher.places;

import it.unozerouno.givemetime.model.UserKeyRing;
import it.unozerouno.givemetime.utils.GiveMeLogger;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.text.format.Time;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * This class instantiates the Location API of Google Play Services and returns the last known location
 * At every location request, it checks if a stored known location is present, and it checks whether it is "new" (accorging to the specified temporal parameters)
 * if a valid stored location is present, it is returned immediately,
 * otherwise it will register for a location update request to LocationServices API. When it receives a new location update it will return to the last Service that
 * requested a Location, then storing the location for further use.
 *
 * @author Edoardo Giacomello <edoardo.giacomello1990@gmail.com>
 *
 */
public final class LocationFetcher implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
	private static LocationFetcher instance;
	private static GoogleApiClient locationClient; 
	private static Location lastLocation;
	private static Time lastUpdate;
	private static OnLocationReadyListener pendingLocationListener; //This listener represents a service that is waiting for a location
	private static int locationTimeTolerance;
	private static Context context;
	
	private LocationFetcher(Context context) {
		LocationFetcher.context = context;
	}

	public static LocationFetcher getInstance(Context context){
		if (instance ==null ){ 
			instance = new LocationFetcher(context);
			buildGoogleApiClient(context);
			locationTimeTolerance = UserKeyRing.getLocationMaxAge(context);
		}
		return instance;
	}
	
	/**
	 * Returns via the listener the last known location if it's present or if it's not too old.
	 * otherwise it waits until the next location update and notify the new location 
	 * If the last known location has been fetched less then "timeTolerance" minutes ago, it is considered reliable and returned
	 * 
	 * @param locationListener listener from which getting the known location
	 * @param timeTolerance max time (in minutes) for considering a location reliable
	 */
	public static synchronized void getLastLocation(OnLocationReadyListener locationListener, int timeTolerance){
		//We consider as "outdated" locations that are older than timeTolerance minutes
		locationTimeTolerance = timeTolerance;
		if(lastUpdate != null){
			Time now = new Time();
			now.setToNow();
			if((lastUpdate != null && lastLocation !=null) && (now.toMillis(false) - lastUpdate.toMillis(false) < (1000*60*timeTolerance))){
				GiveMeLogger.log("Last known location is reliable");
				locationListener.locationReady(lastLocation);
				
				return;
			} 
		}
		//If no last known location is known or it's not reliable, then the LocationService callbacks will eventually notify the listener when ready
		LocationFetcher.pendingLocationListener=locationListener;
		//If the locationClient is not connected, then reinitialize it
		if (!locationClient.isConnecting() && !locationClient.isConnected()){
			GiveMeLogger.log("Rebuilding client..");
			buildGoogleApiClient(context);
		}
		//Since a listener is waiting for a location and we still don't have it, we have just registered for a new Update (see "onConnected" callback)
		GiveMeLogger.log("Waiting for a response");
		}
	
	/**
	 * Builds a new GoogleApiClient bound to LocationServices API (PlayServices) and attempt to connect
	 * @param context
	 */
	private static synchronized void buildGoogleApiClient(Context context) {
		locationClient = new GoogleApiClient.Builder(context)
	        .addConnectionCallbacks(instance)
	        .addOnConnectionFailedListener(instance)
	        .addApi(LocationServices.API)
	        .build();
		GiveMeLogger.log("Location client is connecting...");
		locationClient.connect();
	}
	
	/**
	 * This function requests updates to LocationServices, every a certain amount minutes defined in UserKeyRing
	 * with a balanced power/accuracy tradeoff, which should give an overall accuracy within 100 meters. 
	 */
	private static LocationRequest createLocationRequest() {
	    LocationRequest mLocationRequest = new LocationRequest();
	    mLocationRequest.setInterval(UserKeyRing.getLocationUpdateFrequency(context)/*min*/*60/*sec*/*1000/*millis*/);
	    //This is set in case the user is using an app such googleMaps, which makes location requests very frequently.
	    //We announce we can handle an update every 60 seconds, we're not too greedy!
	    mLocationRequest.setFastestInterval(60/*sec*/*1000/*millis*/);
	    mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
	    return mLocationRequest;
	}
	
	
	
	/**
	 * This function is called when a new valid location is received, informing all pending Listeners 
	 */
	private static synchronized void notifyListeners(Location newLocation){
		if (pendingLocationListener != null) {
			pendingLocationListener.locationReady(newLocation);
			GiveMeLogger.log("Notified to listener");
		}
		//Removing request listener (since the request have been satisfied and we don't want to call multiple times the same callback)
		pendingLocationListener=null;
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		pendingLocationListener.onConnectionFailed();
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		GiveMeLogger.log("LocationClient is connected");
		//On connection, let's try to get the last known location
		Location newLocation = LocationServices.FusedLocationApi.getLastLocation(
				locationClient);
		if((newLocation != null && lastLocation!=null && (newLocation.getTime() - lastLocation.getTime()) > (1000*60*locationTimeTolerance)) || (newLocation!=null && lastLocation ==null))
			//fetched location is valid and it's considered new
		{
			lastLocation = newLocation;
			lastUpdate = new Time();
			lastUpdate.set(lastLocation.getTime());
			//Now we have a valid location, so we can resolve pending requests
			notifyListeners(lastLocation);
			return;
		}
		
		//If no recent locations are known, or got an expired location, then make a request for new location updates
		GiveMeLogger.log("No location known, making request for new location...");
		 LocationServices.FusedLocationApi.requestLocationUpdates(locationClient, createLocationRequest(), this);
		
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		GiveMeLogger.log("Connection suspended");
		
	}
	/**
	 * This interface is used to notify GiveMeTime Services that the LocationFetcher has a valid new Location, according to app parameters
	 * @author Edoardo Giacomello <edoardo.giacomello1990@gmail.com>
	 *
	 */
	public interface OnLocationReadyListener{
		public void locationReady(Location location);
		public void onConnectionFailed();
	}
	
	@Override
	public void onLocationChanged(Location newLocation) {
		GiveMeLogger.log("Received a Location update!");
		if((newLocation != null && lastLocation!=null && (newLocation.getTime() - lastLocation.getTime()) > (1000*60*locationTimeTolerance)) || (newLocation!=null && lastLocation ==null))
			//fetched location is valid and it's considered new
		{
			GiveMeLogger.log("Location is valid!");
			lastLocation = newLocation;
			lastUpdate = new Time();
			lastUpdate.set(lastLocation.getTime());
			//Now we have a valid location, so we can resolve pending requests
			notifyListeners(lastLocation);
			return;
		}
		if(newLocation==null){
			GiveMeLogger.log("LocationFetcher received a null Location update");
		}
	}
}
