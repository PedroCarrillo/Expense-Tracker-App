package com.pedrocarrillo.expensetracker.ui.expenses;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.pedrocarrillo.expensetracker.entities.Expense;
import com.pedrocarrillo.expensetracker.interfaces.IConstants;
import com.pedrocarrillo.expensetracker.interfaces.IDateMode;
import com.pedrocarrillo.expensetracker.ui.MainFragment;
import com.pedrocarrillo.expensetracker.utils.DialogManager;
import com.pedrocarrillo.expensetracker.utils.ExpensesManager;

/**
 * Created by pcarrillo on 17/09/2015.
 */
public class ExpensesFragment extends MainFragment implements BaseViewHolder.RecyclerClickListener {

    public static final int RQ_NEW_EXPENSE = 1001;

//    private List<Expense> mExpenseList;
    private MainExpenseAdapter mMainExpenseAdapter;
    private RecyclerView rvExpenses;
    private @IDateMode int mCurrentDateMode;
    private IExpenseContainerListener expenseContainerListener;

    public static ExpensesFragment newInstance(@IDateMode int mCurrentDateMode, IExpenseContainerListener expenseContainerListener) {
        ExpensesFragment expensesFragment = new ExpensesFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(IDateMode.DATE_MODE_TAG, mCurrentDateMode);
        expensesFragment.setArguments(bundle);
        expensesFragment.setExpenseContainerListener(expenseContainerListener);
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
        if (getArguments() != null) {
            int mode = getArguments().getInt(IDateMode.DATE_MODE_TAG);
            mCurrentDateMode = IDateMode.MODE_TODAY == mode ? IDateMode.MODE_TODAY : (IDateMode.MODE_WEEK == mode ? IDateMode.MODE_WEEK : IDateMode.MODE_MONTH);
            updateData();
            rvExpenses.setAdapter(mMainExpenseAdapter);
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IConstants.IS_ACTION_MODE_ACTIVATED, mActionMode != null);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMainExpenseAdapter != null) mMainExpenseAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            boolean isActionMode = savedInstanceState.getBoolean(IConstants.IS_ACTION_MODE_ACTIVATED);
            if(isActionMode) mActionMode = mMainActivityListener.setActionMode(mActionModeCallback);
        }
        rvExpenses.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvExpenses.addItemDecoration(new DefaultRecyclerViewItemDecorator(getResources().getDimension(R.dimen.dimen_5dp)));
    }

    public void updateData() {
        ExpensesManager.getInstance().setExpensesListByDateMode(mCurrentDateMode);
        if (mMainExpenseAdapter == null) {
            mMainExpenseAdapter = new MainExpenseAdapter(getActivity(), this, ExpensesManager.getInstance().getExpensesList(), mCurrentDateMode);
        } else {
            mMainExpenseAdapter.updateExpenses(ExpensesManager.getInstance().getExpensesList(), mCurrentDateMode);
        }
    }

    // Action mode for expenses.
    private android.view.ActionMode mActionMode;

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
            mMainExpenseAdapter.clearSelection();
            mActionMode = null;
        }
    };

    private void eraseExpenses() {
        DialogManager.getInstance().createCustomAcceptDialog(getActivity(), getString(R.string.delete), getString(R.string.confirm_delete_items), getString(R.string.confirm), getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    ExpensesManager.getInstance().eraseSelectedExpenses();
                    expenseContainerListener.updateExpensesFragments();
                }
                updateData();
                mActionMode.finish();
                mActionMode = null;
            }
        });
    }

    @Override
    public void onClick(RecyclerView.ViewHolder vh, int position) {
        if (mActionMode == null) {
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
        if (mActionMode == null) {
            mActionMode = mMainActivityListener.setActionMode(mActionModeCallback);
        }
        toggleSelection(position);
    }

    public void toggleSelection(int position) {
        mMainExpenseAdapter.toggleSelection(position);
        int count = mMainExpenseAdapter.getSelectedItemCount();
        if (count == 0) {
            mActionMode.finish();
        } else {
            mActionMode.setTitle(String.valueOf(count));
            mActionMode.invalidate();
        }
    }

    public void cancelActionMode() {
        if (mActionMode != null) {
            mActionMode.finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RQ_NEW_EXPENSE && resultCode == Activity.RESULT_OK) {
            updateData();
        }
    }

    public void setExpenseContainerListener(IExpenseContainerListener expenseContainerListener) {
        this.expenseContainerListener = expenseContainerListener;
    }

    public interface IExpenseContainerListener {
        void updateExpensesFragments();
    }

}
