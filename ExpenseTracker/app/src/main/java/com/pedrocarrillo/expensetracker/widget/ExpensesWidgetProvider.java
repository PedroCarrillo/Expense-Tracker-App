package com.pedrocarrillo.expensetracker.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.entities.Expense;
import com.pedrocarrillo.expensetracker.interfaces.IDateMode;
import com.pedrocarrillo.expensetracker.ui.MainActivity;
import com.pedrocarrillo.expensetracker.utils.Util;

/**
 * Created by Pedro on 9/28/2015.
 */
public class ExpensesWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds){
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent clickPendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            Intent widgetServiceIntent = new Intent(context, ExpensesWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            remoteView.setOnClickPendingIntent(R.id.widget, clickPendingIntent);

            remoteView.setEmptyView(R.id.listViewWidget, R.id.empty_view);
            remoteView.setRemoteAdapter(appWidgetId, R.id.listViewWidget, widgetServiceIntent);
            remoteView.setTextViewText(R.id.tv_total, context.getString(R.string.today_expenses_total, Util.getFormattedCurrency(Expense.getTotalExpensesByDateMode(IDateMode.MODE_TODAY))));
            remoteView.setPendingIntentTemplate(R.id.listViewWidget, clickPendingIntent);
            ComponentName component=new ComponentName(context, ExpensesWidgetProvider.class);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listViewWidget);
            appWidgetManager.updateAppWidget(component, remoteView);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ExpensesWidgetService.UPDATE_WIDGET)) {
            AppWidgetManager gm = AppWidgetManager.getInstance(context);
            int[] ids = gm.getAppWidgetIds(new ComponentName(context, ExpensesWidgetProvider.class));
            this.onUpdate(context, gm, ids);
        } else {
            super.onReceive(context, intent);
        }
    }
}
