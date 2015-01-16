package it.unozerouno.givemetime.model.places;

import it.unozerouno.givemetime.model.constraints.DayConstraint;
import it.unozerouno.givemetime.model.constraints.TimeConstraint;

import java.util.Set;

import android.location.Location;


public class PlaceModel{
	private String ID;
	private String Name;
	private String info;
	private Location location;
	private Set<TimeConstraint> openingTime;
	private Set<DayConstraint> openingDays;
	
	public PlaceModel(String iD, String name, Location location) {
		super();
		ID = iD;
		Name = name;
		this.location = location;
	}

	public void setLocation(String ID, Location location) {
		this.ID = ID;
		this.location = location;
	}
	public Location getLocation() {
		return location;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public Set<TimeConstraint> getOpeningTime() {
		return openingTime;
	}
	public void setOpeningTime(Set<TimeConstraint> openingTime) {
		this.openingTime = openingTime;
	}
	public Set<DayConstraint> getOpeningDays() {
		return openingDays;
	}
	public void setOpeningDays(Set<DayConstraint> openingDays) {
		this.openingDays = openingDays;
	}
	
	

}
