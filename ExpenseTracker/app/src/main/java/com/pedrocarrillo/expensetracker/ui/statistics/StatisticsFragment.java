package com.pedrocarrillo.expensetracker.ui.statistics;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.github.mikephil.charting.utils.ColorTemplate;
import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.entities.Category;
import com.pedrocarrillo.expensetracker.entities.Expense;
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
public class StatisticsFragment extends MainFragment implements View.OnClickListener {

    private TextView tvDateFrom;
    private TextView tvDateTo;
    private TextView tvTotal;

    private PieChart pcCategories;
    private BarChart bcCategories;

    private Date mDateFrom;
    private Date mDateTo;
    private List<Category> mCategoryList;

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
        tvTotal = (TextView)rootView.findViewById(R.id.tv_total);
        pcCategories = (PieChart) rootView.findViewById(R.id.pc_categories);
        bcCategories = (BarChart) rootView.findViewById(R.id.bc_categories);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCategoryList = Category.getCategoriesExpense();
        tvDateFrom.setOnClickListener(this);
        tvDateTo.setOnClickListener(this);
        mDateFrom = DateUtils.getFirstDateOfCurrentWeek();
        mDateTo = DateUtils.getTomorrowDate();
        updateDate(tvDateFrom, mDateFrom);
        updateDate(tvDateTo, mDateTo);
        updateData();
    }

    private void updateData() {
        float total = Expense.getCategoryTotalByDate(mDateFrom, mDateTo, null);
        tvTotal.setText(Util.getFormattedCurrency(total));
        setCategoriesBarChart();
        setCategoriesPieChart();
    }

    private void setCategoriesBarChart() {
        List<String> categoriesNames = new ArrayList<>();
        List<BarEntry> entryPerCategory = new ArrayList<>();

        for (int i=0; i < mCategoryList.size(); i++) {
            float value = Expense.getCategoryTotalByDate(mDateFrom, mDateTo, mCategoryList.get(i));
            if (value != 0) {
                categoriesNames.add(mCategoryList.get(i).getName());
                entryPerCategory.add(new BarEntry(value, i));
            }
        }

        BarDataSet dataSet = new BarDataSet(entryPerCategory, "categories");
        dataSet.setColors(Util.getListColors());
        BarData barData = new BarData(categoriesNames, dataSet);
        bcCategories.setVisibleXRangeMaximum(5);
        bcCategories.getAxisLeft().setDrawGridLines(false);
        bcCategories.getXAxis().setDrawGridLines(false);
        bcCategories.getAxisRight().setDrawGridLines(false);
        bcCategories.getAxisRight().setDrawLabels(false);
        bcCategories.setData(barData);
        bcCategories.setDescription("");
        bcCategories.animateY(2000);
        bcCategories.invalidate();
    }

    private void setCategoriesPieChart() {

        pcCategories.setCenterText("");
        pcCategories.setCenterTextSize(10f);
        pcCategories.setHoleRadius(50f);
        pcCategories.setTransparentCircleRadius(55f);
        pcCategories.setUsePercentValues(true);

        Legend l = pcCategories.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);
        pcCategories.animateY(1500, Easing.EasingOption.EaseInOutQuad);

        List<String> categoriesNames = new ArrayList<>();
        List<Entry> categoryPercentagesEntries = new ArrayList<>();

        for (int i=0; i < mCategoryList.size(); i++) {
            float percentage = Expense.getExpensesCategoryPercentage(mDateFrom, mDateTo, mCategoryList.get(i));
            if( percentage != 0) {
                categoriesNames.add(mCategoryList.get(i).getName());
                Entry pieEntry = new Entry(percentage, i);
                categoryPercentagesEntries.add(pieEntry);
            }
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
        pcCategories.setDescription("");

        pcCategories.invalidate();

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
                                bcCategories.notifyDataSetChanged();
                                bcCategories.invalidate();
                                pcCategories.notifyDataSetChanged();
                                pcCategories.invalidate();
                                updateData();
                            }
                        },
                        calendar,
                        (R.id.tv_date_from == id) ? null : mDateFrom,
                        (R.id.tv_date_from == id) ? mDateTo : null);
    }

    private void updateDate(TextView tv, Date date) {
        tv.setText(Util.formatDateToString(date, "MM/dd/yyyy"));
    }

}