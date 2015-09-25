package com.pedrocarrillo.expensetracker.ui.statistics;

import android.animation.PropertyValuesHolder;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.BarChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.view.Tooltip;
import com.db.chart.view.XController;
import com.db.chart.view.YController;
import com.db.chart.view.animation.Animation;
import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.entities.Category;
import com.pedrocarrillo.expensetracker.entities.Expense;
import com.pedrocarrillo.expensetracker.interfaces.IExpensesType;
import com.pedrocarrillo.expensetracker.ui.MainActivity;
import com.pedrocarrillo.expensetracker.ui.MainFragment;
import com.pedrocarrillo.expensetracker.utils.DateUtils;
import com.pedrocarrillo.expensetracker.utils.DialogManager;
import com.pedrocarrillo.expensetracker.utils.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by pcarrillo on 17/09/2015.
 */
public class StatisticsFragment extends MainFragment implements View.OnClickListener {

    private TextView tvDateFrom;
    private TextView tvDateTo;
    private BarChartView bcvCategories;
    private BarChartView bcvCategoriesPercentage;

    private Date mDateFrom;
    private Date mDateTo;
    private List<Float> valuesPerCategory;
    private List<Float> categoryPercentages;

    public static StatisticsFragment newInstance() {
        return new StatisticsFragment();
    }

