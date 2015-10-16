package com.pedrocarrillo.expensetracker.ui.expenses;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.interfaces.IDateMode;
import com.pedrocarrillo.expensetracker.interfaces.IUserActionsMode;
import com.pedrocarrillo.expensetracker.ui.MainActivity;
import com.pedrocarrillo.expensetracker.ui.MainFragment;

import java.util.Arrays;
import java.util.List;

public class ExpensesContainerFragment extends MainFragment implements TabLayout.OnTabSelectedListener, ExpensesFragment.IExpenseContainerListener {

    public static final int RQ_NEW_EXPENSE = 1001;
    private ViewPager vpExpensesContainer;
    private ExpensesViewPagerAdapter expensesViewPagerAdapter;

    public static ExpensesContainerFragment newInstance() {
        ExpensesContainerFragment fragment = new ExpensesContainerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ExpensesContainerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_expenses_container, container, false);
        vpExpensesContainer = (ViewPager)rootView.findViewById(R.id.vp_expenses);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        List<String> tabList = Arrays.asList(getString(R.string.today), getString(R.string.week), getString(R.string.month));
        mMainActivityListener.setTitle(getString(R.string.expenses));
        mMainActivityListener.setMode(MainActivity.NAVIGATION_MODE_TABS);
        mMainActivityListener.setFAB(R.drawable.ic_add_white_48dp, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddNewExpense();
            }
        });

        expensesViewPagerAdapter = new ExpensesViewPagerAdapter(getChildFragmentManager());
        expensesViewPagerAdapter.addFrag(ExpensesFragment.newInstance(IDateMode.MODE_TODAY, this), getString(R.string.today));
        expensesViewPagerAdapter.addFrag(ExpensesFragment.newInstance(IDateMode.MODE_WEEK, this),  getString(R.string.week));
        expensesViewPagerAdapter.addFrag(ExpensesFragment.newInstance(IDateMode.MODE_MONTH, this), getString(R.string.month));
        vpExpensesContainer.setAdapter(expensesViewPagerAdapter);
        mMainActivityListener.setPager(vpExpensesContainer);
        mMainActivityListener.setTabs(tabList, this);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        vpExpensesContainer.setCurrentItem(tab.getPosition());
        for (int i=0; i < expensesViewPagerAdapter.getCount(); i++) {
            ExpensesFragment expensesFragment = expensesViewPagerAdapter.getFragmentList().get(i);
            expensesFragment.cancelActionMode();
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        for (int i=0; i < expensesViewPagerAdapter.getCount(); i++) {
            ExpensesFragment expensesFragment = expensesViewPagerAdapter.getFragmentList().get(i);
            expensesFragment.cancelActionMode();
        }
    }

    private void onAddNewExpense() {
        NewExpenseFragment newExpenseFragment = NewExpenseFragment.newInstance(IUserActionsMode.MODE_CREATE, null);
        newExpenseFragment.setTargetFragment(this, RQ_NEW_EXPENSE);
        newExpenseFragment.show(getChildFragmentManager(), "NEW_EXPENSE");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RQ_NEW_EXPENSE && resultCode == Activity.RESULT_OK) {
            updateExpensesFragments();
        }
    }

    @Override
    public void updateExpensesFragments(){
        for (ExpensesFragment expensesFragment : expensesViewPagerAdapter.getFragmentList()) {
            expensesFragment.updateData();
        }
    }

}
