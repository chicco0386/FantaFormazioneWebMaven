package it.zeze.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.core.Locale;

public class DateUtil {

    private static final Log log = LogFactory.getLog(DateUtil.class);

    public static Date getDateWithPattern(Date date, String pattern) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date dateWithoutTime = null;

        try {
            dateWithoutTime = dateFormat.parse(dateFormat.format(date));
        } catch (Exception e) {
            log.error("getDateWithPattern, Exception caught", e);
        }

        return dateWithoutTime;

    }

    public static String getDateAsString(Date date, String pattern) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        String dateString = null;

        try {
            dateString = dateFormat.format(date);
        } catch (Exception e) {
            log.error("getDateAsString, Exception caught", e);
        }

        return dateString;

    }

    public static Date getDateWithPatternFromString(String dateString, String pattern) throws ParseException {

        return DateUtil.getDateWithPatternFromString(dateString, pattern, false);

    }
    
    public static Date getDateWithPatternFromString(String dateString, String pattern, java.util.Locale lang) throws ParseException {

    	Locale.instance().setDefault(lang);
        return DateUtil.getDateWithPatternFromString(dateString, pattern, false);

    }

    public static Date getDateWithPatternFromString(String dateString, String pattern, boolean isLenient)
            throws ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        dateFormat.setLenient(isLenient);
        Date date = null;

        date = dateFormat.parse(dateString);

        return date;

    }

    /*
     * 
     */

    public static Date getNext(String hourOfDay, String minute) throws NumberFormatException {
        Date nextDate = null;
        Calendar calendarNow = Calendar.getInstance();
        Calendar calendarTemp = Calendar.getInstance();
        calendarTemp.setTime(calendarNow.getTime());
        try {
            if (hourOfDay != null && !hourOfDay.isEmpty()) {
                calendarTemp.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hourOfDay));
            }
            if (minute != null && !minute.isEmpty()) {
                calendarTemp.set(Calendar.MINUTE, Integer.valueOf(minute));
            }
            if (calendarTemp.before(calendarNow)) {
                calendarTemp.add(Calendar.DAY_OF_MONTH, 1);
            }
            nextDate = calendarTemp.getTime();
        } catch (NumberFormatException e) {
            log.error("getNext, errore durante la conversione di un valore", e);
            throw new NumberFormatException("Errore durante la conversione di un valore. Message: " + e.getMessage());
        }

        return nextDate;
    }
}
