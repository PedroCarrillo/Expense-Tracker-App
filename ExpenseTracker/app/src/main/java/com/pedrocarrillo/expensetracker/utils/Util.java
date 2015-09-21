package com.pedrocarrillo.expensetracker.utils;

import android.widget.EditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

}
