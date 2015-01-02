package it.unozerouno.givemetime.model.events;

import it.unozerouno.givemetime.model.constraints.Constraint;
import it.unozerouno.givemetime.model.places.PlaceModel;

import java.util.Date;
import java.util.Set;

import android.text.format.Time;

public class EventModel {
	private String ID;
	private String name; 
	private Time startingDateTime;
	private Time endingDateTime;
	private Set<Constraint> constraints;
	private PlaceModel place;
	private Boolean doNotDisturb;
	private EventCategory category;
	
	
}
