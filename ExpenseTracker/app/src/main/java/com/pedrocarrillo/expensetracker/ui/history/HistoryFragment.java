package com.pedrocarrillo.expensetracker.ui.history;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.adapters.ExpensesAdapter;
import com.pedrocarrillo.expensetracker.adapters.ExpensesHistoryAdapter;
import com.pedrocarrillo.expensetracker.adapters.RemindersAdapter;
import com.pedrocarrillo.expensetracker.custom.BaseViewHolder;
import com.pedrocarrillo.expensetracker.custom.DefaultRecyclerViewItemDecorator;
import com.pedrocarrillo.expensetracker.custom.WrapContentManagerRecyclerView;
import com.pedrocarrillo.expensetracker.entities.Category;
import com.pedrocarrillo.expensetracker.entities.Expense;
import com.pedrocarrillo.expensetracker.interfaces.IDateMode;
import com.pedrocarrillo.expensetracker.interfaces.IExpensesType;
import com.pedrocarrillo.expensetracker.interfaces.IUserActionsMode;
import com.pedrocarrillo.expensetracker.ui.MainActivity;
import com.pedrocarrillo.expensetracker.ui.MainFragment;
import com.pedrocarrillo.expensetracker.ui.expenses.ExpenseDetailActivity;
import com.pedrocarrillo.expensetracker.ui.expenses.ExpenseDetailFragment;
import com.pedrocarrillo.expensetracker.ui.reminders.NewReminderActivity;
import com.pedrocarrillo.expensetracker.utils.DateUtils;
import com.pedrocarrillo.expensetracker.utils.DialogManager;
import com.pedrocarrillo.expensetracker.utils.RealmManager;
import com.pedrocarrillo.expensetracker.utils.Util;
import com.pedrocarrillo.expensetracker.widget.ExpensesWidgetProvider;
import com.pedrocarrillo.expensetracker.widget.ExpensesWidgetService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by pcarrillo on 07/10/2015.
 */
public class HistoryFragment  extends MainFragment implements View.OnClickListener, BaseViewHolder.RecyclerClickListener {

    private Button btnDateFrom;
    private Button btnDateTo;
    private TextView tvTotal;
    private RecyclerView rvHistory;

    private Date mDateFrom;
    private Date mDateTo;
    private List<Expense> mExpensesList;
    private ExpensesHistoryAdapter mExpensesAdapter;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    public HistoryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        // TODO REFACTOR THIS
        btnDateFrom = (Button)rootView.findViewById(R.id.btn_date_from);
        btnDateTo = (Button)rootView.findViewById(R.id.btn_date_to);
        tvTotal = (TextView)rootView.findViewById(R.id.tv_total);

        rvHistory = (RecyclerView)rootView.findViewById(R.id.rv_history);
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMainActivityListener.setMode(MainActivity.NAVIGATION_MODE_STANDARD);
        mMainActivityListener.setTitle(getString(R.string.history));

        btnDateFrom.setOnClickListener(this);
        btnDateTo.setOnClickListener(this);
        mDateFrom = DateUtils.getFirstDateOfCurrentWeek();
        mDateTo = DateUtils.getLastDateOfCurrentWeek();
        updateDate(btnDateFrom, mDateFrom);
        updateDate(btnDateTo, mDateTo);

        rvHistory.setLayoutManager(new WrapContentManagerRecyclerView(getActivity()));
        rvHistory.setHasFixedSize(true);
        rvHistory.addItemDecoration(new DefaultRecyclerViewItemDecorator(getResources().getDimension(R.dimen.dimen_10dp)));
        rvHistory.setNestedScrollingEnabled(false);
        updateData();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_date_from || v.getId() == R.id.btn_date_to) {
            showDateDialog(v.getId());
        }
    }

    private void showDateDialog(final int id) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(id == R.id.btn_date_from ? mDateFrom : mDateTo);
        DialogManager.getInstance()
                .showDatePicker(
                        getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                calendar.set(year, month, day);
                                DateUtils.setDateStartOfDay(calendar);
                                if (id == R.id.btn_date_from) {
                                    mDateFrom = calendar.getTime();
                                    updateDate(btnDateFrom, mDateFrom);
                                } else {
                                    mDateTo = calendar.getTime();
                                    updateDate(btnDateTo, mDateTo);
                                }
                                updateData();
                            }
                        },
                        calendar,
                        (R.id.btn_date_from == id) ? null : mDateFrom,
                        (R.id.btn_date_from == id) ? mDateTo : null);
    }

    private void updateData() {
        float total = Expense.getCategoryTotalByDate(mDateFrom, mDateTo, null);
        mExpensesList = Expense.getExpensesList(mDateFrom, mDateTo, IExpensesType.MODE_EXPENSES, null);
        if ( mExpensesAdapter == null ) {
            mExpensesAdapter = new ExpensesHistoryAdapter(getActivity(), this, mExpensesList);
            rvHistory.setAdapter(mExpensesAdapter);
        } else {
            mExpensesAdapter.updateExpenses(mExpensesList);
        }
        tvTotal.setText(Util.getFormattedCurrency(total));
    }

    private void updateDate(Button btn, Date date) {
        btn.setText(Util.formatDateToString(date, "MM/dd/yyyy"));
    }

    // Action mode for categories.
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
            mExpensesAdapter.clearSelection();
            mActionMode = null;
        }
    };

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
        mExpensesAdapter.toggleSelection(position);
        int count = mExpensesAdapter.getSelectedItemCount();
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

    private void eraseExpenses() {
        DialogManager.getInstance().createCustomAcceptDialog(getActivity(), getString(R.string.delete), getString(R.string.confirm_delete_items), getString(R.string.confirm), getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    boolean isToday = false;
                    List<Expense> expensesToDelete = new ArrayList<>();
                    for (int position : mExpensesAdapter.getSelectedItems()) {
                        Expense expense = mExpensesList.get(position);
                        expensesToDelete.add(expense);
                        Date expenseDate = expense.getDate();
                        // update widget if the expense is created today
                        if (DateUtils.isToday(expenseDate)) {
                            isToday = true;
                        }
                    }
                    if (isToday) {
                        Intent i = new Intent(getActivity(), ExpensesWidgetProvider.class);
                        i.setAction(ExpensesWidgetService.UPDATE_WIDGET);
                        getActivity().sendBroadcast(i);
                    }
                    RealmManager.getInstance().delete(expensesToDelete);
                }
                updateData();
                mActionMode.finish();
                mActionMode = null;
            }
        });
    }
}
