package com.helloword.lgy.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.helloword.lgy.mobilesafe.engine.ProcessInfoProvider;

public class LockScreenClearService extends Service {
    private InnerLockScreenReceiver receiver;
    public LockScreenClearService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //注册监听锁屏广播时间
        IntentFilter intentFilter=new IntentFilter(Intent.ACTION_SCREEN_OFF);
        receiver=new InnerLockScreenReceiver();
        registerReceiver(receiver,intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver!=null){

            unregisterReceiver(receiver);
        }

    }
    class InnerLockScreenReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO: 2017/11/3 锁屏清理成功  但是有  ：Permission denied
            ProcessInfoProvider.killAllProcess(context);
        }
    }
}
