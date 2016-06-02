package com.pedrocarrillo.expensetracker.ui.expenses;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.interfaces.IConstants;
import com.pedrocarrillo.expensetracker.interfaces.IDateMode;
import com.pedrocarrillo.expensetracker.interfaces.IExpensesMode;
import com.pedrocarrillo.expensetracker.interfaces.IUserActionsMode;
import com.pedrocarrillo.expensetracker.ui.MainActivity;
import com.pedrocarrillo.expensetracker.ui.MainFragment;
import com.pedrocarrillo.expensetracker.utils.DialogManager;
import com.pedrocarrillo.expensetracker.utils.ExpensesManager;


/**
 * @author  pcarrillo on 16/10/2015.
 */

public class ExpensesContainerFragment extends MainFragment implements ExpensesFragment.IExpenseContainerListener {

    public static final int RQ_NEW_EXPENSE = 1001;
    private ViewPager vpExpensesContainer;
    private ExpensesViewPagerAdapter expensesViewPagerAdapter;
    public static final String EXPENSES_MODE_KEY = "_expenses_mode_key";
    public IExpensesMode expensesMode;
    public MenuItem budgetMenuItem;

    // Action mode for expenses.
    private android.view.ActionMode mActionMode;

    public static ExpensesContainerFragment newInstance(IExpensesMode expensesMode) {
        ExpensesContainerFragment fragment = new ExpensesContainerFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXPENSES_MODE_KEY, expensesMode);
        fragment.setArguments(args);
        return fragment;
    }

    public ExpensesContainerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_expenses, menu);
        budgetMenuItem = menu.findItem(R.id.action_budget);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_budget) {
            onBudgetClicked();
        }
        return super.onOptionsItemSelected(item);
    }

    private void onBudgetClicked() {
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (expensesMode != IExpensesMode.BUDGET) {
            budgetMenuItem.setVisible(false);
        }
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
        if (getArguments() != null && getArguments().containsKey(EXPENSES_MODE_KEY)) {
            expensesMode = (IExpensesMode) getArguments().getSerializable(EXPENSES_MODE_KEY);
        }
        mMainActivityListener.setTitle(getString(R.string.expenses));
        mMainActivityListener.setMode(MainActivity.NAVIGATION_MODE_TABS);
        mMainActivityListener.setFAB(R.drawable.ic_add_white_48dp, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddNewExpense();
            }
        });

        expensesViewPagerAdapter = new ExpensesViewPagerAdapter(getChildFragmentManager());
        expensesViewPagerAdapter.addFrag(ExpensesFragment.newInstance(IDateMode.MODE_TODAY, expensesMode), getString(R.string.today));
        expensesViewPagerAdapter.addFrag(ExpensesFragment.newInstance(IDateMode.MODE_WEEK, expensesMode), getString(R.string.week));
        expensesViewPagerAdapter.addFrag(ExpensesFragment.newInstance(IDateMode.MODE_MONTH, expensesMode), getString(R.string.month));
        vpExpensesContainer.setAdapter(expensesViewPagerAdapter);
        mMainActivityListener.setPager(vpExpensesContainer, new TabLayout.ViewPagerOnTabSelectedListener(vpExpensesContainer) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                endActionMode();
            }
        });

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
        updateExpenseSummary();
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(IConstants.BROADCAST_UPDATE_EXPENSES));
    }

    private android.view.ActionMode.Callback mActionModeCallback = new android.view.ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.expenses_context_menu, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete:
                    eraseExpenses();
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(android.view.ActionMode mode) {
            endActionMode();
            mActionMode = null;
        }
    };

    @Override
    public boolean isActionMode() {
        return mActionMode != null;
    }

    @Override
    public void startActionMode() {
        mActionMode = mMainActivityListener.setActionMode(mActionModeCallback);
    }

    @Override
    public void endActionMode() {
        if (mActionMode != null) {
            mActionMode.finish();
            ExpensesManager.getInstance().resetSelectedItems();
            updateExpensesFragments();
        }
    }

    @Override
    public void setActionModeTitle(String title) {
        mActionMode.setTitle(title);
        mActionMode.invalidate();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IConstants.IS_ACTION_MODE_ACTIVATED, mActionMode != null);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            boolean isActionMode = savedInstanceState.getBoolean(IConstants.IS_ACTION_MODE_ACTIVATED);
            if(isActionMode) {
                mActionMode = mMainActivityListener.setActionMode(mActionModeCallback);
                setActionModeTitle(String.valueOf(ExpensesManager.getInstance().getSelectedExpensesItems().size()));
            }
        }
    }

    private void eraseExpenses() {
        DialogManager.getInstance().createCustomAcceptDialog(getActivity(), getString(R.string.delete), getString(R.string.confirm_delete_items), getString(R.string.confirm), getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    ExpensesManager.getInstance().eraseSelectedExpenses();
                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(IConstants.BROADCAST_UPDATE_EXPENSES));
                }
                mActionMode.finish();
                mActionMode = null;
            }
        });
    }

    private void updateExpenseSummary() {
        if (vpExpensesContainer != null && expensesViewPagerAdapter != null) {
            ExpensesFragment expensesFragment = expensesViewPagerAdapter.getItem(vpExpensesContainer.getCurrentItem());
            if (mMainActivityListener != null)
                mMainActivityListener.setExpensesSummary(expensesFragment.getCurrentDateMode());
        }
    }

}
