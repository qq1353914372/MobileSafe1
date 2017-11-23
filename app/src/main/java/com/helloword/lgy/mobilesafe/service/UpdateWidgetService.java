package com.helloword.lgy.mobilesafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.helloword.lgy.mobilesafe.HomeActivity;
import com.helloword.lgy.mobilesafe.R;
import com.helloword.lgy.mobilesafe.engine.ProcessInfoProvider;
import com.helloword.lgy.mobilesafe.receiver.MyAppWidget;

import java.util.Timer;
import java.util.TimerTask;

public class UpdateWidgetService extends Service {
    private Timer timer;
    private InnerBrocastReceiver innerBrocastReceiver;
    public UpdateWidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //开启定时器
        startTimer();

        //注册屏幕锁屏，解锁
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        innerBrocastReceiver=new InnerBrocastReceiver();
        //注册屏幕监听
        registerReceiver(innerBrocastReceiver,intentFilter);

    }

    class InnerBrocastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case Intent.ACTION_SCREEN_ON:
                    startTimer();
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    cancelTimer();
                    break;
            }
        }
    }

    private void startTimer() { //开启定时器
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //更新appwidget
                updateAppwidget();
                System.out.println("5秒一次的任务正在执行啊啊啊啊啊啊。。。");
            }
        },0,5000);
    }
    private void cancelTimer()
    {
        if (timer!=null){
            timer.cancel();
            timer=null;
        }
    }

    private void updateAppwidget() {
        //拿到widget对象
        AppWidgetManager aWM=AppWidgetManager.getInstance(this);
        //拿到远程的view对象
        RemoteViews remoteViews=new RemoteViews(getPackageName(), R.layout.process_widget);
        //给view对象赋值
        remoteViews.setTextViewText(R.id.tv_widget_processcount, "进程总数："+ProcessInfoProvider.getProcessCount(getApplicationContext()));
       String strAvailableSpace= Formatter.formatFileSize(getApplicationContext(),ProcessInfoProvider.getAvailableSpace(getApplicationContext()));
        remoteViews.setTextViewText(R.id.tv_widget_processmemory,"可用大小"+strAvailableSpace);
        //给Widget设置延迟点击事件
        //点击widget进入应用
        Intent intent=new Intent(this, HomeActivity.class);
        PendingIntent pendingIntent= PendingIntent.getActivities(this,0, new Intent[]{intent},PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ll_root,pendingIntent);

        //给按钮设置点击事件,点击按钮发送广播，在广播接收者里杀进程
        Intent sendreceiver =new Intent("android.intent.action.KILLPROCESS");
        PendingIntent pendingIntent1=PendingIntent.getBroadcast(this,0,sendreceiver,PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btn_clear,pendingIntent1);


        //更新widget
        ComponentName componentName=new ComponentName(this, MyAppWidget.class);
        aWM.updateAppWidget(componentName,remoteViews);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (innerBrocastReceiver!=null){

            unregisterReceiver(innerBrocastReceiver);
        }
            cancelTimer();

    }
}
