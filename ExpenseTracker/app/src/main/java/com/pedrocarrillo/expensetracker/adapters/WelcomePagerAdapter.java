package com.pedrocarrillo.expensetracker.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.ui.Login.WelcomePage;

/**
 * Created by Pedro on 15/9/2015.
 */
public class WelcomePagerAdapter extends FragmentStatePagerAdapter {

    public static final int NUM_PAGES = 3;

    public WelcomePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        WelcomePage tp = null;
        switch (position) {
            case 0:
                tp = WelcomePage.newInstance(R.layout.first_welcome_page);
                break;
            case 1:
                tp = WelcomePage.newInstance(R.layout.second_welcome_page);
                break;
            case 2:
                tp = WelcomePage.newInstance(R.layout.third_welcome_page);
                break;
        }
        return tp;
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

}