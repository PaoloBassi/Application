package it.unozerouno.givemetime.utils;

import android.app.Activity;

public abstract class TaskListener<ReturnType> {
	private Activity activity;
	public TaskListener(Activity activity){
		this.activity = activity;
	}
	public abstract void onTaskResult(ReturnType... results);
}
