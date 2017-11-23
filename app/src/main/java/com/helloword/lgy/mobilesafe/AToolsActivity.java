package com.helloword.lgy.mobilesafe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.helloword.lgy.mobilesafe.engine.SmsBackUp;

import java.io.File;

public class AToolsActivity extends AppCompatActivity {
    private TextView tv_checklocation,tv_smsbackup,tv_commonnumber,tv_applock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
       initCheckLocation();
        initSmsBackUp();
        initCommonNunber();
        initAppLock();
    }

    private void initAppLock() {
        tv_applock= (TextView) findViewById(R.id.tv_applock);
        tv_applock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AToolsActivity.this,AppLockActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initCommonNunber() {
        tv_commonnumber= (TextView) findViewById(R.id.tv_commonnumber);
        tv_commonnumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        Intent intent=new Intent(getApplicationContext(),CommonNumberActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initSmsBackUp() {
        tv_smsbackup= (TextView) findViewById(R.id.tv_smsbackup);
        tv_smsbackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSmsBackUpDialog();
            }
        });
    }

    private void showSmsBackUpDialog() {
       final ProgressDialog dialog=new ProgressDialog(this);
        dialog.setTitle("备份短信");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.show();
        //短信备份：直接调用engine中的短信备份方法
        //中含有数据库查询操作，耗时，需开启子线程
        new Thread(){
            @Override
            public void run() {
                super.run();
                String path= Environment.getExternalStorageDirectory().getAbsolutePath()+
                        File.separator+"smsbackup.xml";
                SmsBackUp.backUp(getApplicationContext(), path, new SmsBackUp.CallBack() {
                    @Override
                    public void setMax(int max) {
                        dialog.setMax(max);
                    }

                    @Override
                    public void setProgress(int index) {
                        dialog.setProgress(index);

                    }
                });
                //关闭对话框
                dialog.dismiss();

            }
        }.start();

    }

    private void initCheckLocation() {
        tv_checklocation= (TextView) findViewById(R.id.tv_checklocation);
        tv_checklocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AToolsActivity.this,CheckPhoneLocationActivity.class);
                startActivity(intent);

            }
        });
    }
}
