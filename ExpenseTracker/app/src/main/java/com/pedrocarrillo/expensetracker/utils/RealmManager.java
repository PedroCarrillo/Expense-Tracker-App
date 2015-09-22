package com.pedrocarrillo.expensetracker.utils;

import com.pedrocarrillo.expensetracker.ExpenseTrackerApp;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by Pedro on 9/20/2015.
 */
public class RealmManager {

    private Realm realm;

    private static RealmManager ourInstance = new RealmManager();

    public static RealmManager getInstance() {
        return ourInstance;
    }

    public RealmManager(){
        realm = Realm.getInstance(ExpenseTrackerApp.getContext());
    }

    public Realm getRealmInstance() {
        return realm;
    }

    public <E extends RealmObject> void save(E object) {
        realm.beginTransaction();
        realm.copyToRealm(object);
        realm.commitTransaction();
    }

    public <E extends RealmObject> void save(Iterable<E> object) {
        realm.beginTransaction();
        realm.copyToRealm(object);
        realm.commitTransaction();
    }

    public <E extends RealmObject> void delete(E object){
        realm.beginTransaction();
        object.removeFromRealm();
        realm.commitTransaction();
    }

}
