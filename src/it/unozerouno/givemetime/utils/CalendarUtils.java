package it.unozerouno.givemetime.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CalendarUtils {
	/**
	 * Transform a long representation of a Date into a Calendar
	 * @param dateLong: date in milliseconds from epoch
	 * @return calendar object representing the date
	 */
	public static Calendar longToCalendar(long dateLong){
		Date date = new Date(dateLong);
		Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
		cal.setTime(date);
		return cal;
	}

	/**
	 * Parse the convetional format date into a calendar representation for EventModel
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
}
