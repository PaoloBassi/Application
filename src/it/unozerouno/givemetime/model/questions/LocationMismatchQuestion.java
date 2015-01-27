package it.unozerouno.givemetime.model.questions;

import it.unozerouno.givemetime.model.events.EventInstanceModel;
import it.unozerouno.givemetime.model.places.PlaceModel;
import it.unozerouno.givemetime.view.utilities.OnDatabaseUpdatedListener;
import android.content.Context;
import android.location.Location;
import android.text.format.Time;

/**
 * This type of service-generated question is made when the user is not where it is supposed to be according to current scheduled event 
 * @author Edoardo Giacomello <edoardo.giacomello1990@gmail.com>
 *
 */
public class LocationMismatchQuestion extends QuestionModel{
	private EventInstanceModel event;
	private Location locationWhenGenerated;
	private Time generationTime;
	
	

	public LocationMismatchQuestion(Context context, EventInstanceModel event,
			Location locationWhenGenerated, Time generationTime) {
		super(context);
		this.event = event;
		this.locationWhenGenerated = locationWhenGenerated;
		this.generationTime = generationTime;
	}








	public void getPlaceModel(OnDatabaseUpdatedListener<PlaceModel> result){
		//TODO: Implement this
	}
	
	
}
