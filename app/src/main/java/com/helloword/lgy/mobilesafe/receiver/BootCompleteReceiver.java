package com.helloword.lgy.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;
//定义广播接收者  监听sim卡变更
public class BootCompleteReceiver extends BroadcastReceiver {
    private SharedPreferences sp;
    private TelephonyManager tm;
    public BootCompleteReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "开机了", Toast.LENGTH_SHORT).show();
       //拿到之前保存的sim卡序列号
        sp=context.getSharedPreferences("config",Context.MODE_PRIVATE);

       boolean protecting= sp.getBoolean("protecting",false);
        if (protecting){
            String savesim= sp.getString("sim","")+"oo";
            //拿到当前的sim卡序列号
            tm= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String nowsim= tm.getSimSerialNumber();
            if (savesim!=null){
                if (savesim.equals(nowsim)){//sim卡没变更

                }else {//sim卡变更
                    Toast.makeText(context, "sim卡变更变更后号码是："+nowsim, Toast.LENGTH_SHORT).show();
                    SmsManager.getDefault().sendTextMessage(sp.getString("safephone",""),null,"sim changed..",null,null);

                }
        }



        }
    }
}
