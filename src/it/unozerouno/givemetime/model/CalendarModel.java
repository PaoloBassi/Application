package it.unozerouno.givemetime.model;


public class CalendarModel {
	private String id;
	private String owner;
	private String name;
	private int color;
	public CalendarModel(String id, String owner, String name, int color) {
		this.id = id;
		this.owner = owner;
		this.name = name;
		this.color = color;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		name = name;
	}
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
	
	
}
