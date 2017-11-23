package com.helloword.lgy.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LostFindActivity extends AppCompatActivity {
    private SharedPreferences sp;
    private TextView tv_fasenumber;
    private ImageView iv_protecting;

    // TODO: 2017/10/10 播放报警音乐已完成 获取GPS代码完成，调试未完成，调试有难度
    // TODO: 2017/10/10  远程删数据+锁屏   代码部分已完成
    // TODO: 2017/10/10 调试问题是：sim卡监听出现问题，无法调试，需要在真机上调试

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp=getSharedPreferences("config",MODE_PRIVATE);
        boolean configed=sp.getBoolean("configed",false);
        if (configed){
            setContentView(R.layout.activity_lost_find);
            //
           String safenumber= sp.getString("safephone","");
            boolean protecting=sp.getBoolean("protecting",false);

            tv_fasenumber= (TextView) findViewById(R.id.tv_fasenumber);
            iv_protecting= (ImageView) findViewById(R.id.iv_protecting);

            tv_fasenumber.setText(safenumber);
            if (protecting){
                iv_protecting.setImageResource(R.mipmap.lock);
            }else {
                iv_protecting.setImageResource(R.mipmap.unlock);
            }

        }else {
            Intent intent=new Intent(LostFindActivity.this,Setup1Activity.class);
            startActivity(intent);

        }

    }
    public void reEntersetup(View view){
        Intent intent=new Intent(LostFindActivity.this,Setup1Activity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(this,HomeActivity.class);
        startActivity(intent);
    }
}
