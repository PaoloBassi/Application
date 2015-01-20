package it.unozerouno.givemetime.model.events;
/**
 * This class represents the category for an event, encapsulating some default properties about an event
 * I.e: If the category is "Work", we know that the event will not be movable and the notifications will be disabled by default.
 * @author Edoardo Giacomello <edoardo.giacomello1990@gmail.com>
 *
 */
public class EventCategory {
	private String name;
	private boolean default_movable;
	private boolean default_donotdisturb;
	private boolean defaultCategory;
	/**
	 * Creates a new Event Category. The parameter "name" cannot be null.
	 * @param name
	 * @param default_movable
	 * @param default_donotdisturb
	 */
	public EventCategory(String name, boolean default_movable,
			boolean default_donotdisturb) {
		super();
		this.name = name;
		this.default_movable = default_movable;
		this.default_donotdisturb = default_donotdisturb;
		this.defaultCategory=false;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isDefault_movable() {
		return default_movable;
	}
	public void setDefault_movable(boolean default_movable) {
		this.default_movable = default_movable;
	}
	public boolean isDefault_donotdisturb() {
		return default_donotdisturb;
	}
	public void setDefault_donotdisturb(boolean default_donotdisturb) {
		this.default_donotdisturb = default_donotdisturb;
	}
	
	
	public boolean isDefaultCategory() {
		return defaultCategory;
	}
	public void setDefaultCategory(boolean defaultCategory) {
		this.defaultCategory = defaultCategory;
	}
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof EventCategory)) return false;
		EventCategory other = (EventCategory) o;
		return (this.name == other.getName() && this.isDefault_donotdisturb() == other.isDefault_donotdisturb() && this.isDefault_movable() == other.isDefault_donotdisturb());
	}
	
	
}
