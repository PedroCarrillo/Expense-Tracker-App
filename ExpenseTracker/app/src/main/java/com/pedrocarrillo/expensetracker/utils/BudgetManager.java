package com.pedrocarrillo.expensetracker.utils;

import android.preference.PreferenceManager;

import com.pedrocarrillo.expensetracker.ExpenseTrackerApp;
import com.pedrocarrillo.expensetracker.R;

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

    public void saveBudget(float budget) {
        PreferenceHelper.saveFloatPreference(ExpenseTrackerApp.getContext().getString(R.string.budget_shared_key), budget);
    }

    public void saveIntendedSavings(float savings) {
        PreferenceHelper.saveFloatPreference(ExpenseTrackerApp.getContext().getString(R.string.intented_savings_key), savings);
    }

    public float getCurrentBudget() {
        return PreferenceManager.getDefaultSharedPreferences(ExpenseTrackerApp.getContext()).getFloat(ExpenseTrackerApp.getContext().getString(R.string.budget_shared_key), 0);
    }

    public float getIntendedSavings() {
        return PreferenceManager.getDefaultSharedPreferences(ExpenseTrackerApp.getContext()).getFloat(ExpenseTrackerApp.getContext().getString(R.string.intented_savings_key), 0);
    }

    public float getMonthlyAvailableBudget() {
        float currentBudget = getCurrentBudget();
        float intendedSavings = getIntendedSavings();
        if (currentBudget != 0 && intendedSavings != 0) {
            return currentBudget - intendedSavings;
        } else {
            return 0;
        }
    }

    public float getDailyAvailableBudget() {
        float monthlyAvailable = getMonthlyAvailableBudget();
        if (monthlyAvailable != 0) {
            int totalDays = DateUtils.getDaysOfCurrentMonth();
            return monthlyAvailable / totalDays;
        } else {
            return 0;
        }
    }

    public float getWeeklyAvailableBudget() {
        float monthlyAvailable = getMonthlyAvailableBudget();
        if (monthlyAvailable != 0) {
            int totalWeeks = DateUtils.getNumberOfWeeksOfCurrentMonth();
            return getMonthlyAvailableBudget() / totalWeeks;
        } else {
            return 0;
        }
    }

}
