package it.unozerouno.givemetime.model.constraints;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonObject;

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
	
	
	
	public List<Constraint> getConstraints() {
		return constraints;
	}
	
	public void setConstraints(List<Constraint> constraints) {
		this.constraints = constraints;
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
	
	/**
	 * Parses a "periods" array result from PlacesAPI and generates relative constraints.
	 * @param placesOpeningTime
	 * @return
	 * @throws JSONException 
	 */
	public static List<ComplexConstraint> parseJSONResult(JSONArray placesOpeningTime) throws JSONException{
		ArrayList<ComplexConstraint> constraintList = new ArrayList<ComplexConstraint>();
		
		for(int i=0; i < placesOpeningTime.length(); i++){
			JSONObject openingPeriod = placesOpeningTime.getJSONObject(i);
			JSONObject opens = openingPeriod.getJSONObject("open");
			if (!openingPeriod.isNull("close")){
				//Has normal periods
				JSONObject closes = openingPeriod.getJSONObject("close");
				int openingDay = Integer.parseInt(opens.getString("day"));
				int closingDay = Integer.parseInt(closes.getString("day"));
				String openingTime = opens.getString("time");
				String openHour = openingTime.substring(0, 2);
				String openMinutes = openingTime.substring(2, 4);
				String closingTime = closes.getString("time");
				String closeHour = closingTime.substring(0, 2);
				String closeMinutes = closingTime.substring(2, 4);
				
				DayConstraint dayConstraint = new DayConstraint(openingDay, closingDay);
				TimeConstraint timeConstraint = new TimeConstraint(Integer.parseInt(openHour), Integer.parseInt(openMinutes), 0, Integer.parseInt(closeHour), Integer.parseInt(closeMinutes), 0);
				ComplexConstraint intersection = new ComplexConstraint();
				intersection.addConstraint(timeConstraint);
				intersection.addConstraint(dayConstraint);
				constraintList.add(intersection);
				
				
			} else {
				//It may be always open
				if (opens.getInt("day") == 0 && opens.getString("time") == "0000"){
					//It's always open
					DayConstraint alwaysOpenConstraint = new DayConstraint(0, 6);
					ComplexConstraint alwaysConstraint = new ComplexConstraint();
					alwaysConstraint.addConstraint(alwaysOpenConstraint);
					constraintList.add(alwaysConstraint);
					
				} else {
					//Something gone wrong
					throw new JSONException("Found opening hour with no close, but doesn't open at sunday midnight");
				}
			}
			
			
		}
		return constraintList;
	}
	
}

