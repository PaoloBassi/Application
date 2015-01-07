package it.unozerouno.givemetime.utils;

import it.unozerouno.givemetime.view.main.fragments.DebugFragment;

public class GiveMeLogger {
	public static void log(String msg){
		if(!DebugFragment.log(msg)){
		System.out.println("Logger: "+ msg);
		}
	}
}
