package it.unozerouno.givemetime.controller;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import it.unozerouno.givemetime.utils.AsyncTaskWithListener;
import it.unozerouno.givemetime.utils.GiveMeLogger;
import it.unozerouno.givemetime.view.utilities.ApiLoginInterface;

/**
 * This class contains the startUp flow the application follow everytime it startsup
 * @author Edoardo Giacomello
 *
 */
public final class StartUpFlow extends AsyncTaskWithListener<Void, Void, Void>{
	private static StartUpFlow instance;
	private static Activity caller;
	private StartUpFlow(Activity caller) {
		if (instance==null){
			instance = this;
		}
	}
	
	
	//TODO: Complete the startup flow.
	private void run(int stage){
		switch (stage) {
		case 0:
			//0: Done
			GiveMeLogger.log("StartUp Flow Complete.");
		case 1:
			//1-Log the user
			Intent loginIntent = new Intent(caller,ApiLoginInterface.class);
	    	caller.startActivityForResult(loginIntent, ApiLoginInterface.RequestCode.LOGIN);
			break;
		case 2:
			//2-Check if all crucial variables are set properly [UserKeyRing, DB, etc]
			
			break;
		case 3:
			//3-Db Synchronization (Fetch from Gcalendar)
			
			break;
		case 4:
			//4-Collect new data from service (questions, etc)
			
			break;
		case 5:
			//5-If not already running, start the service
			
			break;
		case 6:
			//6-Propose service collected questions
			
			break;
		
		default:
			break;
		}
		
	}
	
	
	private void error(int atStage){
		GiveMeLogger.log("Error at startup flow on stage" + atStage);
	}


	@Override
	protected Void doInBackground(Void... params) {
		for (int i = 1; i < 7; i++) {
			run(i);
		}
		return null;
	}
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		run(0);
	}
}
