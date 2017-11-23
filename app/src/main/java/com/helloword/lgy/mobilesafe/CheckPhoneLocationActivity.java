package com.helloword.lgy.mobilesafe;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.helloword.lgy.mobilesafe.engine.AdressDao;

public class CheckPhoneLocationActivity extends AppCompatActivity {
    private EditText et_checkphonelocation;
    private Button btn_checkphonelocation;
    private TextView tv_checkphonelocation_result;
    private String mAdress;


    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tv_checkphonelocation_result.setText(mAdress);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_phone_location);
        initUI();
    }

    private void initUI() {
        et_checkphonelocation= (EditText) findViewById(R.id.et_checkphonelocation);
        btn_checkphonelocation= (Button) findViewById(R.id.btn_checkphonelocation);
        tv_checkphonelocation_result= (TextView) findViewById(R.id.tv_checkphonelocation_result);

        btn_checkphonelocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //拿到要查询的电话号码
                String phone=et_checkphonelocation.getText().toString().trim();
                //查询。查询是耗时操作，开启子线程去查询
                query(phone);
            }
        });

        et_checkphonelocation.addTextChangedListener(new TextWatcher() {
            //文本改变
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            //文本改变之前
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            //文本改变之后
            @Override
            public void afterTextChanged(Editable s) {
                String phone=et_checkphonelocation.getText().toString().trim();
                query(phone);

            }
        });

    }

    private void query(final String phone) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                mAdress = AdressDao.getAdress(phone);
                //发一条空消息让主线程拿着查询结果更新ui
                mHandler.sendEmptyMessage(0);
            }
        }.start();

    }
}
