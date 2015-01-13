package it.unozerouno.givemetime.model.places;

import it.unozerouno.givemetime.model.constraints.DayConstraint;
import it.unozerouno.givemetime.model.constraints.TimeConstraint;

import java.util.Set;

import android.location.Location;


public class PlaceModel{
	private String ID;
	private String Name;
	private Location location;
	private Set<TimeConstraint> openingTime;
	private Set<DayConstraint> openingDays;
	
	public void setLocation(Location location) {
		this.location = location;
	}
	public Location getLocation() {
		return location;
	}
	

}
