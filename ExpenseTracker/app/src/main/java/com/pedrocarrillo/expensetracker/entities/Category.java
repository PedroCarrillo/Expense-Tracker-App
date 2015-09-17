package com.pedrocarrillo.expensetracker.entities;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by pcarrillo on 17/09/2015.
 */
public class Category extends RealmObject {

    private String name;
    private int type;

    public Category() {
    }

    public Category(String name, int type) {
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
}
