package it.unozerouno.givemetime.model.constraints;

import java.util.Set;
//TODO: Implement this class
public class ComplexConstraint extends Constraint {
	private Set<Constraint> constraints;

	@Override
	public Boolean isActive() {
	return false;	
	}

}
