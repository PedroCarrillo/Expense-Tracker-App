package com.pedrocarrillo.expensetracker.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Pedro on 9/28/2015.
 */
public class ExpensesWidgetService extends RemoteViewsService {

    public static final String UPDATE_WIDGET = "_update_widget_today";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        return (new ListProvider(this.getApplicationContext(), intent));
    }

}