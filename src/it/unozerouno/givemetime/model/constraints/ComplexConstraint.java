package it.unozerouno.givemetime.model.constraints;

import java.util.ArrayList;
import java.util.List;

import android.text.format.Time;
/**
 * This class Represents an intersection between other types of Constraints 
 * example: the sentence "from Friday at 10:00 to Saturday at 20:00" is a complex constraint
 * made of a DayConstraint(Friday, Saturday) and a TimeConstraint (10:00,20:00)
 * @author Edoardo Giacomello <edoardo.giacomello1990@gmail.com>
 *
 */
public class ComplexConstraint extends Constraint {
	private List<Constraint> constraints;

	public ComplexConstraint() {
		constraints = new ArrayList<Constraint>();
	}
	public synchronized void addConstraint(Constraint newConstraint){
		constraints.add(newConstraint);
	}
	@Override
	public Boolean isActive() {
	for (Constraint constraint : constraints) {
		if(!constraint.isActive()){return false;}
	}
	return true;
	}
	
	public Boolean isActive(Time when){
		for (Constraint constraint : constraints) {
			if (constraint instanceof DateConstraint) {
				DateConstraint dateConstr = (DateConstraint) constraint;
				if (!dateConstr.isActive(when)){
					return false;
				}}
			else if (constraint instanceof DayConstraint) {
					DayConstraint dayConstr = (DayConstraint) constraint;
					if (!dayConstr.isActive(when.weekDay)){
						return false;
					}
			}
			else if (constraint instanceof TimeConstraint) {
					TimeConstraint timeConstr = (TimeConstraint) constraint;
					if (!timeConstr.isActive(when)){
						return false;
					}
			}
	}
			return true;
		}
}

