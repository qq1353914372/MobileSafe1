package com.helloword.lgy.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.helloword.lgy.mobilesafe.ui.SettingItemView;

public class Setup2Activity extends SetupFatherActivity {
    private SettingItemView bindsim;
    private TelephonyManager tm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
        bindsim = (SettingItemView) findViewById(R.id.siv_bindsim);
        bindsim.setTitle("点击绑定sim卡");
        String sim=sp.getString("sim",null);
        if (TextUtils.isEmpty(sim)){
           unbindsim();
        }else {
            bindsim();
        }



        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        bindsim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                //判断一下当前的状态是否勾选
                if (bindsim.isChecked()) {
                    editor.putString("sim", null);
                    unbindsim();
                } else {
                    //拿到sim卡序列号
                    String sim = tm.getSimSerialNumber();
                    editor.putString("sim", sim);
                        bindsim();

                }
                editor.commit();

            }
        });
    }

    public  void bindsim(){
        bindsim.setChecked(true);
        bindsim.setDesc("sim卡已经绑定");
    }
    public void unbindsim(){
        bindsim.setChecked(false);
        bindsim.setDesc("sim卡没有绑定");
    }
    @Override
    public void next() {
      String sim=  sp.getString("sim",null);
        if(TextUtils.isEmpty(sim)){
            Toast.makeText(this, "没有绑定sim卡", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(this, Setup3Activity.class);
        startActivity(intent);
        finish();
        //设置Activitry切换动画
        overridePendingTransition(R.anim.next_tran_in, R.anim.next_tran_out);
    }

    @Override
    public void pre() {
        Intent intent = new Intent(this, Setup1Activity.class);
        startActivity(intent);
        //设置Activitry切换动画
        overridePendingTransition(R.anim.pre_tran_in, R.anim.pre_tran_out);

    }

    public void next(View view) {
        Intent intent = new Intent(this, Setup3Activity.class);
        startActivity(intent);
        finish();
        //设置Activitry切换动画
        overridePendingTransition(R.anim.next_tran_in, R.anim.next_tran_out);
    }

    public void pre(View view) {
        Intent intent = new Intent(this, Setup1Activity.class);
        startActivity(intent);
        //设置Activitry切换动画
        overridePendingTransition(R.anim.pre_tran_in, R.anim.pre_tran_out);
    }
}
