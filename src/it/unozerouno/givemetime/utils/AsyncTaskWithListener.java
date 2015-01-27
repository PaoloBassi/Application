package it.unozerouno.givemetime.utils;

import java.util.ArrayList;

import android.database.CursorJoiner.Result;
import android.os.AsyncTask;
/**
 * This class is a Simple AsyncTask with the following addition, made for simplify the writing of code:
 * -Listener for Task Results: use setListener(Tasklistener<Result>) to add a TaskListener for writing what to do with the result directly on the calling class
 * 
 * @author Edoardo Giacomello
 * @param <Params>
 * @param <Progress>
 * @param <Result>
 * @see AsyncTask, {@link AsyncTaskWithListener}
 */
public abstract class AsyncTaskWithListener <Params, Progress, ResultType>  extends AsyncTask<Params, Progress, ResultType>{
	//private ArrayList<TaskListener<Result>> taskListeners = new ArrayList<TaskListener<Result>>();
	TaskListener<ResultType> listener;
	public void setListener(TaskListener<ResultType> listener){
	//	taskListeners.add(listener);
		this.listener = listener;
	}
	public void setResult(ResultType... results){
//		for (TaskListener<ResultType> listener : taskListeners) {
//			listener.onTaskResult(results);
//		}
		listener.onTaskResult(results);
	}
}
