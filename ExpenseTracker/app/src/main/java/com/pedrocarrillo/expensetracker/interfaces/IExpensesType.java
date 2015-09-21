package com.pedrocarrillo.expensetracker.interfaces;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Pedro on 9/20/2015.
 */


@IntDef({IExpensesType.MODE_EXPENSES, IExpensesType.MODE_INCOME})
@Retention(RetentionPolicy.SOURCE)
public @interface IExpensesType{
    int MODE_EXPENSES = 0;
    int MODE_INCOME = 1;
}

