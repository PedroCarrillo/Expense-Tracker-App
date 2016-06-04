package com.pedrocarrillo.expensetracker.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Pedro on 9/21/2015.
 */
public class DateUtils {

    public static Calendar setDateStartOfDay(Calendar calendar){
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        return calendar;
    }

    public static Date getFirstDateOfCurrentMonth() {
        Calendar cal = setDateStartOfDay(Calendar.getInstance());
        cal.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    public static Date getLastDateOfCurrentMonth() {
        Calendar cal = setDateStartOfDay(Calendar.getInstance());
        cal.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.add(Calendar.DATE, 1);
        return cal.getTime();
    }

    public static Date getRealLastDateOfCurrentMonth() {
        Calendar cal = setDateStartOfDay(Calendar.getInstance());
        cal.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    public static Date getFirstDateOfCurrentWeek() {
        return getCalendarFirstDayOfCurrentWeek().getTime();
    }

    public static Calendar getCalendarFirstDayOfCurrentWeek() {
        Calendar cal = setDateStartOfDay(Calendar.getInstance());
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        return setDateStartOfDay(cal);
    }

    public static Date getLastDateOfCurrentWeek(){
        Calendar cal = setDateStartOfDay(Calendar.getInstance());
//        Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_WEEK);
        cal.set(Calendar.DAY_OF_WEEK, 7);
        cal.add(Calendar.DATE, 2);
        return cal.getTime();
    }

    public static Date getRealLastDateOfCurrentWeek() {
        Calendar cal = setDateStartOfDay(Calendar.getInstance());
        cal.set(Calendar.DAY_OF_WEEK, 7);
        cal.add(Calendar.DATE, 1);
        return cal.getTime();
    }

    public static Date getTomorrowDate() {
        Calendar cal = setDateStartOfDay(Calendar.getInstance());
        cal.add(Calendar.DATE, 1);
        return cal.getTime();
    }

    public static Date addDaysToDate(Date currentDate, int numDays) {
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.DATE, numDays);
        return c.getTime();
    }

    public static List<Date> getWeekDates() {
        Calendar cal = getCalendarFirstDayOfCurrentWeek();
        List<Date> dateList = new ArrayList<>();
        for (int i=0; i<7; i++) {
            dateList.add(cal.getTime());
            cal.add(Calendar.DAY_OF_WEEK, 1);
        }
        return dateList;
    }

    public static boolean isToday(Date date) {
        return  android.text.format.DateUtils.isToday(date.getTime());
    }

    public static Date getToday() {
        return setDateStartOfDay(Calendar.getInstance()).getTime();
    }

    public static int getDaysOfCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static int getNumberOfWeeksOfCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getActualMaximum(Calendar.WEEK_OF_MONTH);
    }

    public static String currentMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
    }

}
