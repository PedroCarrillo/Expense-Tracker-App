package com.pedrocarrillo.expensetracker.ui.expenses;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.adapters.ExpensesAdapter;
import com.pedrocarrillo.expensetracker.custom.DefaultRecyclerViewItemDecorator;
import com.pedrocarrillo.expensetracker.entities.Expense;
import com.pedrocarrillo.expensetracker.interfaces.IDateMode;
import com.pedrocarrillo.expensetracker.interfaces.IUserActionsMode;
import com.pedrocarrillo.expensetracker.ui.MainActivity;
import com.pedrocarrillo.expensetracker.ui.MainFragment;
import com.pedrocarrillo.expensetracker.utils.DateUtils;
import com.pedrocarrillo.expensetracker.utils.DialogManager;
import com.pedrocarrillo.expensetracker.utils.RealmManager;
import com.pedrocarrillo.expensetracker.widget.ExpensesWidgetProvider;
import com.pedrocarrillo.expensetracker.widget.ExpensesWidgetService;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by pcarrillo on 17/09/2015.
 */
public class ExpensesFragment extends MainFragment implements TabLayout.OnTabSelectedListener, ExpensesAdapter.ExpenseAdapterOnClickHandler {

    public static final int RQ_NEW_EXPENSE = 1001;

    private List<Expense> mExpenseList;
    private ExpensesAdapter mExpensesAdapter;
    private RecyclerView rvExpenses;
    private @IDateMode int mCurrentDateMode;

    public static ExpensesFragment newInstance() {
        return new ExpensesFragment();
    }

    public ExpensesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_expenses, container, false);
        rvExpenses = (RecyclerView) rootView.findViewById(R.id.rv_expenses);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mExpensesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        List<String> tabList = Arrays.asList(getString(R.string.today), getString(R.string.week), getString(R.string.month));
        mMainActivityListener.setTitle(getString(R.string.expenses));
        mMainActivityListener.setMode(MainActivity.NAVIGATION_MODE_TABS);
        mMainActivityListener.setTabs(tabList, this);
        mMainActivityListener.setFAB(R.drawable.ic_add_white_48dp, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddNewExpense();
            }
        });
        rvExpenses.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvExpenses.addItemDecoration(new DefaultRecyclerViewItemDecorator(getResources().getDimension(R.dimen.dimen_5dp)));
        rvExpenses.setAdapter(mExpensesAdapter);
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final Expense expense= (Expense) viewHolder.itemView.getTag();
                DialogManager.getInstance().createCustomAcceptDialog(getActivity(), getString(R.string.delete), getString(R.string.confirm_delete_expense), getString(R.string.confirm), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            Date expenseDate = expense.getDate();
                            RealmManager.getInstance().delete(expense);
                            // update widget if the expense is created today
                            if (DateUtils.isToday(expenseDate)) {
                                Intent i = new Intent(getActivity(), ExpensesWidgetProvider.class);
                                i.setAction(ExpensesWidgetService.UPDATE_WIDGET);
                                getActivity().sendBroadcast(i);
                            }
                        }
                        reloadData();
                    }
                });
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (viewHolder.itemView.getTag() == null) return 0;
                return super.getSwipeDirs(recyclerView, viewHolder);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rvExpenses);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (tab.getTag()!=null) {
            if (tab.getTag().toString().equalsIgnoreCase(getString(R.string.today))) {
                mCurrentDateMode= IDateMode.MODE_TODAY;
            } else if (tab.getTag().toString().equalsIgnoreCase(getString(R.string.week))) {
                mCurrentDateMode = IDateMode.MODE_WEEK;
            } else if (tab.getTag().toString().equalsIgnoreCase(getString(R.string.month))) {
                mCurrentDateMode = IDateMode.MODE_MONTH;
            }
        }
        reloadData();
    }

    private void onAddNewExpense() {
        NewExpenseFragment newExpenseFragment = NewExpenseFragment.newInstance(IUserActionsMode.MODE_CREATE, null);
        newExpenseFragment.setTargetFragment(this, RQ_NEW_EXPENSE);
        newExpenseFragment.show(getChildFragmentManager(), "NEW_EXPENSE");
    }

    private void reloadData() {
        switch (mCurrentDateMode) {
            case IDateMode.MODE_TODAY:
                mExpenseList = Expense.getTodayExpenses();
                break;
            case IDateMode.MODE_WEEK:
                mExpenseList = Expense.getWeekExpenses();
                break;
            case IDateMode.MODE_MONTH:
                mExpenseList = Expense.getMonthExpenses();
                break;
        }
        if (mExpensesAdapter == null) {
            mExpensesAdapter = new ExpensesAdapter(getActivity(), this, mExpenseList, mCurrentDateMode);
        } else {
            mExpensesAdapter.updateExpenses(mExpenseList, mCurrentDateMode);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onClick(ExpensesAdapter.ViewHolder vh) {
        Expense expenseSelected = (Expense) vh.itemView.getTag();
        Intent expenseDetail = new Intent(getActivity(), ExpenseDetailActivity.class);
        expenseDetail.putExtra(ExpenseDetailFragment.EXPENSE_ID_KEY, expenseSelected.getId());
        startActivity(expenseDetail);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RQ_NEW_EXPENSE && resultCode == Activity.RESULT_OK) {
            reloadData();
        }
    }
}
