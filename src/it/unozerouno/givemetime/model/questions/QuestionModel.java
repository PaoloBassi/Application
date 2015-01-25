package it.unozerouno.givemetime.model.questions;

import android.content.Context;

public abstract class QuestionModel {
	private Context context;

	public QuestionModel(Context context) {
		super();
		this.context = context;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	
}
