package it.unozerouno.givemetime.model.questions;

import android.content.Context;
import android.text.format.Time;

public abstract class QuestionModel {
	private Context context;
	private Time generationTime;
	
	

	public QuestionModel(Context context, Time generationTime) {
		super();
		this.context = context;
		this.generationTime = generationTime;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public Time getGenerationTime() {
		return generationTime;
	}

	public void setGenerationTime(Time generationTime) {
		this.generationTime = generationTime;
	}
}
