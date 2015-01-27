package it.unozerouno.givemetime.controller.service;

import it.unozerouno.givemetime.controller.fetcher.DatabaseManager;
import it.unozerouno.givemetime.controller.fetcher.places.LocationFetcher;
import it.unozerouno.givemetime.controller.fetcher.places.LocationFetcher.OnLocationReadyListener;
import it.unozerouno.givemetime.controller.optimizer.FeasibleEventGiver;
import it.unozerouno.givemetime.model.UserKeyRing;
import it.unozerouno.givemetime.model.events.EventInstanceModel;
import it.unozerouno.givemetime.model.questions.LocationMismatchQuestion;
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
import android.text.format.Time;

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
		GiveMeLogger.log("Starting the service Flow");
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
					
					final ArrayList<EventInstanceModel> activeEvents) {
				//That's the list of current active events
				if (activeEvents.isEmpty()){
					//2a - No events are active, then we suppose it is the free-time (please make checks about Work TimeTable, etc)	
					showNotification("No active events");
						//Getting current Location
							LocationFetcher.getInstance(getApplication());
							LocationFetcher.getLastLocation(new OnLocationReadyListener() {
								@Override
								public void onConnectionFailed() {
									GiveMeLogger.log("No connectivity for getting location");
								}
								@Override
								public void locationReady(Location location) {
									GiveMeLogger.log("Got location! " + location.getLatitude() + " " + location.getLongitude());
									//Now that we have a location, we can check if the user is really where it should be
									for (EventInstanceModel currentActiveEvent : activeEvents) {
										if (currentActiveEvent.getEvent().getLocation() == null){
											//Event location is not set
											if(currentActiveEvent.getEvent().getDoNotDisturb()){
												Time now = new Time();
												now.setToNow();
												LocationMismatchQuestion question = new LocationMismatchQuestion(getApplication(), currentActiveEvent, location, now);
												showNotification("Storing a new LocationMismatchQuestion");
												//TODO: Store a LocationMismatch question - is your event @ location?
												
											}else{
												Time now = new Time();
												now.setToNow();
												LocationMismatchQuestion question = new LocationMismatchQuestion(getApplication(), currentActiveEvent, location, now);
												showNotification("Is " + currentActiveEvent.getEvent().getName() + " at " + location.getLatitude()+","+location.getLongitude()+"?");
												//TODO: Notify a LocationMismatch question - is your event @ location?
												
											}
										} else
										{
											//Current event has a location, we can check if the user is far from it
											if (location.distanceTo(currentActiveEvent.getEvent().getLocation())> UserKeyRing.getLocationMaxDistTolerance(getApplicationContext())){
												//The user is far from the current scheduled event
												if(currentActiveEvent.getEvent().getDoNotDisturb()){
													Time now = new Time();
													now.setToNow();
													LocationMismatchQuestion question = new LocationMismatchQuestion(getApplication(), currentActiveEvent, location, now);
													showNotification("Storing a new LocationMismatchQuestion");
													//TODO: Store a LocationMismatch question - were you actually doing "event" @ location?
												}else{
													Time now = new Time();
													now.setToNow();
													LocationMismatchQuestion question = new LocationMismatchQuestion(getApplication(), currentActiveEvent, location, now);
													showNotification("Are you doing " + currentActiveEvent.getEvent().getName() + " at " + location.getLatitude()+","+location.getLongitude()+"?");
													//TODO: Notify a LocationMismatch question - were you actually doing "event" @ location?
		
												}
											} else {
												GiveMeLogger.log("User is at the scheduled event");
											}
										}
									}
									
									
									
								} 
							}, UserKeyRing.getLocationUpdateFrequency(getApplication()));
				} else {
					//2b- If there are active events, proceed with the flow
					GiveMeLogger.log(activeEvents.size() + " events are active");
					//TODO: Get Location
					
					//TODO: If event location is null, OR is it far away from current, then store in db the new location
					//TODO: if the event is DND, then store the question, else notify the question
					
					
					
					
					
				}
			}
		};
		//1 - Get the active event 
		DatabaseManager.getActiveEvents(listener, getApplicationContext());
	}
	
	
	
	
	/**
	 * This function is made for debug purpose only, as the location requests are asynchronous and cannot be returned by this function directly
	 */
	private void getLocation(){
		
	}
	
}