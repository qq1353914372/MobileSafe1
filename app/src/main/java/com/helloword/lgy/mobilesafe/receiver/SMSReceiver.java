package com.helloword.lgy.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import com.helloword.lgy.mobilesafe.R;
import com.helloword.lgy.mobilesafe.service.GPSService;

import static android.content.Context.DEVICE_POLICY_SERVICE;

public class SMSReceiver extends BroadcastReceiver {

    // TODO: 2017/10/9 短信远程控制手机已完成 未调试  未调试完成 做下一个
    private SharedPreferences sp;

    public SMSReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        Object[] objs = (Object[]) intent.getExtras().get("pdus");
        for (Object b : objs) {
            //拿到发信人的号码以及短信内容
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) b);
            String sender = sms.getOriginatingAddress();
            String body = sms.getMessageBody();
            // TODO: 2017/10/10 逻辑有问题  从sp拿到的不是手机号码是sim卡序列号
            //拿到安全号码 判断发信人是否是安全号码
            String safephone = sp.getString("safephone", "");
            if (sender.equals(safephone)) {
                switch (body) {
                    case "#*location*#":
                        System.out.println("GPS追踪");
                        Intent i = new Intent(context, GPSService.class);
                        context.startService(i);
                        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
                        String lastlocation = sp.getString("lastlocation", null);
                        if (TextUtils.isEmpty(lastlocation)) {
                            SmsManager.getDefault().sendTextMessage(sender, null, "getting location", null, null);
                        } else {
                            SmsManager.getDefault().sendTextMessage(sender, null, lastlocation, null, null);
                        }
                        break;
                    case "#*alarm*#":
                        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.a);
                        mediaPlayer.start();
                        System.out.println("播放报警音乐");
                        break;
                    case "#*wipeddata*#":
                        System.out.println("远程删除数据");
                        //获取设备策略服务
                        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(DEVICE_POLICY_SERVICE);
                        //获取管理员权限
                        Intent openadminIntent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                        ComponentName componentName = new ComponentName(context, MyAdmin.class);
                        openadminIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
                        openadminIntent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "按钮不会失灵");
                        // TODO: 2017/10/10 这一步有问题 逻辑问题
                        context.startActivity(openadminIntent);
                        //锁屏
                        dpm.wipeData(0); //恢复出厂设置
                        break;
                    case "#*lockscreen*#":
                        System.out.println("远程锁屏");
                        //获取设备策略服务
                        DevicePolicyManager dpm1 = (DevicePolicyManager) context.getSystemService(DEVICE_POLICY_SERVICE);
                        //获取管理员权限
                        Intent openadminIntent1 = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                        ComponentName componentName1 = new ComponentName(context, MyAdmin.class);
                        openadminIntent1.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName1);
                        openadminIntent1.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "按钮不会失灵");
                        // TODO: 2017/10/10 这一步有问题 逻辑问题
                        context.startActivity(openadminIntent1);
                        //锁屏
                        dpm1.lockNow();
                        break;
                }
            }

        }
    }
}
