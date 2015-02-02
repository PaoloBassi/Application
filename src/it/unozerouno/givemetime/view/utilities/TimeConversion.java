package it.unozerouno.givemetime.view.utilities;

import android.text.format.Time;

public class TimeConversion {
 public static String timeToString(Time time, boolean getTime, boolean showSeconds, boolean getDate, boolean showYear){
	 if (time == null) return "";
	 StringBuffer timeString = new StringBuffer();
	 StringBuffer dateString = new StringBuffer();
	 
	 if(getTime){
	 dateString.append(time.monthDay);
	 dateString.append("/");
	 dateString.append(time.month+1);
	 if(showYear){
	 dateString.append("/");
	 dateString.append(time.year);
	 }
	 }
	 
	 if (getDate){
	 timeString.append(time.hour);
	 timeString.append(":");
	 timeString.append(time.minute);
	 if(showSeconds){
		 timeString.append(":");
		 timeString.append(time.second);
	 }
	 }
	 
	 if (getTime && !getDate) return timeString.toString();
	 if (!getTime && getDate) return dateString.toString();
	 if (getTime && getDate) return timeString.toString() + " " +dateString.toString();
	 return "";
 }
 
 public static long getNow(){
	 Time now = new Time();
	 now.setToNow();
	 return now.toMillis(false);
 }
}
