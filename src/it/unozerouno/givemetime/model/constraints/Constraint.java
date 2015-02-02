package it.unozerouno.givemetime.model.constraints;

/**
 * Represent an date or time interval
 * @author Edoardo Giacomello
 *
 */
public abstract class Constraint {
	private int id;
	
	public Constraint() {
		//When the id is not set, it value is -1, telling the DatabaseManager that it's a new constraint to be added;
		id = -1;
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
	public abstract Boolean isActive();
}
