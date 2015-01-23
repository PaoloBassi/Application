package it.unozerouno.givemetime.controller.service;

import it.unozerouno.givemetime.controller.fetcher.DatabaseManager;
import it.unozerouno.givemetime.utils.GiveMeLogger;
import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;

public class GiveMeTimeService extends IntentService{
	private DatabaseManager database;
	
	
	public GiveMeTimeService() {
		super("GiveMeTimeWorkerThread");
	}

	
	@Override
	public void onCreate() {
		super.onCreate();
		//Initializing components
		database = DatabaseManager.getInstance(getApplication());
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		GiveMeLogger.log("Service started");
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
		// TODO Auto-generated method stub
		
	}
	
	
	
	
}