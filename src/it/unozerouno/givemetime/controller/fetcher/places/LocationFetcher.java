package it.unozerouno.givemetime.controller.fetcher.places;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

/**
 * This class instantiates the Location API of Google Play Services and returns the last known location
 * @author Edoardo Giacomello <edoardo.giacomello1990@gmail.com>
 *
 */
public class LocationFetcher implements ConnectionCallbacks, OnConnectionFailedListener {
	GoogleApiClient locationClient; 
	Location lastLocation;
	OnLocationReadyListener locationListener;
	
	
	public void getLocation(Context context, OnLocationReadyListener locationListener){
		
		this.locationListener = locationListener;
		buildGoogleApiClient(context);
	}
	protected synchronized void buildGoogleApiClient(Context context) {
		locationClient = new GoogleApiClient.Builder(context)
	        .addConnectionCallbacks(this)
	        .addOnConnectionFailedListener(this)
	        .addApi(LocationServices.API)
	        .build();
		locationClient.connect();
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		locationListener.onConnectionFailed();
	}

	@Override
	public void onConnected(Bundle arg0) {
		lastLocation = LocationServices.FusedLocationApi.getLastLocation(
				locationClient);
	        locationListener.locationReady(lastLocation);
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		System.out.println("Connection suspended");
		
	}
	public interface OnLocationReadyListener{
		public void locationReady(Location location);
		public void onConnectionFailed();
	}
}
