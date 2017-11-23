package com.helloword.lgy.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class Setup4Activity extends SetupFatherActivity {
    private SharedPreferences sp;
    private CheckBox cb_protecting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
        sp=getSharedPreferences("config",MODE_PRIVATE);
        cb_protecting= (CheckBox) findViewById(R.id.cb_protecting);

       boolean protecting= sp.getBoolean("protecting",false);
        if (protecting){
            cb_protecting.setText("防盗保护已经开启");
            cb_protecting.setChecked(true);
        }else {
            cb_protecting.setText("防盗保护没有开启");
            cb_protecting.setChecked(false);

        }
        cb_protecting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cb_protecting.setText("防盗保护已经开启");
                }else {
                    cb_protecting.setText("防盗保护没有开启");
                }
               SharedPreferences.Editor editor= sp.edit();
                editor.putBoolean("protecting",isChecked);
                editor.commit();
            }
        });
    }

    @Override
    public void next() {
        SharedPreferences.Editor editor=sp.edit();
        editor.putBoolean("configed",true);
        editor.commit();
        Intent intent=new Intent(this,LostFindActivity.class);
        startActivity(intent);
        finish();
        //设置Activitry切换动画
        overridePendingTransition(R.anim.next_tran_in,R.anim.next_tran_out);
    }

    @Override
    public void pre() {
        Intent intent=new Intent(this,Setup3Activity.class);
        startActivity(intent);
        //设置Activitry切换动画
        overridePendingTransition(R.anim.pre_tran_in,R.anim.pre_tran_out);
    }

    public void next(View view){
        SharedPreferences.Editor editor=sp.edit();
        editor.putBoolean("configed",true);
        editor.commit();
        Intent intent=new Intent(this,LostFindActivity.class);
        startActivity(intent);
        finish();
        //设置Activitry切换动画
        overridePendingTransition(R.anim.next_tran_in,R.anim.next_tran_out);

    }
    public void pre(View view){
        Intent intent=new Intent(this,Setup3Activity.class);
        startActivity(intent);
        //设置Activitry切换动画
        overridePendingTransition(R.anim.pre_tran_in,R.anim.pre_tran_out);
    }

//    //按下返回键
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent=new Intent(this,HomeActivity.class);
//        startActivity(intent);
//        finish();
//    }
}
