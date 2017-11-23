package com.helloword.lgy.mobilesafe.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.helloword.lgy.mobilesafe.R;
import com.helloword.lgy.mobilesafe.service.UpdateWidgetService;

/**
 * Implementation of App Widget functionality.
 */
public class MyAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.process_widget);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    //创建第一个widget部件时调用
    @Override
    public void onEnabled(Context context) {
        //开启更新widget服务
        context.startService(new Intent(context, UpdateWidgetService.class));


    }
    //每当添加一个widget部件时调用
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
            //开启更新widget服务
            context.startService(new Intent(context, UpdateWidgetService.class));
        }
    }

    //当widget部件的宽高发生改变的时候调用  部件从无到有或者拉伸宽高
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        //开启更新widget服务
        context.startService(new Intent(context, UpdateWidgetService.class));
    }

    //每当删除一个widget部件的时候调用
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);

    }
    //当最后一个widget部件被删除的时候调用
    @Override
    public void onDisabled(Context context) {
        //关闭服务
        context.stopService(new Intent(context, UpdateWidgetService.class));
    }
}

