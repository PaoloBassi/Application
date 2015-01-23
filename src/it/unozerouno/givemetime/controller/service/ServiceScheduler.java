package it.unozerouno.givemetime.controller.service;

import it.unozerouno.givemetime.controller.fetcher.DatabaseManager;
import it.unozerouno.givemetime.model.UserKeyRing;
import it.unozerouno.givemetime.model.constraints.TimeConstraint;
import it.unozerouno.givemetime.model.events.EventInstanceModel;
import it.unozerouno.givemetime.model.events.EventListener;
import it.unozerouno.givemetime.model.events.EventModel;

import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.test.IsolatedContext;
import android.text.format.Time;
import android.util.SparseArray;

/**
 * This is a scheduler that determines when the GiveMeTime service have to be started
 * It's launched at boot time and it also supports rescheduling, based on events present in the user calendar
 * @author Edoardo Giacomello <edoardo.giacomello1990@gmail.com>
 *
 */
public class ServiceScheduler extends BroadcastReceiver {
	private static Context context;
	private static ArrayList<EventInstanceModel> todayEvents;
	private static DatabaseManager database;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			ServiceScheduler.context = context;
			database = DatabaseManager.getInstance(context);
			getTodayEvents();
		}
	}
	
	/**
	 * Fetches today event from database, when done, continues with the flow
	 */
	private void getTodayEvents(){
		Time start = new Time();
		Time end = new Time();
		Time today = new Time();
		today.setToNow();
		start.set(0, 0, 0, today.monthDay, today.month, today.year);
		end.set(59, 59, 23, today.monthDay, today.month, today.year);
		todayEvents = new ArrayList<EventInstanceModel>();
		EventListener<EventInstanceModel> eventListener = new EventListener<EventInstanceModel>() {
			
			@Override
			public void onEventCreation(EventInstanceModel newEvent) {
				todayEvents.add(newEvent);
			}
			
			@Override
			public void onEventChange(EventInstanceModel newEvent) {
			}

			@Override
			public void onLoadCompleted() {
				reschedule();	
			}
		};
		
		
		database.getEventsInstances(start, end, context, eventListener);
	}

	/**
	 * Makes a full schedule
	 */
	private static void reschedule(){
		scheduleOverEvents();
		scheduleOverFreeTime();
		System.out.println("GiveMeTime Service schedule completed");
	}
	
	/**
	 * Starts the service once, at given time
	 * @param time
	 */
	private static void scheduleServiceAt(Long time){
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent serviceIntent = new Intent(context, GiveMeTimeService.class);
		PendingIntent alarmIntent = PendingIntent.getService(context, 0, serviceIntent, 0);
		alarmManager.set(AlarmManager.RTC_WAKEUP, time, alarmIntent);
		System.out.println("Scheduled service at " + time);
	}
	
	/**
	 * Schedules the service in the middle of every scheduled event
	 * 
	 */
	private static void scheduleOverEvents(){
		//This array contains starting times for today services
		SparseArray<Long> scheduleOverEvents = new SparseArray<Long>();
		
		//Getting service start time for every event scheduled today
		for (EventInstanceModel currentEvent : todayEvents) {
			Time start = currentEvent.getStartingTime();
			Time end = currentEvent.getEndingTime();
			if(start != null && end != null){
				Long startLong = start.toMillis(false);
				Long endLong = end.toMillis(false);
				Long duration = endLong - startLong;
				Long scheduledTime = startLong + (duration/(long) 2);
				System.out.println("Start is " + startLong + " end is " + endLong + " scheduling at " + scheduledTime);
				int eventId = Integer.parseInt(currentEvent.getEvent().getID());
				scheduleOverEvents.put(eventId, scheduledTime);
			}
			//Scheduling events
			for (int i = 0; i < scheduleOverEvents.size(); i++) {
				int key = scheduleOverEvents.keyAt(i);
				Long time = scheduleOverEvents.get(key);
				scheduleServiceAt(time);
			}
		}
		
		
		
		
	}

	private static void scheduleOverFreeTime(){
		//Generating today millis for each hour
		Time today = new Time();
		today.setToNow();
		today.set(today.monthDay, today.month, today.year);
		Long midnight = today.toMillis(false);
		ArrayList<Long> dayHour = new ArrayList<Long>();
		
		for (int hour = 0; hour < 24; hour++) {
			dayHour.add(Long.valueOf(midnight+hour));
		}
		
		//Getting sleep times - service is not scheduled during sleep times: 
		TimeConstraint sleepTime = DatabaseManager.getUserSleepTime(UserKeyRing.getUserEmail(context));
		Time now = new Time();
		now.setToNow();
		for (Long scheduleTime : dayHour) {
			//If it's not sleep time and the schedule it's in the future
			if((!sleepTime.isActive(scheduleTime)) && scheduleTime>now.toMillis(false) ){
				//TODO: Maybe shuffle a bit the time
				scheduleServiceAt(scheduleTime);
			}
		}
		
		
	}


}
