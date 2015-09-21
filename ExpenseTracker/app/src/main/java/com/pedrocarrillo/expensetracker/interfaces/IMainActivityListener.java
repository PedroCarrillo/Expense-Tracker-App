package com.pedrocarrillo.expensetracker.interfaces;

import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.design.widget.TabLayout;
import android.view.View;

import com.pedrocarrillo.expensetracker.ui.MainActivity;

import java.util.List;

/**
 * Created by pcarrillo on 17/09/2015.
 */
public interface IMainActivityListener {

    void setMode(@MainActivity.NavigationMode int mode);
    void setTabs(List<String> tabList, TabLayout.OnTabSelectedListener onTabSelectedListener);
    void setFAB(@DrawableRes int drawableId, View.OnClickListener onClickListener);
    void setTitle(String title);

}
