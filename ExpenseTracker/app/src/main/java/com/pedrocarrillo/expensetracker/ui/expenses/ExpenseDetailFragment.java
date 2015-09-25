package com.pedrocarrillo.expensetracker.ui.expenses;

import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.view.BarChartView;
import com.db.chart.view.Tooltip;
import com.db.chart.view.XController;
import com.db.chart.view.YController;
import com.db.chart.view.animation.Animation;
import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.entities.Expense;
import com.pedrocarrillo.expensetracker.interfaces.IUserActionsMode;
import com.pedrocarrillo.expensetracker.ui.BaseFragment;
import com.pedrocarrillo.expensetracker.utils.DateUtils;
import com.pedrocarrillo.expensetracker.utils.RealmManager;
import com.pedrocarrillo.expensetracker.utils.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ExpenseDetailFragment extends BaseFragment implements View.OnClickListener {

    public static final String EXPENSE_ID_KEY = "_expense_id";
    public static final int RQ_EDIT_EXPENSE = 1001;

    private BarChartView bcvWeekExpenses;
    private List<Float> valuesPerDay;
    private Expense expense;

    static ExpenseDetailFragment newInstance(String id) {
        ExpenseDetailFragment expenseDetailFragment = new ExpenseDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXPENSE_ID_KEY, id);
        expenseDetailFragment.setArguments(bundle);
        return expenseDetailFragment;
    }

    public ExpenseDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = onCreateFragmentView(R.layout.fragment_expense_detail, inflater, container, true);
        bcvWeekExpenses = (BarChartView) rootView.findViewById(R.id.linechart);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            String id = getArguments().getString(EXPENSE_ID_KEY);
            expense = (Expense) RealmManager.getInstance().findById(Expense.class, id);
            loadData();
            setWeekChart();
        }
    }

    private void loadData() {
        ((TextView)getView().findViewById(R.id.tv_total)).setText(String.valueOf(expense.getTotal()));
        ((TextView)getView().findViewById(R.id.tv_category)).setText(String.valueOf(expense.getCategory().getName()));
        ((TextView)getView().findViewById(R.id.tv_description)).setText(String.valueOf(expense.getDescription()));
        (getView().findViewById(R.id.fab_edit)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab_edit) {
            onEditExpense();
        }
    }

    private void onEditExpense() {
        NewExpenseFragment newExpenseFragment = NewExpenseFragment.newInstance(IUserActionsMode.MODE_UPDATE, expense.getId());
        newExpenseFragment.setTargetFragment(this, RQ_EDIT_EXPENSE);
        newExpenseFragment.show(getChildFragmentManager(), "EDIT_EXPENSE");
    }

    private void setWeekChart() {
        List<Date> dateList = DateUtils.getWeekDates();

        Runnable action =  new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        if( getActivity() != null) showTooltipWeekChart();
                    }
                }, 500);
            }
        };

        Tooltip tip = new Tooltip(getActivity(), R.layout.tooltip_bar_chart);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            tip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1));
            tip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA,0));
        }

        bcvWeekExpenses.setTooltips(tip);
        BarSet barSet = new BarSet();
        valuesPerDay = new ArrayList<>();
        Collections.sort(dateList);
        for (Date date : dateList) {
            String day = Util.formatDateToString(date, "EEE");
            float value = Expense.getCategoryTotalByDate(date, expense.getCategory());
            valuesPerDay.add(value);
            barSet.addBar(new Bar(day, value));
        }
        barSet.setColor(getResources().getColor(R.color.colorPrimaryLight));
        bcvWeekExpenses.setSetSpacing(Tools.fromDpToPx(-15));
        bcvWeekExpenses.setRoundCorners(Tools.fromDpToPx(2));
        bcvWeekExpenses.addData(barSet);
        bcvWeekExpenses.setBarSpacing(Tools.fromDpToPx(35));
        int maxValue = Math.round(Collections.max(valuesPerDay));
        bcvWeekExpenses.setBorderSpacing(0)
                .setAxisBorderValues(0, maxValue, 10)
                .setAxisColor(getResources().getColor(R.color.grey))
                .setLabelsColor(getResources().getColor(R.color.colorPrimaryDark))
                .setYAxis(false)
                .setYLabels(YController.LabelPosition.NONE)
                .setXLabels(XController.LabelPosition.OUTSIDE);

        int[] order = {0, 1, 2, 3, 4, 5, 6};
        bcvWeekExpenses.show(new Animation()
                .setOverlap(.3f, order)
                .setEndAction(action));

    }

    private void showTooltipWeekChart(){
        ArrayList<ArrayList<Rect>> areas = new ArrayList<>();
        areas.add(bcvWeekExpenses.getEntriesArea(0));

        for(int i = 0; i < areas.size(); i++) {
            for (int j = 0; j < areas.get(i).size(); j++) {
                Tooltip tooltip = new Tooltip(getActivity(), R.layout.tooltip_bar_chart, R.id.value);
                tooltip.prepare(areas.get(i).get(j), valuesPerDay.get(j));
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    tooltip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1));
                    tooltip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 0));
                }
                bcvWeekExpenses.showTooltip(tooltip, true);
            }
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RQ_EDIT_EXPENSE && resultCode == Activity.RESULT_OK) {
            loadData();
            bcvWeekExpenses.dismissAllTooltips();
            bcvWeekExpenses.reset();
            setWeekChart();
        }
    }

}