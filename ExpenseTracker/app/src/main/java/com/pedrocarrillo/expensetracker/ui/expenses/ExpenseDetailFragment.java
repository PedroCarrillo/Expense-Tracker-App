package com.pedrocarrillo.expensetracker.ui.expenses;

import android.animation.PropertyValuesHolder;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.HorizontalBarChartView;
import com.db.chart.view.Tooltip;
import com.db.chart.view.XController;
import com.db.chart.view.animation.Animation;
import com.google.android.gms.ads.internal.purchase.GInAppPurchaseManagerInfoParcel;
import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.entities.Expense;
import com.pedrocarrillo.expensetracker.ui.BaseFragment;
import com.pedrocarrillo.expensetracker.utils.DateUtils;
import com.pedrocarrillo.expensetracker.utils.RealmManager;
import com.pedrocarrillo.expensetracker.utils.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import io.realm.RealmObject;

/**
 * A placeholder fragment containing a simple view.
 */
public class ExpenseDetailFragment extends BaseFragment {

    public static final String EXPENSE_ID_KEY = "_expense_id";
    private HorizontalBarChartView chartOtherExpensesWeek;
    private TextView tvTwo;
    private TextView tvTwoMetric;

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            String id = getArguments().getString(EXPENSE_ID_KEY);
            Expense expense = (Expense) RealmManager.getInstance().findById(Expense.class, id);
            List<Date> dateList = DateUtils.getWeekDates();

            Runnable action =  new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {

                        }
                    }, 500);
                }
            };

            Tooltip tip = new Tooltip(getActivity(), R.layout.barchart_two_tooltip);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                tip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1));
                tip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA,0));
            }
            chartOtherExpensesWeek.setTooltips(tip);

            BarSet barSet = new BarSet();
            Bar bar;
            List<Float> valuesPerDay = new ArrayList<>();
            Collections.sort(dateList);
            for (Date date : dateList) {
                String day = Util.formatDateToString(date, "EEE");
                float value = Expense.getCategoryTotalByDate(date, expense.getCategory());

                valuesPerDay.add(value);
                bar = new Bar(day, value);
                barSet.addBar(bar);
            }

            chartOtherExpensesWeek.addData(barSet);
            chartOtherExpensesWeek.setBarSpacing(Tools.fromDpToPx(10));
            
            int maxValue = Math.round(Collections.max(valuesPerDay));
            chartOtherExpensesWeek.setBorderSpacing(0)
                    .setAxisBorderValues(0, maxValue, 10)
                    .setXAxis(false)
                    .setYAxis(false)
                    .setLabelsColor(Color.parseColor("#FF8E8A84"))
                    .setXLabels(XController.LabelPosition.NONE);


            int[] order = {4, 3, 2, 1, 5, 0, 6};
            chartOtherExpensesWeek.show(new Animation()
                    .setOverlap(.5f, order)
                    .setEndAction(action));

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_expense_detail, container, false);
        chartOtherExpensesWeek = (HorizontalBarChartView) rootView.findViewById(R.id.linechart);
        return rootView;
    }
}
