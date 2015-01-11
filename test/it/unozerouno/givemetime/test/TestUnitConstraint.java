/**
 * 
 */
package it.unozerouno.givemetime.test;

import it.unozerouno.givemetime.model.constraints.TimeConstraint;
import it.unozerouno.givemetime.utils.GiveMeLogger;
import android.text.format.Time;


/**
 * @author Edoardo Giacomello <edoardo.giacomello1990@gmail.com>
 *
 */
public class TestUnitConstraint {

	/**
	 * @throws java.lang.Exception
	 */
	
	public static void setUpBeforeClass() throws Exception {
		
	}

	public void test() {
		Time start = new Time();
		start.setToNow();
		start.set(0, 0, 0, start.monthDay, start.month, start.year);
		Time end = new Time ();
		end.setToNow();
		end.set(8, 0, 8, end.monthDay, end.month, end.year);
		TimeConstraint constraint = new TimeConstraint(start, end);
		GiveMeLogger.log(constraint.isActive().toString());
		Time anotherTime = new Time();
		anotherTime.set(0,0,12,1,2,2015);
		GiveMeLogger.log(constraint.isActive(anotherTime).toString());
	}

}
