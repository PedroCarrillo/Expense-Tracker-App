package com.pedrocarrillo.expensetracker.interfaces;

import com.pedrocarrillo.expensetracker.ui.MainActivity;

import java.util.List;

/**
 * Created by pcarrillo on 17/09/2015.
 */
public interface IMainActivityListener {

    void setMode(@MainActivity.NavigationMode int mode);
    void setTabs(List<String> tabList);

}
