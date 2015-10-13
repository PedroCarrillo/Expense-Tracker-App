package com.pedrocarrillo.expensetracker.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.pedrocarrillo.expensetracker.ExpenseTrackerApp;
import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.entities.Expense;
import com.pedrocarrillo.expensetracker.interfaces.IExpensesType;
import com.pedrocarrillo.expensetracker.ui.expenses.ExpenseDetailFragment;
import com.pedrocarrillo.expensetracker.utils.DateUtils;
import com.pedrocarrillo.expensetracker.utils.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

/**
 * Created by Pedro on 9/28/2015.
 */
public class ListProvider implements RemoteViewsService.RemoteViewsFactory {

    private Context context = null;
    private int appWidgetId;
    private List<Expense> expenseList;

    public ListProvider(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        getExpenses();
    }

    @Override
    public void onDataSetChanged() {
        getExpenses();

    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getCount() {
        return expenseList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
    *Similar to getView of Adapter where instead of View
    *we return RemoteViews
    *
    */
    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.layout_expense_widget_item);
        Expense expense = expenseList.get(position);
        remoteView.setTextViewText(R.id.tv_category, String.valueOf(expense.getCategory().getName()));
        remoteView.setTextViewText(R.id.tv_description, expense.getDescription());
        remoteView.setTextViewText(R.id.tv_total, Util.getFormattedCurrency(expense.getTotal()));
        remoteView.setViewVisibility(R.id.tv_description, (expense.getDescription() != null && !expense.getDescription().isEmpty()) ? View.VISIBLE : View.GONE);
        remoteView.setTextColor(R.id.tv_total, expense.getType() == IExpensesType.MODE_EXPENSES ? context.getResources().getColor(R.color.colorAccentRed) : context.getResources().getColor(R.color.colorAccentGreen));
        Intent expenseIntent = new Intent();
        expenseIntent.putExtra(ExpenseDetailFragment.EXPENSE_ID_KEY, expense.getId());
        remoteView.setOnClickFillInIntent(R.id.widget_item, expenseIntent);
        return remoteView;
    }


    public void getExpenses() {
        Date today = DateUtils.getToday();
        Date tomorrow = DateUtils.getTomorrowDate();
        this.expenseList = new ArrayList<>();
        Realm realm = Realm.getInstance(context);
        //sync realm instance with other instances
        realm.refresh();
        this.expenseList = Expense.cloneExpensesCollection(realm.where(Expense.class).between("date", today, tomorrow).findAll());
    }

}
