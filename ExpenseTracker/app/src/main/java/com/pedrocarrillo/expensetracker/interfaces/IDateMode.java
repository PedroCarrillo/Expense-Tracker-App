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
    String DATE_MODE_TAG = "_date_user_mode";
}

