package com.pedrocarrillo.expensetracker.ui.statistics;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.custom.SelectDateFragment;
import com.pedrocarrillo.expensetracker.entities.Category;
import com.pedrocarrillo.expensetracker.entities.Expense;
import com.pedrocarrillo.expensetracker.interfaces.ISelectDateFragment;
import com.pedrocarrillo.expensetracker.ui.MainActivity;
import com.pedrocarrillo.expensetracker.ui.MainFragment;
import com.pedrocarrillo.expensetracker.utils.DateUtils;
import com.pedrocarrillo.expensetracker.utils.DialogManager;
import com.pedrocarrillo.expensetracker.utils.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by pcarrillo on 17/09/2015.
 */
public class StatisticsFragment extends MainFragment implements ISelectDateFragment {

    private TextView tvPcCategoriesEmpty;
    private TextView tvBcCategoriesEmpty;

    private PieChart pcCategories;
    private BarChart bcCategories;

    private List<Category> mCategoryList;
    private SelectDateFragment selectDateFragment;

    public static StatisticsFragment newInstance() {
        return new StatisticsFragment();
    }

    public StatisticsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_statistics, container, false);
        pcCategories = (PieChart) rootView.findViewById(R.id.pc_categories);
        bcCategories = (BarChart) rootView.findViewById(R.id.bc_categories);
        tvPcCategoriesEmpty = (TextView)rootView.findViewById(R.id.tv_bar_chart_category_empty);
        tvBcCategoriesEmpty = (TextView)rootView.findViewById(R.id.tv_pie_categories_chart_empty);
        selectDateFragment = (SelectDateFragment)getChildFragmentManager().findFragmentById(R.id.select_date_fragment);
        selectDateFragment.setSelectDateFragment(this);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMainActivityListener.setMode(MainActivity.NAVIGATION_MODE_STANDARD);
        mMainActivityListener.setTitle(getString(R.string.statistics));
        mCategoryList = Category.getCategoriesExpense();
        setupCharts();
    }

    private void setupCharts() {

        // set up pie chart
        pcCategories.setCenterText("");
        pcCategories.setCenterTextSize(10f);
        pcCategories.setHoleRadius(50f);
        pcCategories.setTransparentCircleRadius(55f);
        pcCategories.setUsePercentValues(true);
        pcCategories.setDescription("");
        pcCategories.setNoDataText("");

        Legend l = pcCategories.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);
        pcCategories.animateY(1500, Easing.EasingOption.EaseInOutQuad);

    }

    @Override
    public void updateData() {
        // Bar Chart
        bcCategories.setDescription("");
        bcCategories.setNoDataText("");
        bcCategories.animateY(2000);
        bcCategories.setVisibleXRangeMaximum(5);
        bcCategories.getAxisLeft().setDrawGridLines(false);
        bcCategories.getXAxis().setDrawGridLines(false);
        bcCategories.getAxisRight().setDrawGridLines(false);
        bcCategories.getAxisRight().setDrawLabels(false);

        // Restarting chart views
        bcCategories.notifyDataSetChanged();
        bcCategories.invalidate();
        pcCategories.notifyDataSetChanged();
        pcCategories.invalidate();

        float total = Expense.getCategoryTotalByDate(selectDateFragment.getDateFrom(), selectDateFragment.getDateTo(), null);
        selectDateFragment.getTextViewTotal().setText(Util.getFormattedCurrency(total));
        setCategoriesBarChart();
        setCategoriesPieChart();
    }

    private void setCategoriesBarChart() {
        List<String> categoriesNames = new ArrayList<>();
        List<BarEntry> entryPerCategory = new ArrayList<>();

        for (int i=0; i < mCategoryList.size(); i++) {
            float value = Expense.getCategoryTotalByDate(selectDateFragment.getDateFrom(), selectDateFragment.getDateTo(), mCategoryList.get(i));
            if (value > 0) {
                categoriesNames.add(mCategoryList.get(i).getName());
                entryPerCategory.add(new BarEntry(value, categoriesNames.size()-1));
            }
        }
        if (categoriesNames.isEmpty()) {
            tvBcCategoriesEmpty.setVisibility(View.VISIBLE);
            bcCategories.setVisibility(View.GONE);
        } else {
            tvBcCategoriesEmpty.setVisibility(View.GONE);
            bcCategories.setVisibility(View.VISIBLE);
        }
        BarDataSet dataSet = new BarDataSet(entryPerCategory, getString(R.string.categories));
        dataSet.setColors(Util.getListColors());
        BarData barData = new BarData(categoriesNames, dataSet);
        bcCategories.setData(barData);
        bcCategories.invalidate();
    }

    private void setCategoriesPieChart() {

        List<String> categoriesNames = new ArrayList<>();
        List<Entry> categoryPercentagesEntries = new ArrayList<>();

        for (int i=0; i < mCategoryList.size(); i++) {
            float percentage = Expense.getExpensesCategoryPercentage(selectDateFragment.getDateFrom(), selectDateFragment.getDateTo(), mCategoryList.get(i));
            if( percentage > 0) {
                categoriesNames.add(mCategoryList.get(i).getName());
                Entry pieEntry = new Entry(percentage, categoriesNames.size()-1);
                categoryPercentagesEntries.add(pieEntry);
            }
        }
        if (categoriesNames.isEmpty()) {
            tvPcCategoriesEmpty.setVisibility(View.VISIBLE);
            bcCategories.setVisibility(View.GONE);
        } else {
            tvPcCategoriesEmpty.setVisibility(View.GONE);
            bcCategories.setVisibility(View.VISIBLE);
        }

        PieDataSet dataSet = new PieDataSet(categoryPercentagesEntries, "Categories");
        dataSet.setSliceSpace(1f);
        dataSet.setSelectionShift(5f);

        dataSet.setColors(Util.getListColors());

        PieData data = new PieData(categoriesNames, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(getResources().getColor(R.color.primary_dark));
        pcCategories.setData(data);
        pcCategories.invalidate();

    }


}