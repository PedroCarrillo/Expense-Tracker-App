package com.pedrocarrillo.expensetracker.ui;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.pedrocarrillo.expensetracker.interfaces.IFragmentListener;

/**
 * Created by Pedro on 15/9/2015.
 */
public class BaseActivity extends AppCompatActivity implements IFragmentListener {

    @Override
    public void replaceFragment(int containerId, Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        String tag = fragment.getClass().getSimpleName();
        transaction.replace(containerId, fragment, tag);
        if(addToBackStack) transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void setResultWithData(int status, Intent intent) {
        setResult(status, intent);
        closeActivity();
    }

    @Override
    public void closeActivity() {
        finish();
    }
}
