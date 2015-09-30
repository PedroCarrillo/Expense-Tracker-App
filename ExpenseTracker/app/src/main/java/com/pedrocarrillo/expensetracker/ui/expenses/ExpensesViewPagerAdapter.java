package com.pedrocarrillo.expensetracker.ui.expenses;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pedro on 9/29/2015.
 */
public class ExpensesViewPagerAdapter extends FragmentStatePagerAdapter {

    private final List<ExpensesFragment> mFragmentList = new ArrayList<>();

    public ExpensesViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public ExpensesFragment getItem(int position) {
        ExpensesFragment expensesFragment = mFragmentList.get(position);
        expensesFragment.reloadData();
        return expensesFragment;
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFrag(ExpensesFragment fragment) {
        mFragmentList.add(fragment);
    }

}