package com.helloword.lgy.mobilesafe;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import java.util.Calendar;

public class TrafficService extends Service {
    private boolean isRanning;
    private Calendar c;
    private SharedPreferences sp;

    private Handler tHandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            c = Calendar.getInstance();
            int  hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            int day=c.get(Calendar.DAY_OF_MONTH);
            if (hour==0||minute==0){//每日开始统计流量
                saveTodayS();
            }

        }


    };
    private void saveMonthS() {

    }
    private void saveTodayS() {
        sp=getSharedPreferences("config",MODE_PRIVATE);
       SharedPreferences.Editor editor= sp.edit();
        long r = TrafficStats.getMobileRxBytes();
        long s = TrafficStats.getMobileTxBytes();
        long todaystart = r + s;
        editor.putLong("todaystart",todaystart);
        editor.commit();

    }


    public TrafficService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isRanning=true;
        new Thread(){
            @Override
            public void run() {
                super.run();
                while (isRanning){
                tHandler.sendEmptyMessage(0);
                    try {
                        Thread.sleep(1000*30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }

}
