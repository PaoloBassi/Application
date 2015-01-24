package it.unozerouno.givemetime.controller.service;

import it.unozerouno.givemetime.controller.fetcher.DatabaseManager;
import it.unozerouno.givemetime.controller.fetcher.places.LocationFetcher;
import it.unozerouno.givemetime.controller.fetcher.places.LocationFetcher.OnLocationReadyListener;
import it.unozerouno.givemetime.model.UserKeyRing;
import it.unozerouno.givemetime.model.events.EventInstanceModel;
import it.unozerouno.givemetime.utils.GiveMeLogger;
import it.unozerouno.givemetime.view.utilities.OnDatabaseUpdatedListener;

import java.util.ArrayList;

import android.R;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

public class GiveMeTimeService extends IntentService{
	private DatabaseManager database;
	private static ArrayList<Integer> activeNotificationsId;
	
	
	public GiveMeTimeService() {
		super("GiveMeTimeWorkerThread");
	}

	
	@Override
	public void onCreate() {
		super.onCreate();
		//Initializing components
		database = DatabaseManager.getInstance(getApplication());
		if(activeNotificationsId==null){
			activeNotificationsId = new ArrayList<Integer>();
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		//Calling back the IntentService onStartCommand for managing the life cycle
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		// TODO Free any resources or save the collected data

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		//TODO: put here the entire service flow
		//This methods are meant for debug purpose only
		GiveMeLogger.log("Stanting the service Flow");
		serviceFlow();
		GiveMeLogger.log("Trying to get Location");
		getLocation();
	}
	
	
	private void showNotification(String message){
		Notification noti = new Notification.Builder(getApplicationContext())
        .setContentTitle("GiveMeTime")
        .setContentText(message)
        .setSmallIcon(R.drawable.ic_dialog_info)
        .getNotification();
		NotificationManager mNotificationManager =   (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		int id= activeNotificationsId.size();
		mNotificationManager.notify(id, noti);
		activeNotificationsId.add(activeNotificationsId.size());
		GiveMeLogger.log("Notification sent: "+ message );
	}
	/**
	 * This method contains the whole flow of the service.
	 */
	private void serviceFlow(){
		//Getting current active events
		OnDatabaseUpdatedListener<ArrayList<EventInstanceModel>> listener = new OnDatabaseUpdatedListener<ArrayList<EventInstanceModel>>() {
			@Override
			protected void onUpdateFinished(
					ArrayList<EventInstanceModel> activeEvents) {
				//That's the list of current active events
				if (activeEvents.isEmpty()){
					//No events are active, so we suppose it is the free-time (please make checks about Work TimeTable, etc)	
					showNotification("No active events");
				} else {
					//If there are active events, proceed with the flow
					showNotification(activeEvents.size() + " events are active");
				}
			}
		};
		DatabaseManager.getActiveEvents(listener, getApplicationContext());
	}
	
	/**
	 * This function is made for debug purpose only, as the location requests are asynchronous and cannot be returned by this function directly
	 */
	private void getLocation(){
		LocationFetcher.getInstance(getApplication());
		LocationFetcher.getLastLocation(new OnLocationReadyListener() {
			
			@Override
			public void onConnectionFailed() {
				showNotification("No connectivity for getting location");
			}
			
			@Override
			public void locationReady(Location location) {
				showNotification("Got location! " + location.getLatitude() + " " + location.getLatitude());
			} 
		}, UserKeyRing.getLocationUpdateFrequency(getApplication()));
	}
	
}