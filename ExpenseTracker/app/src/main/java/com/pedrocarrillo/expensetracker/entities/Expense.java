package com.pedrocarrillo.expensetracker.entities;

import android.util.Log;

import com.pedrocarrillo.expensetracker.interfaces.IDateMode;
import com.pedrocarrillo.expensetracker.interfaces.IExpensesType;
import com.pedrocarrillo.expensetracker.utils.DateUtils;
import com.pedrocarrillo.expensetracker.utils.RealmManager;
import com.pedrocarrillo.expensetracker.utils.Util;

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
        RealmResults<Expense> totalExpense = getExpensesList(dateFrom, dateTo, IExpensesType.MODE_EXPENSES, null);
        RealmResults<Expense> totalIncome = getExpensesList(dateFrom, dateTo, IExpensesType.MODE_INCOME, null);
        float total = totalExpense.sum("total").floatValue() - totalIncome.sum("total").floatValue();
        return total;
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

    public static RealmResults<Expense> getExpensesList(Date dateFrom, Date dateTo, @IExpensesType Integer type, Category category) {
        RealmQuery<Expense> realmQuery = RealmManager.getInstance().getRealmInstance()
                .where(Expense.class);
        if (dateTo != null) {
            realmQuery.between("date", dateFrom, dateTo);
        } else {
            realmQuery.equalTo("date", dateFrom);
        }
        if (category != null) realmQuery.equalTo("category.id", category.getId());
        if (type != null) realmQuery.equalTo("type", type);
        return realmQuery.findAll();
    }

    public static float getExpensesCategoryPercentage(Date fromDate, Date toDate, Category category) {
        float totalCategory = getCategoryTotalByDate(fromDate, toDate, category);
        float total = getExpensesList(fromDate, toDate, IExpensesType.MODE_EXPENSES, null).sum("total").floatValue();
        return totalCategory * 100 / total;
    }

}
