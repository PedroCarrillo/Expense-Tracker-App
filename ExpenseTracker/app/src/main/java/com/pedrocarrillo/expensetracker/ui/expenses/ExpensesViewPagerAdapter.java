package com.pedrocarrillo.expensetracker.ui.expenses;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pedro on 9/29/2015.
 */
public class ExpensesViewPagerAdapter extends FragmentStatePagerAdapter {

    private final List<ExpensesFragment> mFragmentList = new ArrayList<>();
    private List<String> pageTitles = new ArrayList<>();

    public ExpensesViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public ExpensesFragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFrag(ExpensesFragment fragment, String title) {
        mFragmentList.add(fragment);
        pageTitles.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitles.get(position);
    }

    public List<ExpensesFragment> getFragmentList() {
        return mFragmentList;
    }

}