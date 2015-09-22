package com.pedrocarrillo.expensetracker.entities;

import android.support.annotation.Nullable;
import android.util.Log;

import com.pedrocarrillo.expensetracker.interfaces.IExpensesType;
import com.pedrocarrillo.expensetracker.utils.DateUtils;
import com.pedrocarrillo.expensetracker.utils.RealmManager;
import com.pedrocarrillo.expensetracker.utils.Util;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by pcarrillo on 21/09/2015.
 */
public class Expense extends RealmObject {

    private String description;
    private Date date;
    private @IExpensesType int type;
    private Category category;
    private long total;

    public Expense() {
    }

    public Expense(String description, Date date, @IExpensesType int type, Category category, long total) {
        this.description = description;
        this.date = date;
        this.type = type;
        this.category = category;
        this.total = total;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

        public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public static List<Expense> getExpenses() {
        RealmResults<Expense> expenses = RealmManager.getInstance().getRealmInstance().where(Expense.class)
//                .equalTo("type", type)
                .findAll();
        return expenses;
    }

    public static float getWeekTotalExpenses() {
        Date startWeek = DateUtils.getFirstDateOfCurrentWeek();
        Date endWeek = DateUtils.getLastDateOfCurrentWeek();
        Log.e("getWeekTotalExpenses", " " + startWeek);
        Log.e("getWeekTotalExpenses", " " + endWeek);
        RealmResults<Expense> totalExpense = getExpensesList(startWeek, endWeek, IExpensesType.MODE_EXPENSES);
        RealmResults<Expense> totalIncome = getExpensesList(startWeek, endWeek, IExpensesType.MODE_INCOME);
        float total = totalExpense.sum("total").floatValue() - totalIncome.sum("total").floatValue();
        return total;
    }

    public static float getMonthTotalExpenses(){
        Date startMonth = DateUtils.getFirstDateOfCurrentMonth();
        Date endMonth = DateUtils.getLastDateOfCurrentMonth();
        Log.e("getThisMonthExpenses", " " + startMonth);
        Log.e("getThisMonthExpenses", " " + endMonth);
        RealmResults<Expense> totalExpense = getExpensesList(startMonth, endMonth, IExpensesType.MODE_EXPENSES);
        RealmResults<Expense> totalIncome = getExpensesList(startMonth, endMonth, IExpensesType.MODE_INCOME);
        float total = totalExpense.sum("total").floatValue() - totalIncome.sum("total").floatValue();
        return total;
    }

    public static float getTodayTotalExpenses(){
        Date today = DateUtils.getToday();
        Date tomorrow = DateUtils.getTomorrowDate();
        Log.e("getTodaysTotal", " " + today);
        Log.e("getTodaysTotal", " " + tomorrow);
        RealmResults<Expense> totalExpense = getExpensesList(today, tomorrow, IExpensesType.MODE_EXPENSES);
        RealmResults<Expense> totalIncome = getExpensesList(today, tomorrow, IExpensesType.MODE_INCOME);
        float total = totalExpense.sum("total").floatValue() - totalIncome.sum("total").floatValue();
        return total;
    }

    public static List<Expense> getTodayExpenses() {
        Date today = DateUtils.getToday();
        Date tomorrow = DateUtils.getTomorrowDate();
        return getExpensesList(today, tomorrow, null);
    }

    public static List<Expense> getWeekExpenses() {
        Date startWeek = DateUtils.getFirstDateOfCurrentWeek();
        Date endWeek = DateUtils.getLastDateOfCurrentWeek();
        return getExpensesList(startWeek, endWeek, null);
    }

    public static List<Expense> getMonthExpenses() {
        Date startMonth = DateUtils.getFirstDateOfCurrentMonth();
        Date endMonth = DateUtils.getLastDateOfCurrentMonth();
        return getExpensesList(startMonth, endMonth, null);
    }

    public static RealmResults<Expense> getExpensesList(Date dateFrom, Date dateTo, @IExpensesType Integer type) {
        RealmQuery<Expense> realmQuery = RealmManager.getInstance().getRealmInstance()
                .where(Expense.class)
                .between("date", dateFrom, dateTo);
        if (type != null) realmQuery.equalTo("type", type);
        return realmQuery.findAll();
    }

}
