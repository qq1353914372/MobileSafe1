package com.helloword.lgy.mobilesafe;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {
    private TextView tv_sqlash_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        tv_sqlash_version= (TextView) findViewById(R.id.tv_sqlash_version);
        tv_sqlash_version.setText("版本号："+getVersion());

    }
    private String getVersion(){
        PackageManager pm=getPackageManager();
        try {
          PackageInfo info= pm.getPackageInfo(getPackageName(),0);
          return   info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return "";
    }
}
