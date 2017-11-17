/**
 * 
 */
package com.cts.aws.poc.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * @author Azharkhan
 *
 */
public abstract class DateUtils {
	
	public static final SimpleDateFormat MYSQL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	public static Date getDateFromLocalDate(LocalDate input) {
		
		return Date.from(input.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}
	
	public static LocalDate gregorianCalendarToLocalDate(XMLGregorianCalendar gregorianCalendar) {
		
		return LocalDate.of(gregorianCalendar.getYear(), gregorianCalendar.getMonth(), gregorianCalendar.getDay());
	}
}