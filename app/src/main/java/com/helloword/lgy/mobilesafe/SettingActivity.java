package com.helloword.lgy.mobilesafe;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import com.helloword.lgy.mobilesafe.com.helloword.lgy.mobilesafe.utils.ServiceUtils;
import com.helloword.lgy.mobilesafe.service.BlackNumberService;
import com.helloword.lgy.mobilesafe.service.PhoneStateListenerService;
import com.helloword.lgy.mobilesafe.service.WacchDogService;
import com.helloword.lgy.mobilesafe.ui.SettingClickView;
import com.helloword.lgy.mobilesafe.ui.SettingItemView;

public class SettingActivity extends AppCompatActivity {
    private SettingItemView siv_update;
    private SettingItemView siv_checkadress;
    private SharedPreferences sp;
    private SettingClickView scv_toaststyle;
    private SettingClickView scv_toastlocation;
    private SettingItemView siv_blacknumber;
    private SettingItemView siv_applock;

    String[] toastSytle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sp = getSharedPreferences("config", MODE_PRIVATE);

        scv_toaststyle= (SettingClickView) findViewById(R.id.scv_toaststyle);
        initSiv_update();
        initSiv_checkadress();
        initChooseToast_Style();
        initToastLocation();
        initBlackNumber();
        initAppLock();




    }

    private void initAppLock() {
        siv_applock= (SettingItemView) findViewById(R.id.siv_applock);
        siv_applock.setTitle("程序锁设置");
        siv_applock.setDesc("程序锁已关闭");
        boolean isRunning=ServiceUtils.isRunning(this,
                "com.helloword.lgy.mobilesafe.service.WacchDogService");
        siv_applock.setChecked(isRunning);
        siv_applock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck=siv_applock.isChecked();
                Intent intent=new Intent(getApplicationContext(), WacchDogService.class);
                if (!isCheck){
                    siv_applock.setChecked(true);
                    siv_applock.setDesc("程序锁已开启");
                    startService(intent);
                }else {
                    siv_applock.setChecked(false);
                    siv_applock.setDesc("程序锁已关闭");
                    stopService(intent);
                }
            }
        });
    }

    private void initBlackNumber() {
        siv_blacknumber= (SettingItemView) findViewById(R.id.siv_blacknumber);
        siv_blacknumber.setTitle("黑名单拦截设置");
        siv_blacknumber.setDesc("黑名单拦截已关闭");
        boolean isRunning=ServiceUtils.isRunning(this,
                "com.helloword.lgy.mobilesafe.service.BlackNumberService");
        siv_blacknumber.setChecked(isRunning);
        siv_blacknumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            boolean isCheck=siv_blacknumber.isChecked();
                Intent intent=new Intent(getApplicationContext(), BlackNumberService.class);
                if (!isCheck){
                    siv_blacknumber.setChecked(true);
                    siv_blacknumber.setDesc("黑名单拦截已开启");
                    startService(intent);
                }else {
                    siv_blacknumber.setChecked(false);
                    siv_blacknumber.setDesc("黑名单拦截已关闭");
                stopService(intent);
                }
            }
        });

    }
    private void initSiv_checkadress() {//开启来电显示归属地Toast

        siv_checkadress = (SettingItemView) findViewById(R.id.siv_checkadress);
        siv_checkadress.setTitle("来电显示归属地功能");
        boolean isRunning = ServiceUtils.isRunning(this,
                "com.helloword.lgy.mobilesafe.service.PhoneStateListenerService");
        siv_checkadress.setChecked(isRunning);
        siv_checkadress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean ischeck = siv_checkadress.isChecked();
                SharedPreferences.Editor editor = sp.edit();
                if (!ischeck) {//开启服务。监听电话状态
                    siv_checkadress.setChecked(true);
                    siv_checkadress.setDesc("来电显示归属地功能已开启");
                    editor.putBoolean("showlocation", true);
                    editor.commit();
                    Intent intent = new Intent(SettingActivity.this, PhoneStateListenerService.class);
                    startService(intent);

                } else {//关闭服务
                    siv_checkadress.setChecked(false);
                    siv_checkadress.setDesc("来电显示归属地功能已关闭");
                    editor.putBoolean("showlocation", false);
                    editor.commit();
                    Intent intent = new Intent(SettingActivity.this, PhoneStateListenerService.class);
                    stopService(intent);
                }

            }
        });

    }

    private void initToastLocation() {
        scv_toastlocation= (SettingClickView) findViewById(R.id.scv_toastlocation);
        scv_toastlocation.setTitle("归属地提示框位置");
        scv_toastlocation.setDesc("设置归属地提示框位置");
        scv_toastlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SettingActivity.this,ToastLocationActivity.class);
                startActivity(intent);
            }
        });

    }

    public void initChooseToast_Style() {

        scv_toaststyle.setTitle("设置归属地风格");

        toastSytle=new String[]{"透明","橙色","蓝色","灰色","绿色"};
       int index= sp.getInt("toastSytle",0);
        scv_toaststyle.setDesc(toastSytle[index]);

        scv_toaststyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToastStyleDialog();
            }
        });


    }

    private void showToastStyleDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("请选择归属地样式");
        builder.setSingleChoiceItems(toastSytle, sp.getInt("toastSytle",0), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //记录选择的条目的id，关闭对话框，显示选择的颜色
               SharedPreferences.Editor editor= sp.edit();
                editor.putInt("toastSytle",which);
                editor.commit();
                dialog.dismiss();
                scv_toaststyle.setDesc(toastSytle[which]);

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }



    private void initSiv_update() {//自动更新

        siv_update = (SettingItemView) findViewById(R.id.siv_update);

        boolean update = sp.getBoolean("update", false);
        if (update) {
            siv_update.setChecked(true);
            siv_update.setDesc("自动更新已经开启");
        } else {
            siv_update.setChecked(false);
            siv_update.setDesc("自动更新已经关闭");
        }

        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SharedPreferences.Editor editor = sp.edit();

                if (siv_update.isChecked()) {//本来开启升级，点击，下面改变状态
                    siv_update.setChecked(false);
                    siv_update.setDesc("自动更新已经关闭");
                    editor.putBoolean("update", false);

                } else {////本来关闭升级，点击，下面改变状态
                    siv_update.setChecked(true);
                    siv_update.setDesc("自动更新已经开启");
                    editor.putBoolean("update", true);
                }

                editor.commit();
            }
        });
    }
}
