package com.pedrocarrillo.expensetracker.utils;

/**
 * Created by pedrocarrillo on 6/3/16.
 */
public class BudgetManager {
    private static BudgetManager ourInstance = new BudgetManager();

    public static BudgetManager getInstance() {
        return ourInstance;
    }

    private BudgetManager() {
    }
}
