package it.unozerouno.givemetime.utils;

import android.database.CursorJoiner.Result;
import android.os.AsyncTask;

public abstract class AsyncTaskWithListener <Params, Progress, Result>  extends AsyncTask<Params, Progress, Result>{
	TaskListener<Result> taskListener;
	public void setListener(TaskListener<Result> listener){
		taskListener = listener;
	}
	public void setResult(Result... results){
		taskListener.onTaskResult(results);
	}
}
