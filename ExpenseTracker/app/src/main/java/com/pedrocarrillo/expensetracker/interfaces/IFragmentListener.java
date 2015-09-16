package com.pedrocarrillo.expensetracker.interfaces;

import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by Pedro on 15/9/2015.
 */
public interface IFragmentListener {

    void replaceFragment(int containerId, Fragment fragment, boolean addToBackStack);
    void setResultWithData(int status, Intent intent);
    void closeActivity();

}
