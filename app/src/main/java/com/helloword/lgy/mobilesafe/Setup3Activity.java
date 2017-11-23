package com.helloword.lgy.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Setup3Activity extends SetupFatherActivity {

    private EditText et_setup3_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        et_setup3_phone= (EditText) findViewById(R.id.et_setup3_phone);

       et_setup3_phone.setText( sp.getString("safephone",""));
    }

    @Override
    public void next() {
        Intent intent = new Intent(this, Setup4Activity.class);
        startActivity(intent);
        finish();
        //设置Activitry切换动画
        overridePendingTransition(R.anim.next_tran_in, R.anim.next_tran_out);
    }

    @Override
    public void pre() {
        Intent intent = new Intent(this, Setup2Activity.class);
        startActivity(intent);
        //设置Activitry切换动画
        overridePendingTransition(R.anim.pre_tran_in, R.anim.pre_tran_out);

    }

    public void selectContact(View view) {
        Intent intent = new Intent(this, SelectContactActivity.class);
        startActivityForResult(intent, 0);
    }
    //


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data==null){
            return;
        }
        String phone=data.getStringExtra("phone");
        et_setup3_phone.setText(phone);
    }

    public void next(View view) {
      SharedPreferences.Editor editor= sp.edit();
        editor.putString("safephone",et_setup3_phone.getText().toString().trim());
        editor.commit();
        Intent intent = new Intent(this, Setup4Activity.class);
        startActivity(intent);
        finish();
        //设置Activitry切换动画
        overridePendingTransition(R.anim.next_tran_in, R.anim.next_tran_out);
    }

    public void pre(View view) {
        Intent intent = new Intent(this, Setup2Activity.class);
        startActivity(intent);
        //设置Activitry切换动画
        overridePendingTransition(R.anim.pre_tran_in, R.anim.pre_tran_out);
    }
}