    public StatisticsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivityListener.setMode(MainActivity.NAVIGATION_MODE_STANDARD);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_statistics, container, false);
        tvDateFrom = (TextView)rootView.findViewById(R.id.tv_date_from);
        tvDateTo = (TextView)rootView.findViewById(R.id.tv_date_to);
        bcvCategories = (BarChartView) rootView.findViewById(R.id.chartCategories);
        bcvCategoriesPercentage = (BarChartView) rootView.findViewById(R.id.chartCategoriesPercentages);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tvDateFrom.setOnClickListener(this);
        tvDateTo.setOnClickListener(this);
        mDateFrom = DateUtils.getFirstDateOfCurrentWeek();
        mDateTo = DateUtils.getTomorrowDate();
        updateDate(tvDateFrom, mDateFrom);
        updateDate(tvDateTo, mDateTo);
        setCategoryChart();
        setCategoryChartPercentage();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.tv_date_from || v.getId() == R.id.tv_date_to) {
            showDateDialog(v.getId());
        }
    }

    private void showDateDialog(final int id) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        DialogManager.getInstance()
                .showDatePicker(
                        getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                calendar.set(year, month, day);
                                if (id == R.id.tv_date_from) {
                                    mDateFrom = calendar.getTime();
                                    updateDate(tvDateFrom, mDateFrom);
                                } else {
                                    mDateTo = calendar.getTime();
                                    updateDate(tvDateTo, mDateTo);
                                }
                                bcvCategories.dismissAllTooltips();
                                bcvCategories.reset();
                                setCategoryChart();
                                bcvCategoriesPercentage.dismissAllTooltips();
                                bcvCategoriesPercentage.reset();
                                setCategoryChartPercentage();
                            }
                        },
                        calendar,
                        (R.id.tv_date_from == id) ? null : mDateTo,
                        (R.id.tv_date_from == id) ? mDateTo : null);
    }

    private void updateDate(TextView tv, Date date) {
        tv.setText(Util.formatDateToString(date, "MM/dd/yyyy"));
    }

    private void setCategoryChart() {
        List<Category> categoryList = Category.getCategoriesExpense();

        Runnable action =  new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        if( getActivity() != null) showTooltipCategoriesChart();
                    }
                }, 500);
            }
        };

        Tooltip tip = new Tooltip(getActivity(), R.layout.tooltip_bar_chart);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            tip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1));
            tip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA,0));
        }

        bcvCategories.setTooltips(tip);
        BarSet barSet = new BarSet();
        valuesPerCategory = new ArrayList<>();

        int[] order = new int[categoryList.size()];
        int pos = 0;
        for (Category category : categoryList) {
            float value = Expense.getCategoryTotalByDate(mDateFrom, mDateTo, category);
            valuesPerCategory.add(value);
            order[pos] = pos++;
            barSet.addBar(new Bar(category.getName(), value));
        }
        barSet.setColor(getResources().getColor(R.color.colorPrimaryLight));
        bcvCategories.setSetSpacing(Tools.fromDpToPx(-15));
        bcvCategories.setRoundCorners(Tools.fromDpToPx(2));
        bcvCategories.addData(barSet);
        bcvCategories.setBarSpacing(Tools.fromDpToPx(35));
        int maxValue = Math.round(Collections.max(valuesPerCategory));
        bcvCategories.setBorderSpacing(0)
                .setAxisBorderValues(0, maxValue, 10)
                .setAxisColor(getResources().getColor(R.color.grey))
                .setLabelsColor(getResources().getColor(R.color.colorPrimaryDark))
                .setYAxis(false)
                .setYLabels(YController.LabelPosition.NONE)
                .setXLabels(XController.LabelPosition.OUTSIDE);
        bcvCategories.setHorizontalScrollBarEnabled(true);
        Collections.shuffle(Arrays.asList(order));
        bcvCategories.show(new Animation()
                .setOverlap(.3f, order)
                .setEndAction(action));
    }

    private void showTooltipCategoriesChart(){
        ArrayList<ArrayList<Rect>> areas = new ArrayList<>();
        areas.add(bcvCategories.getEntriesArea(0));

        for(int i = 0; i < areas.size(); i++) {
            for (int j = 0; j < areas.get(i).size(); j++) {
                Tooltip tooltip = new Tooltip(getActivity(), R.layout.tooltip_bar_chart, R.id.value);
                tooltip.prepare(areas.get(i).get(j), valuesPerCategory.get(j));
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    tooltip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1));
                    tooltip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 0));
                }
                bcvCategories.showTooltip(tooltip, true);
            }
        }

    }

    private void setCategoryChartPercentage() {
        List<Category> categoryList = Category.getCategoriesExpense();

        Runnable action =  new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        if( getActivity() != null) showTooltipCategoriesPercentageChart();
                    }
                }, 500);
            }
        };

        Tooltip tip = new Tooltip(getActivity(), R.layout.tooltip_bar_chart);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            tip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1));
            tip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA,0));
        }

        bcvCategoriesPercentage.setTooltips(tip);
        BarSet barSet = new BarSet();
        categoryPercentages = new ArrayList<>();
        int[] order = new int[categoryList.size()];
        int pos = 0;
        for (Category category : categoryList) {
            float percentage = Expense.getExpensesCategoryPercentage(mDateFrom, mDateTo, category);
            categoryPercentages.add(percentage);
            order[pos] = pos++;
            barSet.addBar(new Bar(category.getName(), percentage));
        }
        barSet.setColor(getResources().getColor(R.color.colorPrimaryLight));
        bcvCategoriesPercentage.setSetSpacing(Tools.fromDpToPx(40));
        bcvCategoriesPercentage.setRoundCorners(Tools.fromDpToPx(2));
        bcvCategoriesPercentage.addData(barSet);
        bcvCategoriesPercentage.setBarSpacing(Tools.fromDpToPx(35));
        bcvCategoriesPercentage.setBorderSpacing(0)
                .setAxisBorderValues(0, 100, 10)
                .setAxisColor(getResources().getColor(R.color.grey))
                .setLabelsColor(getResources().getColor(R.color.colorPrimaryDark))
                .setYAxis(false)
                .setYLabels(YController.LabelPosition.NONE)
                .setXLabels(XController.LabelPosition.OUTSIDE);
        bcvCategoriesPercentage.setHorizontalScrollBarEnabled(true);
        Collections.shuffle(Arrays.asList(order));
        bcvCategoriesPercentage.show(new Animation()
                .setOverlap(.8f, order)
                .setEndAction(action));
    }

    private void showTooltipCategoriesPercentageChart(){
        ArrayList<ArrayList<Rect>> areas = new ArrayList<>();
        areas.add(bcvCategoriesPercentage.getEntriesArea(0));

        for(int i = 0; i < areas.size(); i++) {
            for (int j = 0; j < areas.get(i).size(); j++) {
                Tooltip tooltip = new Tooltip(getActivity(), R.layout.tooltip_bar_chart, R.id.value);
                tooltip.prepare(areas.get(i).get(j), categoryPercentages.get(j));
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    tooltip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1));
                    tooltip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 0));
                }
                bcvCategoriesPercentage.showTooltip(tooltip, true);
            }
        }

    }

}