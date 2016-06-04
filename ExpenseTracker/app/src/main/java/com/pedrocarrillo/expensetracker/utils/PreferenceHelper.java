package com.pedrocarrillo.expensetracker.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.pedrocarrillo.expensetracker.ExpenseTrackerApp;
import com.pedrocarrillo.expensetracker.R;

/**
 * Created by pedrocarrillo on 6/3/16.
 */

public class PreferenceHelper {

    public static final void saveFloatPreference(String key, float value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ExpenseTrackerApp.getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

}
