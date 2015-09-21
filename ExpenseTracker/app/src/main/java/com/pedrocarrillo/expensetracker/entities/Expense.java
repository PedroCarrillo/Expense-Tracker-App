package com.pedrocarrillo.expensetracker.entities;

import com.pedrocarrillo.expensetracker.interfaces.IExpensesType;
import com.pedrocarrillo.expensetracker.utils.RealmManager;

import java.util.Date;
import java.util.List;

import io.realm.RealmObject;
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

}
