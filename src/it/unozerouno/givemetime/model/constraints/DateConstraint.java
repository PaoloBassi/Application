package it.unozerouno.givemetime.model.constraints;

import java.sql.Date;

/**
 * Represent a Date interval
 * @author Edoardo Giacomello
 *
 */
public class DateConstraint extends Constraint{
	private Date startingDate;
	private Date endingDate;
	
	/**
	 * Create a new Date Interval starting from startingDate (included) and finishing at endingDate (excluded)
	 * @param startingDate
	 * @param endingDate
	 */
	public DateConstraint(Date startingDate, Date endingDate) {
		this.startingDate = startingDate;
		this.endingDate = endingDate;
	}
	@Override
	public Boolean isActive() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
