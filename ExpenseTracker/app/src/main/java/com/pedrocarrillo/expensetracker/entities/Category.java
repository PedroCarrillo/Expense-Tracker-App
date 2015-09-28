package com.pedrocarrillo.expensetracker.entities;

import com.pedrocarrillo.expensetracker.interfaces.IExpensesType;
import com.pedrocarrillo.expensetracker.utils.RealmManager;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

/**
 * Created by pcarrillo on 17/09/2015.
 */
public class Category extends RealmObject {

    @PrimaryKey
    private String id;

    private String name;
    private int type;
    private RealmList<Expense> expenses;

    public Category() {
    }

    public Category(String name, @IExpensesType int type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public RealmList<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(RealmList<Expense> expenses) {
        this.expenses = expenses;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static List<Category> getCategoriesIncome() {
        return getCategoriesForType(IExpensesType.MODE_INCOME);
    }

    public static List<Category> getCategoriesExpense() {
        return getCategoriesForType(IExpensesType.MODE_EXPENSES);
    }

    public static List<Category> getCategoriesForType(@IExpensesType int type){
        RealmResults<Category> categories = RealmManager.getInstance().getRealmInstance().where(Category.class)
                .equalTo("type", type)
                .findAll();
        return categories;
    }

}
