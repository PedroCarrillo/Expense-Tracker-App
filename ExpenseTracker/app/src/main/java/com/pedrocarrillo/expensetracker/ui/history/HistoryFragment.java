package com.pedrocarrillo.expensetracker.ui.history;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.adapters.BaseExpenseAdapter;
import com.pedrocarrillo.expensetracker.custom.BaseViewHolder;
import com.pedrocarrillo.expensetracker.custom.DefaultRecyclerViewItemDecorator;
import com.pedrocarrillo.expensetracker.custom.DividerRecyclerItemDecorator;
import com.pedrocarrillo.expensetracker.custom.SelectDateFragment;
import com.pedrocarrillo.expensetracker.custom.SparseBooleanArrayParcelable;
import com.pedrocarrillo.expensetracker.custom.WrapContentManagerRecyclerView;
import com.pedrocarrillo.expensetracker.entities.Expense;
import com.pedrocarrillo.expensetracker.interfaces.FileGeneratorParser;
import com.pedrocarrillo.expensetracker.interfaces.IConstants;
import com.pedrocarrillo.expensetracker.interfaces.IExpensesType;
import com.pedrocarrillo.expensetracker.interfaces.ISelectDateFragment;
import com.pedrocarrillo.expensetracker.ui.MainActivity;
import com.pedrocarrillo.expensetracker.ui.MainFragment;
import com.pedrocarrillo.expensetracker.ui.expenses.ExpenseDetailActivity;
import com.pedrocarrillo.expensetracker.ui.expenses.ExpenseDetailFragment;
import com.pedrocarrillo.expensetracker.utils.DateManager;
import com.pedrocarrillo.expensetracker.utils.DialogManager;
import com.pedrocarrillo.expensetracker.utils.ExpensesManager;
import com.pedrocarrillo.expensetracker.utils.FileManager;
import com.pedrocarrillo.expensetracker.utils.HistoryFileParser;
import com.pedrocarrillo.expensetracker.utils.Util;

import java.io.File;

/**
 * Created by pcarrillo on 07/10/2015.
 */
public class HistoryFragment extends MainFragment implements BaseViewHolder.RecyclerClickListener, ISelectDateFragment {

    private RecyclerView rvHistory;

    private BaseExpenseAdapter mExpensesAdapter;
    private SelectDateFragment selectDateFragment;

    public static final int REQUEST_WRITE_EXTERNAL_STORE = 101;

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
        rvHistory = (RecyclerView)rootView.findViewById(R.id.rv_history);
        selectDateFragment = (SelectDateFragment)getChildFragmentManager().findFragmentById(R.id.select_date_fragment);
        selectDateFragment.setSelectDateFragment(this);
        setHasOptionsMenu(true);
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMainActivityListener.setMode(MainActivity.NAVIGATION_MODE_STANDARD);
        mMainActivityListener.setTitle(getString(R.string.history));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setAutoMeasureEnabled(true);
        rvHistory.setLayoutManager(linearLayoutManager);
        rvHistory.addItemDecoration(
                new DividerRecyclerItemDecorator(getActivity()));
//        rvHistory.addItemDecoration(new DefaultRecyclerViewItemDecorator(getResources().getDimension(R.dimen.dimen_10dp)));
        rvHistory.setNestedScrollingEnabled(false);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(IConstants.TAG_SELECTED_ITEMS)) {
                mExpensesAdapter.setSelectedItems((SparseBooleanArray) savedInstanceState.getParcelable(IConstants.TAG_SELECTED_ITEMS));
                mExpensesAdapter.notifyDataSetChanged();
            }
            boolean isActionMode = savedInstanceState.getBoolean(IConstants.IS_ACTION_MODE_ACTIVATED);
            if(isActionMode) {
                mActionMode = mMainActivityListener.setActionMode(mActionModeCallback);
                mActionMode.setTitle(String.valueOf(mExpensesAdapter.getSelectedItems().size()));
                mActionMode.invalidate();
            }
        }
    }

    @Override
    public void updateData() {
        float total = Expense.getTotal(DateManager.getInstance().getDateFrom(), DateManager.getInstance().getDateTo());
        ExpensesManager.getInstance().setExpensesList(DateManager.getInstance().getDateFrom(), DateManager.getInstance().getDateTo(), null);
        if ( mExpensesAdapter == null ) {
            mExpensesAdapter = new BaseExpenseAdapter(getActivity(), this);
            rvHistory.setAdapter(mExpensesAdapter);
        } else {
            mExpensesAdapter.updateExpenses(ExpensesManager.getInstance().getExpensesList());
        }
        TextView totalTextView = selectDateFragment.getTextViewTotal();
        totalTextView.setText(Util.getFormattedCurrency(total));
        totalTextView.setTextColor(getResources().getColor(Expense.getExpensesTotalIntColorResource(DateManager.getInstance().getDateFrom(), DateManager.getInstance().getDateTo())));
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IConstants.IS_ACTION_MODE_ACTIVATED, mActionMode != null);
        outState.putParcelable(IConstants.TAG_SELECTED_ITEMS, new SparseBooleanArrayParcelable(mExpensesAdapter.getSelectedBooleanArray()));
        super.onSaveInstanceState(outState);
    }

    private void eraseExpenses() {
        DialogManager.getInstance().createCustomAcceptDialog(getActivity(), getString(R.string.delete), getString(R.string.confirm_delete_items), getString(R.string.confirm), getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    ExpensesManager.getInstance().eraseSelectedExpenses();
                }
                updateData();
                mActionMode.finish();
                mActionMode = null;
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_history, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_export) {
            exportExpenses();
        }
        return super.onOptionsItemSelected(item);
    }

    private void exportExpenses() {
        if(PackageManager.PERMISSION_GRANTED== ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            writeFile();
        } else{
            requestPermission(getActivity());
        }
    }

    public void requestPermission(final Context context) {
        if(ActivityCompat.shouldShowRequestPermissionRationale((Activity)context,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.

            new AlertDialog.Builder(context)
                    .setMessage(context.getString(R.string.write_external_message))
                    .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    REQUEST_WRITE_EXTERNAL_STORE);
                        }
                    }).show();

        }else {
            // permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions((Activity)context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_EXTERNAL_STORE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    writeFile();
                }
                return;
            }
        }
    }

    private void writeFile() {
        File expensesFile = FileManager.generateFile(new HistoryFileParser());

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(expensesFile));
        StringBuilder titleBuilder = new StringBuilder();
        titleBuilder.append(getString(R.string.app_name)).append(" ").append(Util.formatDateToString(DateManager.getInstance().getDateFrom(), Util.getCurrentDateFormat())).append(" - ").append(Util.formatDateToString(DateManager.getInstance().getDateTo(), Util.getCurrentDateFormat()));
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, titleBuilder.toString());
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.expenses_mail_content));
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share_title)));
    }
    
}
