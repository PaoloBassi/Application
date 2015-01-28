package it.unozerouno.givemetime.model.questions;

import it.unozerouno.givemetime.controller.fetcher.DatabaseManager;
import it.unozerouno.givemetime.controller.fetcher.places.LocationFetcher;
import it.unozerouno.givemetime.controller.fetcher.places.PlaceFetcher;
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
	private PlaceModel place;
	
	

	public LocationMismatchQuestion(Context context, EventInstanceModel event,
			Location locationWhenGenerated, Time generationTime) {
		super(context,generationTime);
		this.event = event;
		this.locationWhenGenerated = locationWhenGenerated;
	}







	/**
	 * Retrieve the nearest placemodel from the coordinates in which this question has been generated.
	 * Note that if the placemodel is still unknown, it will be fetched from PlacesAPI
	 * @return
	 */
	public void buildPlaceModel(final OnDatabaseUpdatedListener<PlaceModel> resultListener){
		if (place == null){
			//If the place is still unknown then we have to fetch it asynchronously from DatabaseManager (thus PlaceFetcher)
			OnDatabaseUpdatedListener<PlaceModel> placeModelConverter = new OnDatabaseUpdatedListener<PlaceModel>() {
				@Override
				protected void onUpdateFinished(PlaceModel placeModel) {
					//When the placemodel is ready we store it and notify back to caller
					setPlace(placeModel);
					resultListener.updateFinished(placeModel);
				}
			};
			DatabaseManager.getPlaceModelFromLocation(locationWhenGenerated, placeModelConverter);
		}else{
			//If the place is still present we simply notify it back
			resultListener.updateFinished(place);
		}
	}


	


  
	/**
	 * Return the STORED placemodel associated with this question.
	 * NOTE THAT if you havn't previously called "buildPlaceModel" this function will return null, so call it only when you are sure that this question contains a model
	 * @return
	 */
	public PlaceModel getPlace() {
		return place;
	}







	public void setPlace(PlaceModel place) {
		this.place = place;
	}







	public EventInstanceModel getEvent() {
		return event;
	}







	public void setEvent(EventInstanceModel event) {
		this.event = event;
	}







	public Location getLocationWhenGenerated() {
		return locationWhenGenerated;
	}







	public void setLocationWhenGenerated(Location locationWhenGenerated) {
		this.locationWhenGenerated = locationWhenGenerated;
	}
	
	
}
