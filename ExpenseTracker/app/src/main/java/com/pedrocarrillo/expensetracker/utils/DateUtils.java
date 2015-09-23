package com.pedrocarrillo.expensetracker.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Pedro on 9/21/2015.
 */
public class DateUtils {

    private static Calendar setDateStartOfDay(Calendar calendar){
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

    public static Date getFirstDateOfCurrentWeek() {
        return getCalendarFirstDayOfCurrentWeek().getTime();
    }

    public static Calendar getCalendarFirstDayOfCurrentWeek() {
        Calendar cal = setDateStartOfDay(Calendar.getInstance());
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        return cal;
    }

    public static Date getLastDateOfCurrentWeek(){
        Calendar cal = setDateStartOfDay(Calendar.getInstance());
//        Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_WEEK);
        cal.set(Calendar.DAY_OF_WEEK, 7);
        cal.add(Calendar.DATE, 2);
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

    public static Date getToday() {
        return setDateStartOfDay(Calendar.getInstance()).getTime();
    }

}
