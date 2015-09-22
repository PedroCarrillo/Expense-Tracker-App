package com.pedrocarrillo.expensetracker.utils;

import java.util.Calendar;
import java.util.Date;

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
        Calendar cal = setDateStartOfDay(Calendar.getInstance());
        cal.set(Calendar.DAY_OF_WEEK, Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_WEEK));
        return cal.getTime();
    }

    public static Date getLastDateOfCurrentWeek(){
        Calendar cal = setDateStartOfDay(Calendar.getInstance());
        cal.set(Calendar.DAY_OF_WEEK, Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_WEEK));
        cal.add(Calendar.DATE, 1);
        return cal.getTime();
    }

    public static Date getTomorrowDate() {
        Calendar cal = setDateStartOfDay(Calendar.getInstance());
        cal.add(Calendar.DATE, 1);
        return cal.getTime();
    }

    public static Date getToday() {
        return setDateStartOfDay(Calendar.getInstance()).getTime();
    }

}
