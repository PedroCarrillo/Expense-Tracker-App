package com.pedrocarrillo.expensetracker.utils;

import android.widget.EditText;

/**
 * Created by Pedro on 15/9/2015.
 */
public class Util {

    public static boolean isEmptyField(EditText et) {
        if(et.getText() == null || et.getText().toString().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

}
