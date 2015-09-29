package com.pedrocarrillo.expensetracker.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.ui.MainActivity;

/**
 * Created by Pedro on 9/28/2015.
 */
public class ExpensesWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds){
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);
            Intent widgetServiceIntent = new Intent(context, ExpensesWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            views.setRemoteAdapter(appWidgetId, R.id.listViewWidget, widgetServiceIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
//        Intent service_start = new Intent(context, myFetchService.class);
//        context.startService(service_start);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.e("widget", "on receive");
    }
}
