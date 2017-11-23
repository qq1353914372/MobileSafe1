package com.helloword.lgy.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.helloword.lgy.mobilesafe.com.helloword.lgy.mobilesafe.utils.ServiceUtils;
import com.helloword.lgy.mobilesafe.service.LockScreenClearService;

public class ProcessSettingActivity extends AppCompatActivity {
    CheckBox cb_showsystem,cb_lockclear;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_setting);
        sp=getSharedPreferences("config",MODE_PRIVATE);
        initShowSystem();
        initLockScreenClear();
    }

    private void initLockScreenClear() {
        cb_lockclear= (CheckBox) findViewById(R.id.cb_lockclear);
        //将单选框的状态与服务是否在运行绑定起来
       boolean isRunning= ServiceUtils.isRunning(this,"com.helloword.lgy.mobilesafe.service.LockScreenClearService");
       if (isRunning){
           cb_lockclear.setText("锁屏清理已开启");
       }else {
           cb_lockclear.setText("锁屏清理已关闭");
       }
        cb_lockclear.setChecked(isRunning);
        cb_lockclear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cb_lockclear.setText("锁屏清理已开启");
                    //开启服务
                    Intent intent=new Intent(getApplicationContext(), LockScreenClearService.class);
                    startService(intent);
                }else {
                    cb_lockclear.setText("锁屏清理已关闭");
                    //关闭服务
                    Intent intent=new Intent(getApplicationContext(), LockScreenClearService.class);
                    stopService(intent);
                }
            }
        });
    }

    private void initShowSystem() {
        cb_showsystem= (CheckBox) findViewById(R.id.cb_showsystem);
        boolean showsystem=sp.getBoolean("showsystem",false);
        cb_showsystem.setChecked(showsystem);
        if (showsystem){
            cb_showsystem.setText("显示系统进程");
        }else {
            cb_showsystem.setText("隐藏系统进程");
        }
        cb_showsystem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cb_showsystem.setText("显示系统进程");
                }else {
                    cb_showsystem.setText("隐藏系统进程");
                }
              SharedPreferences.Editor editor= sp.edit();
                editor.putBoolean("showsystem",isChecked);
                editor.commit();
            }
        });
    }
}
