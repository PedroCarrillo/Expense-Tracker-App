package com.pedrocarrillo.expensetracker.entities;


import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.IntegerRes;

import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.interfaces.IDateMode;
import com.pedrocarrillo.expensetracker.interfaces.IExpensesType;
import com.pedrocarrillo.expensetracker.utils.DateUtils;
import com.pedrocarrillo.expensetracker.utils.RealmManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

/**
 * Created by pcarrillo on 21/09/2015.
 */
public class Expense extends RealmObject {

    @PrimaryKey
    private String id;

    private String description;
    private Date date;
    private @IExpensesType int type;
    private Category category;
    private float total;

    public Expense() {
    }

    public Expense(String description, Date date, @IExpensesType int type, Category category, float total) {
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

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static float getTotalExpensesByDateMode(@IDateMode int dateMode){
        Date dateFrom;
        Date dateTo;
        switch (dateMode) {
            case IDateMode.MODE_TODAY:
                dateFrom = DateUtils.getToday();
                dateTo = DateUtils.getTomorrowDate();
                break;
            case IDateMode.MODE_WEEK:
                dateFrom = DateUtils.getFirstDateOfCurrentWeek();
                dateTo = DateUtils.getLastDateOfCurrentWeek();
                break;
            case IDateMode.MODE_MONTH:
                dateFrom = DateUtils.getFirstDateOfCurrentMonth();
                dateTo = DateUtils.getLastDateOfCurrentMonth();
                break;
            default:
                dateFrom = new Date();
                dateTo = new Date();
        }
        return getTotal(dateFrom, dateTo);
    }

    public static List<Expense> getTodayExpenses() {
        Date today = DateUtils.getToday();
        Date tomorrow = DateUtils.getTomorrowDate();
        return getExpensesList(today, tomorrow, null, null);
    }

    public static List<Expense> getWeekExpenses() {
        Date startWeek = DateUtils.getFirstDateOfCurrentWeek();
        Date endWeek = DateUtils.getLastDateOfCurrentWeek();
        return getExpensesList(startWeek, endWeek, null, null);
    }

    public static List<Expense> getWeekExpensesByCategory(Expense expense) {
        Date startWeek = DateUtils.getFirstDateOfCurrentWeek();
        Date endWeek = DateUtils.getLastDateOfCurrentWeek();
        return getExpensesList(startWeek, endWeek, null, expense.getCategory());
    }

    public static List<Expense> getMonthExpenses() {
        Date startMonth = DateUtils.getFirstDateOfCurrentMonth();
        Date endMonth = DateUtils.getLastDateOfCurrentMonth();
        return getExpensesList(startMonth, endMonth, null, null);
    }

    public static float getCategoryTotalByDate(Date date, Category category) {
        RealmResults<Expense> totalExpense = getExpensesList(date, DateUtils.addDaysToDate(date, 1), IExpensesType.MODE_EXPENSES, category);
        return totalExpense.sum("total").floatValue();
    }

    public static float getCategoryTotalByDate(Date fromDate, Date toDate, Category category) {
        RealmResults<Expense> totalExpense = getExpensesList(fromDate, DateUtils.addDaysToDate(toDate, 1), IExpensesType.MODE_EXPENSES, category);
        return totalExpense.sum("total").floatValue();
    }

    public static RealmResults<Expense> getExpensesPerCategory(Category category) {
        return RealmManager.getInstance().getRealmInstance().where(Expense.class).equalTo("category.id", category.getId()).findAll();
    }

    public static RealmQuery<Expense> getExpensesList(Date dateFrom, Date dateTo, Category category) {
        RealmQuery<Expense> realmQuery = RealmManager.getInstance().getRealmInstance()
                .where(Expense.class);
        if (dateTo != null) {
            realmQuery.between("date", dateFrom, dateTo);
        } else {
            realmQuery.equalTo("date", dateFrom);
        }
        if (category != null) realmQuery.equalTo("category.id", category.getId());
        return realmQuery;
    }

    public static RealmResults<Expense> getExpensesListResults(Date dateFrom, Date dateTo, Category category) {
        return getExpensesList(dateFrom, dateTo, category).findAll();
    }

    public static RealmResults<Expense> getExpensesList(Date dateFrom, Date dateTo, @IExpensesType Integer type, Category category) {
        RealmQuery<Expense> realmQuery = getExpensesList(dateFrom, dateTo, category);
        if (type != null) realmQuery.equalTo("type", type);
        return realmQuery.findAll();
    }

    public static float getTotal(Date dateFrom, Date dateTo) {
        RealmResults<Expense> totalExpense = getTotalExpenses(dateFrom, dateTo);
        RealmResults<Expense> totalIncome = getTotalIncome(dateFrom, dateTo);
        return Math.abs(totalExpense.sum("total").floatValue() - totalIncome.sum("total").floatValue());
    }

    @ColorRes
    public static int getExpensesTotalIntColorResource(Date dateFrom, Date dateTo) {
        float expensesTotal = getTotalExpenses(dateFrom, dateTo).sum("total").floatValue();
        float incomeTotal = getTotalIncome(dateFrom, dateTo).sum("total").floatValue();
        float total = incomeTotal - expensesTotal;
        if (total > 0) {
            return R.color.colorAccentGreen;
        } else if (total < 0) {
            return R.color.colorAccentRed;
        } else {
            return android.R.color.black;
        }
    }

    @ColorRes
    public static int getExpensesTotalIntColorResource(@IDateMode int dateMode) {
        Date dateFrom;
        Date dateTo;
        switch (dateMode) {
            case IDateMode.MODE_TODAY:
                dateFrom = DateUtils.getToday();
                dateTo = DateUtils.getTomorrowDate();
                break;
            case IDateMode.MODE_WEEK:
                dateFrom = DateUtils.getFirstDateOfCurrentWeek();
                dateTo = DateUtils.getLastDateOfCurrentWeek();
                break;
            case IDateMode.MODE_MONTH:
                dateFrom = DateUtils.getFirstDateOfCurrentMonth();
                dateTo = DateUtils.getLastDateOfCurrentMonth();
                break;
            default:
                dateFrom = new Date();
                dateTo = new Date();
        }
        return getExpensesTotalIntColorResource(dateFrom, dateTo);
    }

    public static RealmResults<Expense> getTotalExpenses(Date dateFrom, Date dateTo) {
        return getExpensesList(dateFrom, dateTo, IExpensesType.MODE_EXPENSES, null);
    }

    public static RealmResults<Expense> getTotalIncome(Date dateFrom, Date dateTo) {
        return getExpensesList(dateFrom, dateTo, IExpensesType.MODE_INCOME, null);
    }

    public static float getExpensesCategoryPercentage(Date fromDate, Date toDate, Category category) {
        float totalCategory = getCategoryTotalByDate(fromDate, toDate, category);
        float total = getExpensesList(fromDate, DateUtils.addDaysToDate(toDate, 1), IExpensesType.MODE_EXPENSES, null).sum("total").floatValue();
        return totalCategory * 100 / total;
    }

    public static List<Expense> cloneExpensesCollection(List<Expense> expenseList) {
        List<Expense> clonedExpenses = new ArrayList<>();
        for (Expense expense : expenseList) {
            Expense cloneExpense = new Expense();
            cloneExpense.setId(expense.getId());
            Category category = new Category();
            category.setName(expense.getCategory().getName());
            cloneExpense.setCategory(category);
            cloneExpense.setDate(expense.getDate());
            cloneExpense.setDescription(expense.getDescription());
            cloneExpense.setTotal(expense.getTotal());
            cloneExpense.setType(expense.getType());
            clonedExpenses.add(cloneExpense);
        }
        return clonedExpenses;
    }
}
