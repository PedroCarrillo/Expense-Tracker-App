package com.pedrocarrillo.expensetracker.utils;

import android.content.Intent;
import android.util.SparseBooleanArray;

import com.pedrocarrillo.expensetracker.ExpenseTrackerApp;
import com.pedrocarrillo.expensetracker.entities.Category;
import com.pedrocarrillo.expensetracker.entities.Expense;
import com.pedrocarrillo.expensetracker.interfaces.IDateMode;
import com.pedrocarrillo.expensetracker.interfaces.IExpensesType;
import com.pedrocarrillo.expensetracker.widget.ExpensesWidgetProvider;
import com.pedrocarrillo.expensetracker.widget.ExpensesWidgetService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author  pcarrillo on 16/10/2015.
 */
public class ExpensesManager {

    private List<Expense> mExpensesList = new ArrayList<>();
    private SparseBooleanArray mSelectedExpensesItems = new SparseBooleanArray();
    private boolean isMainExpenses = false;

    private static ExpensesManager ourInstance = new ExpensesManager();

    public static ExpensesManager getInstance() {
        return ourInstance;
    }

    private ExpensesManager() {
    }

    public void setExpensesList(Date dateFrom, Date dateTo, @IExpensesType int type,  Category category) {
        mExpensesList = Expense.getExpensesList(dateFrom, dateTo, type, category);
        isMainExpenses = false;
        resetSelectedItems();
    }

    public void setExpensesListByDateMode(@IDateMode int mCurrentDateMode) {
        switch (mCurrentDateMode) {
            case IDateMode.MODE_TODAY:
                mExpensesList = Expense.getTodayExpenses();
                break;
            case IDateMode.MODE_WEEK:
                mExpensesList = Expense.getWeekExpenses();
                break;
            case IDateMode.MODE_MONTH:
                mExpensesList = Expense.getMonthExpenses();
                break;
        }
        isMainExpenses = true;
    }

    public List<Expense> getExpensesList() {
        return mExpensesList;
    }

    public SparseBooleanArray getSelectedExpensesItems() {
        return mSelectedExpensesItems;
    }

    public void resetSelectedItems() {
        mSelectedExpensesItems.clear();
    }

    public List<Integer> getSelectedExpensesIndex() {
        List<Integer> items = new ArrayList<>(mSelectedExpensesItems.size());
        for (int i = 0; i < mSelectedExpensesItems.size(); ++i) {
            items.add(mSelectedExpensesItems.keyAt(i));
        }
        return items;
    }

    public void eraseSelectedExpenses() {
        boolean isToday = false;
        List<Expense> expensesToDelete = new ArrayList<>();
        for (int position : getSelectedExpensesIndex()) {
            Expense expense = mExpensesList.get(isMainExpenses ? position - 1 : position);
            expensesToDelete.add(expense);
            Date expenseDate = expense.getDate();
            // update widget if the expense is created today
            if (DateUtils.isToday(expenseDate)) {
                isToday = true;
            }
        }
        if (isToday) {
            Intent i = new Intent(ExpenseTrackerApp.getContext(), ExpensesWidgetProvider.class);
            i.setAction(ExpensesWidgetService.UPDATE_WIDGET);
            ExpenseTrackerApp.getContext().sendBroadcast(i);
        }
        RealmManager.getInstance().delete(expensesToDelete);
    }

    public void setSelectedItems(SparseBooleanArray selectedItems) {
        this.mSelectedExpensesItems = selectedItems;
    }

}
