package com.pedrocarrillo.expensetracker.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.EditText;

import com.pedrocarrillo.expensetracker.ExpenseTrackerApp;
import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.entities.Expense;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Pedro on 15/9/2015.
 */
public class Util {

    public static String formatDateToString(Date date, String pattern) {
        DateFormat formatter = new SimpleDateFormat(pattern);
        String formattedDate = formatter.format(date);
        return formattedDate;
    }

    public static boolean isEmptyField(EditText et) {
        if(et.getText() == null || et.getText().toString().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public static String getFormattedCurrency(float number) {
        String countryCode = PreferenceManager.getDefaultSharedPreferences(ExpenseTrackerApp.getContext()).getString(ExpenseTrackerApp.getContext().getString(R.string.pref_country_key), ExpenseTrackerApp.getContext().getString(R.string.default_country));
        Locale locale = new Locale("EN", countryCode);
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        String formattedNumber = numberFormat.format(number);
        String symbol = numberFormat.getCurrency().getSymbol(locale);
        return formattedNumber.replace(symbol, symbol+" ");
    }

}
