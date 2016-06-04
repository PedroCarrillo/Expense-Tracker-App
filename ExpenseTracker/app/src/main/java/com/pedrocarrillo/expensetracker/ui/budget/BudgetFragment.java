package com.pedrocarrillo.expensetracker.ui.budget;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.ui.BaseFragment;
import com.pedrocarrillo.expensetracker.utils.BudgetManager;
import com.pedrocarrillo.expensetracker.utils.DateUtils;

/**
 * Created by PedroCarrillo on 6/3/16.
 */

public class BudgetFragment extends BaseFragment {

    EditText monthlyBudget, monthlySavings;
    TextView infoSavings;

    public static BudgetFragment newInstance() {
        return new BudgetFragment();
    }

    public BudgetFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_budget, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            saveBudget();
            mFragmentListener.closeActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveBudget() {
        float monthlyBudgetTotal = Float.valueOf(monthlyBudget.getText().toString());
        float monthlySavingsTotal = Float.valueOf(monthlySavings.getText().toString());
        BudgetManager.getInstance().saveBudget(monthlyBudgetTotal);
        BudgetManager.getInstance().saveIntendedSavings(monthlySavingsTotal);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = onCreateFragmentView(R.layout.fragment_budget, inflater, container, true);
        monthlyBudget = (EditText)rootView.findViewById(R.id.monthly_budget);
        monthlySavings = (EditText)rootView.findViewById(R.id.monthly_savings);
        infoSavings = (TextView)rootView.findViewById(R.id.info_savings);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        monthlyBudget.setText(String.valueOf(BudgetManager.getInstance().getCurrentBudget()));
        monthlySavings.setText(String.valueOf(BudgetManager.getInstance().getIntendedSavings()));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextWatcher budgetTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(monthlySavings.getText().toString().isEmpty() || monthlyBudget.getText().toString().isEmpty() || monthlySavings.getText().toString().equals("0") || monthlyBudget.getText().toString().equals("0")) {
                    infoSavings.setVisibility(View.INVISIBLE);
                } else {
                    infoSavings.setVisibility(View.VISIBLE);
                    setInfoData();
                }
            }
        };
        monthlySavings.addTextChangedListener(budgetTextWatcher);
        monthlyBudget.addTextChangedListener(budgetTextWatcher);
    }

    private void setInfoData() {
        float monthlyBudgetTotal = Float.valueOf(monthlyBudget.getText().toString());
        float monthlySavingsTotal = Float.valueOf(monthlySavings.getText().toString());
        int totalDays = DateUtils.getDaysOfCurrentMonth();
        float monthly = monthlyBudgetTotal - monthlySavingsTotal;
        float daily = monthly / totalDays;
        float weekly = monthly / DateUtils.getNumberOfWeeksOfCurrentMonth();
        infoSavings.setText(getString(R.string.budget_info, String.valueOf(daily), String.valueOf(weekly), String.valueOf(monthly), DateUtils.currentMonth()));
    }

}
