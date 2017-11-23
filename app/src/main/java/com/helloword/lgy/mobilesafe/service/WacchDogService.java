package com.helloword.lgy.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

import com.helloword.lgy.mobilesafe.EnterPwdActivity;
import com.helloword.lgy.mobilesafe.db.dao.AppLockDAO;

import java.util.List;

public class WacchDogService extends Service {
    boolean isWatch;
    private AppLockDAO mDAO;
    private List<String> mLockPackNameList;
    private InnerBrocastReceiver mReceiver;
    private String mSkippackagename;
    private  MyContentObserver myContentObserver;

    public WacchDogService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //定义看门狗，一个可控制的死循环，在循环里监听开启的应用并对比是否为加锁应用
        mDAO = AppLockDAO.getInstance(this);
        isWatch = true;
        watch();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SKIP");
        mReceiver = new InnerBrocastReceiver();
        registerReceiver(mReceiver, intentFilter);
        //注册内容观察者
        myContentObserver=new MyContentObserver(new Handler());
        getContentResolver().registerContentObserver(Uri.parse("content://applock//change"),true,myContentObserver);

    }
    class MyContentObserver extends ContentObserver{
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    mLockPackNameList = mDAO.queryAll();
                }
            }.start();

        }
    }
    class InnerBrocastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mSkippackagename = intent.getStringExtra("skippackagename");
        }
    }

    private void watch() {
        //死循环为耗时操作，开子线程
        new Thread() {
            @Override
            public void run() {
                super.run();
                mLockPackNameList = mDAO.queryAll();
                while (isWatch) {
                    ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                    List<ActivityManager.RunningTaskInfo> taskInfoList = am.getRunningTasks(1);
                    ActivityManager.RunningTaskInfo taskInfo = taskInfoList.get(0);
                    String taskPackagename = taskInfo.topActivity.getPackageName();
                    if (mLockPackNameList.contains(taskPackagename)) {
                        if (!taskPackagename.equals(mSkippackagename)) {
                            Intent intent = new Intent(getApplicationContext(), EnterPwdActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("taskPackagename", taskPackagename);
                            startActivity(intent);
                        }
                    }
                    //稍微睡眠一下让循环太频繁消耗内存
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }.start();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //让看门狗的死循环停止
        isWatch=false;
        //注销内容观察者
        if (myContentObserver!=null){
            getContentResolver().unregisterContentObserver(myContentObserver);
        }
        //注销广播接受者
        if (mReceiver!=null){

            unregisterReceiver(mReceiver);
        }
    }
}
