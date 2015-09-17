package com.pedrocarrillo.expensetracker.interfaces;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;

import java.util.List;

/**
 * Created by Pedro on 15/9/2015.
 */
public interface IFragmentListener {

    void replaceFragment(Fragment fragment, boolean addToBackStack);
    void replaceFragment(int containerId, Fragment fragment, boolean addToBackStack);
    void setResultWithData(int status, Intent intent);
    void closeActivity();

}
