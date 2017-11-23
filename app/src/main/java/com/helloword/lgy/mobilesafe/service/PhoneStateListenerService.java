package com.helloword.lgy.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.helloword.lgy.mobilesafe.R;
import com.helloword.lgy.mobilesafe.engine.AdressDao;

public class PhoneStateListenerService extends Service {

    TelephonyManager tm;
    MyListener myListener;
    InnerOutCallReceiver receiver;



// TODO: 2017/10/23 吐司样式已完成  下一步实现拖拽吐司

    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private View mView;
    private WindowManager mWM;
    private String mAdress;

    private TextView textView;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            textView.setText(mAdress);

        }
    };

    public PhoneStateListenerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //sp


        //注册来电电话状态监听
        tm= (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        myListener= new MyListener();
        tm.listen(myListener, PhoneStateListener.LISTEN_CALL_STATE);

        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);

        //注册去电广播监听
        //去电广播监听过滤条件
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        //创建广播接收者
        receiver=new InnerOutCallReceiver();

        registerReceiver(receiver,intentFilter);


    }
    class InnerOutCallReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            showToast(getResultData());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销电话监听
        if (tm!=null&&myListener!=null){
            tm.listen(myListener,PhoneStateListener.LISTEN_NONE);
        }
        if (receiver!=null){
            unregisterReceiver(receiver);
        }



    }

    public void showToast(String incomingNumber) {
        //自定义Toast
        // XXX This should be changed to use a Dialog, with a Theme.Toast
        // defined that sets up the layout params appropriately.
        final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;//高
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;//宽
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;//类型
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON//屏幕亮时显示到屏幕
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;//不能获取焦点
        //| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;//不能被触摸
        //params.gravity = Gravity.LEFT + Gravity.TOP;//位置在左上
        //拿到保存在sp的自定义吐司的位置
        SharedPreferences sp=getSharedPreferences("config",MODE_PRIVATE);
        int locationX=sp.getInt("locationX",0);
        int locationY =sp.getInt("locationY",0);
        params.gravity = locationX +locationY;

        //查询号码归属地
        query(incomingNumber);

        mView = View.inflate(PhoneStateListenerService.this, R.layout.toast_view, null);
        textView = (TextView) mView.findViewById(R.id.tv_toast);

        //
        int[] mipmapId=new int[]{
                R.mipmap.t,//透明
                R.mipmap.c,//橙色
                R.mipmap.l,//蓝色
                R.mipmap.h,//灰色
                R.mipmap.lv,//绿色
        };

        int index=sp.getInt("toastSytle",0);

        textView.setBackgroundResource(mipmapId[index]);
        mWM.addView(mView, mParams);

    }

    private void query(final String incomingNumber) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                mAdress = AdressDao.getAdress(incomingNumber);
                mHandler.sendEmptyMessage(0);

            }
        }.start();

    }

    class MyListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE://空闲状态
                    System.out.println("空闲状态.............");
                    //移除Toast
                    if (mWM != null && mView != null) {

                        mWM.removeView(mView);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://摘机
                    System.out.println("摘机........");
                    break;
                case TelephonyManager.CALL_STATE_RINGING://响铃
                    System.out.println("响铃........");
                    //弹出Toast
                    showToast(incomingNumber);
                    break;
            }
        }
    }
}
