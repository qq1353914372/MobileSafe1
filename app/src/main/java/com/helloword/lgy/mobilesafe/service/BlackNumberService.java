package com.helloword.lgy.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.helloword.lgy.mobilesafe.db.dao.BlackNumberDAO;

public class BlackNumberService extends Service {
    private InnerReceiver innerReceiver;
    private BlackNumberDAO mDao;
    private TelephonyManager tm;
    private Listener listener;

    public BlackNumberService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provier.Telephony.SMS_RECEIVED");
        filter.setPriority(1000);
        innerReceiver = new InnerReceiver();
        registerReceiver(innerReceiver, filter);
        mDao = BlackNumberDAO.getInstance(getApplicationContext());
    }

    class InnerReceiver extends BroadcastReceiver {
        //获取短信内容及号码，如果号码是黑名单内的号码以及拦截模式是1or3（拦截短信or所有）
        //拦截短信
        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] objs = (Object[]) intent.getExtras().get("pdus");
            for (Object obj : objs) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
                String sender = sms.getOriginatingAddress();
                String body = sms.getMessageBody();

                int mode = mDao.getMode(sender);
                if (mode == 1 || mode == 3) {
                    abortBroadcast();
                }
            }
            //监听电话状态
            tm= (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            listener= new Listener();
            tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);


        }
    }
    class Listener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE://空闲状态

                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://摘机

                    break;
                case TelephonyManager.CALL_STATE_RINGING://响铃
                    //响铃时判断来电号码是否是黑名单号码，是则挂断电话
                    endCall(incomingNumber);

                    break;
            }
        }
    }

    private void endCall(String phone) {
        // TODO: 2017/10/30 拦截电话未完成！！！
      int mode=  mDao.getMode(phone);
        if (mode==2||mode==3){
            //拦截电话
            //步骤：有电话拨入，处于响铃状态，响铃状态时通过代码挂断电话。
            //拦截电话的方法在aidl文件ITelephony中的endCall()方法
        }



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(innerReceiver);
    }
}
