package it.unozerouno.givemetime.controller.service;

import it.unozerouno.givemetime.controller.fetcher.DatabaseManager;
import it.unozerouno.givemetime.model.events.EventInstanceModel;
import it.unozerouno.givemetime.model.events.EventListener;
import it.unozerouno.givemetime.model.events.EventModel;

import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.format.Time;

/**
 * This is a scheduler that determines when the GiveMeTime service have to be started
 * It's launched at boot time and it also supports rescheduling, based on events present in the user calendar
 * @author Edoardo Giacomello <edoardo.giacomello1990@gmail.com>
 *
 */
public class ServiceScheduler extends BroadcastReceiver {
	private static Context context;
	ArrayList<EventModel> todayEvents;
	DatabaseManager database;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			ServiceScheduler.context = context;
			database = DatabaseManager.getInstance(context);
		//	getTodayEvents();
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
		todayEvents = new ArrayList<EventModel>();
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
								
			}
		};
		
		
		database.getEventsInstances(start, end, context, eventListener);
	}

	/**
	 * Starts the service once, at given time
	 * @param time
	 */
	private static void scheduleServiceAt(Time time){
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent serviceIntent = new Intent(context, GiveMeTimeService.class);
		PendingIntent alarmIntent = PendingIntent.getService(context, 0, serviceIntent, 0);
		alarmManager.set(AlarmManager.RTC_WAKEUP, time.toMillis(false), alarmIntent);
		
	}
	
	/**
	 * This schedules the service in the middle of every scheduled event
	 * 
	 */
	private static void scheduleOverEvents(){
		DatabaseManager database = DatabaseManager.getInstance(context);
	}
}
