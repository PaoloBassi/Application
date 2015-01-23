package it.unozerouno.givemetime.utils;

import android.app.Activity;
import android.content.Context;

public abstract class TaskListener<ReturnType> {
	private Context context;
	public TaskListener(Context caller){
		this.context = caller;
	}
	public abstract void onTaskResult(ReturnType... results);
}
