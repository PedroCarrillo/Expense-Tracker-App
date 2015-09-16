package com.pedrocarrillo.expensetracker;

import android.app.Application;
import android.content.Context;

/**
 * Created by Pedro on 15/9/2015.
 */
public class ExpenseTrackerApp extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }

}
