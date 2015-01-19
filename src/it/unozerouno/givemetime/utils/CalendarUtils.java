package it.unozerouno.givemetime.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.text.format.Time;

public class CalendarUtils {
	/**
	 * Transform a long representation of a Date into Time 
	 * @param dateLong: date in milliseconds from epoch
	 * @return time representing the date
	 */
	public static Time longToTime(long dateLong){
		Time time = new Time();
		time.set(dateLong);
		return time;
	}

	/**
	 * Parse the convetional format date into a calendar representation for EventInstanceModel
	 * @param formatDate: date retrieved from repeated events in the format yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
	 * @return calendar object containing the formatDate information
	 * @throws ParseException
	 */
	
	public static Calendar formatDateToCalendar(String formatDate) throws ParseException{
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
		sdf.setCalendar(calendar);
		calendar.setTime(sdf.parse(formatDate));
		return calendar;
	}
	
	/**
	 * transform a Time object into an equivalent Calendar (used in display events)
	 * @param time
	 * @return
	 */
	public static Calendar timeToCalendar(Time time){
		long millis = time.toMillis(false);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(millis);
		return cal;
	}
	
	/**
	 * adds trailing zeroes in case of hour or minute from 0 to 9
	 * @return the string representation of hour and minute HH:mm
	 */
	public static String formatHour(int hour, int minute){
		String strHour = String.valueOf(hour);
		String strMinute = String.valueOf(minute);
		if (strHour.length() == 1){
			strHour = "0" + strHour;
		}
		if (strMinute.length() == 1){
			strMinute = "0" + strMinute;
		}
		return strHour + ":" + strMinute;
	}

	
	/**
	 * when creating a new event, set the minute of the starting event to 0 or 30, after the current minute values
	 * @param minutes
	 * @return 30 if current minute value is between 0 or 30, 0 otherwise. The hour is incremented accordingly
	 */
	
	public static void approximateMinutes(Time time){
		if (time.minute >= 0 && time.minute <= 30){
			time.minute = 30;
		} else {
			time.minute = 0;
			time.hour += 1;
		}
	}
}
