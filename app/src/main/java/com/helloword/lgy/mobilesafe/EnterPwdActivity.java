package com.helloword.lgy.mobilesafe;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EnterPwdActivity extends AppCompatActivity {
    private TextView tv_packagename;
    private ImageView iv_icon;
    private EditText et_pwd;
    private Button btn_enter;
    private String mPackagename;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pwd);
        initUI();
        initDada();
    }

    private void initDada() {
        mPackagename=getIntent().getStringExtra("taskPackagename");
        PackageManager pm=getPackageManager();
        try {
            ApplicationInfo applicationInfo=pm.getApplicationInfo(mPackagename,0);
           Drawable icon= applicationInfo.loadIcon(pm);
            iv_icon.setImageDrawable(icon);
            tv_packagename.setText(applicationInfo.loadLabel(pm).toString());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd=et_pwd.getText().toString().trim();
                if (TextUtils.isEmpty(pwd)){
                    Toast.makeText(EnterPwdActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                }else {
                    if (pwd.equals("123")){
                        //密码正确
                        Intent intent=new Intent("android.intent.action.SKIP");
                        intent.putExtra("skippackagename",mPackagename);
                        sendBroadcast(intent);
                        finish();
                    }else {
                        Toast.makeText(EnterPwdActivity.this, "密码错误！", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });

    }
    private void initUI() {
        tv_packagename= (TextView) findViewById(R.id.tv_packagename);
        iv_icon= (ImageView) findViewById(R.id.iv_icon);
        et_pwd= (EditText) findViewById(R.id.et_pwd);
        btn_enter= (Button) findViewById(R.id.btn_enter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //回退按钮，打开桌面app的activity
        Intent intent=new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}
