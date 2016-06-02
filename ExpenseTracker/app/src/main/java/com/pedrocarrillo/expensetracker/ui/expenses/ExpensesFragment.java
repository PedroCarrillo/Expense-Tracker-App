package com.pedrocarrillo.expensetracker.ui.expenses;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.adapters.MainExpenseAdapter;
import com.pedrocarrillo.expensetracker.custom.BaseViewHolder;
import com.pedrocarrillo.expensetracker.custom.DefaultRecyclerViewItemDecorator;
import com.pedrocarrillo.expensetracker.custom.SparseBooleanArrayParcelable;
import com.pedrocarrillo.expensetracker.entities.Expense;
import com.pedrocarrillo.expensetracker.interfaces.IConstants;
import com.pedrocarrillo.expensetracker.interfaces.IDateMode;
import com.pedrocarrillo.expensetracker.interfaces.IExpensesMode;
import com.pedrocarrillo.expensetracker.ui.MainFragment;
import com.pedrocarrillo.expensetracker.utils.DialogManager;
import com.pedrocarrillo.expensetracker.utils.ExpensesManager;

import static com.pedrocarrillo.expensetracker.ui.expenses.ExpensesContainerFragment.EXPENSES_MODE_KEY;

/**
 * Created by pcarrillo on 17/09/2015.
 */
public class ExpensesFragment extends MainFragment implements BaseViewHolder.RecyclerClickListener {

    public static final int RQ_NEW_EXPENSE = 1001;

    private MainExpenseAdapter mMainExpenseAdapter;
    private RecyclerView rvExpenses;
    private @IDateMode int mCurrentDateMode;
    private IExpenseContainerListener expenseContainerListener;
    private ExpenseChangeReceiver expenseChangeReceiver = new ExpenseChangeReceiver();
    public IExpensesMode expensesMode;

    public static ExpensesFragment newInstance(@IDateMode int dateMode, IExpensesMode expensesMode) {
        ExpensesFragment expensesFragment = new ExpensesFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(IDateMode.DATE_MODE_TAG, dateMode);
        bundle.putSerializable(EXPENSES_MODE_KEY, expensesMode);
        expensesFragment.setArguments(bundle);
        return expensesFragment;
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
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(IConstants.TAG_SELECTED_ITEMS, new SparseBooleanArrayParcelable(mMainExpenseAdapter.getSelectedBooleanArray()));
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(expenseChangeReceiver, new IntentFilter(IConstants.BROADCAST_UPDATE_EXPENSES));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(expenseChangeReceiver);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() != null) {
            expenseContainerListener = (IExpenseContainerListener) getParentFragment();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getParentFragment() != null) {
            expenseContainerListener = null;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            @IDateMode int mode = getArguments().getInt(IDateMode.DATE_MODE_TAG);
            expensesMode = (IExpensesMode) getArguments().getSerializable(EXPENSES_MODE_KEY);
            mCurrentDateMode = mode;
            ExpensesManager.getInstance().setExpensesListByDateMode(mCurrentDateMode);
            mMainExpenseAdapter = new MainExpenseAdapter(getActivity(), this, mCurrentDateMode);
            rvExpenses.setAdapter(mMainExpenseAdapter);
        }
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(IConstants.TAG_SELECTED_ITEMS)) {
                mMainExpenseAdapter.setSelectedItems((SparseBooleanArray)savedInstanceState.getParcelable(IConstants.TAG_SELECTED_ITEMS));
                mMainExpenseAdapter.notifyDataSetChanged();
            }
        }
        rvExpenses.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvExpenses.addItemDecoration(new DefaultRecyclerViewItemDecorator(getResources().getDimension(R.dimen.dimen_5dp)));
    }

    public void updateData() {
        ExpensesManager.getInstance().setExpensesListByDateMode(mCurrentDateMode);
        ExpensesManager.getInstance().resetSelectedItems();
       if (mMainExpenseAdapter != null) mMainExpenseAdapter.updateExpenses(mCurrentDateMode);
    }

    @Override
    public void onClick(RecyclerView.ViewHolder vh, int position) {
        if (!expenseContainerListener.isActionMode()) {
            Expense expenseSelected = (Expense) vh.itemView.getTag();
            Intent expenseDetail = new Intent(getActivity(), ExpenseDetailActivity.class);
            expenseDetail.putExtra(ExpenseDetailFragment.EXPENSE_ID_KEY, expenseSelected.getId());
            startActivity(expenseDetail);
        } else {
            toggleSelection(position);
        }
    }

    @Override
    public void onLongClick(RecyclerView.ViewHolder vh, int position) {
        if (!expenseContainerListener.isActionMode()) {
            expenseContainerListener.startActionMode();
        }
        toggleSelection(position);
    }

    public void toggleSelection(int position) {
        mMainExpenseAdapter.toggleSelection(position);
        int count = mMainExpenseAdapter.getSelectedItemCount();
        if (count == 0) {
            expenseContainerListener.endActionMode();
        } else {
            expenseContainerListener.setActionModeTitle(String.valueOf(count));
        }
    }

    public void cancelActionMode() {
        if (expenseContainerListener.isActionMode()) {
            expenseContainerListener.endActionMode();
            mMainExpenseAdapter.clearSelection();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RQ_NEW_EXPENSE && resultCode == Activity.RESULT_OK) {
            updateData();
        }
    }

    public class ExpenseChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            updateData();
        }
    }

    public @IDateMode int getCurrentDateMode() {
        return mCurrentDateMode;
    }

    public interface IExpenseContainerListener {
        void updateExpensesFragments();
        boolean isActionMode();
        void startActionMode();
        void endActionMode();
        void setActionModeTitle(String title);
    }

}
