package com.pedrocarrillo.expensetracker.interfaces;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Pedro on 9/22/2015.
 */
@IntDef({IDateMode.MODE_TODAY, IDateMode.MODE_WEEK, IDateMode.MODE_MONTH})
@Retention(RetentionPolicy.SOURCE)
public @interface IDateMode {
    int MODE_TODAY = 100;
    int MODE_WEEK = 101;
    int MODE_MONTH = 102;
    String DATE_TODAY_TAG = "_today";
    String DATE_WEEK_TAG = "_week";
    String DATE_MONTH_TAG = "_month";
    String DATE_MODE_TAG = "_date_user_mode";
}

