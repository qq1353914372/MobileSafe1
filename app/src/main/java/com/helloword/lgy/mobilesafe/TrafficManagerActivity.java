package com.helloword.lgy.mobilesafe;

import android.content.SharedPreferences;
import android.net.TrafficStats;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.widget.TextView;

import java.util.Calendar;

public class TrafficManagerActivity extends AppCompatActivity {
    private TextView tv_todaytraffic, tv_thismonthtraffic, tv_traffic;
    private SharedPreferences sp;
    private Calendar c;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_manager);
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        long r = TrafficStats.getMobileRxBytes();
        long s = TrafficStats.getMobileTxBytes();
        long startT = r + s;
        //拿到sp，如果之前没有保存开始统计流量的位置，则现在保存
        sp = getSharedPreferences("config", MODE_PRIVATE);
        long todaystart = sp.getLong("todaystart", startT);
        long monthstart = sp.getLong("monthstart", startT);
        //今日流量
        if (todaystart==startT){
          SharedPreferences.Editor editor= sp.edit();
            editor.putLong("todaystart",startT);
            editor.commit();
        }
        //现在的流量
        long end_traffic=r+s;
        //算出今日流量
        long today_traffic=end_traffic-todaystart;
        //转格式
        String strtoday= Formatter.formatFileSize(this,today_traffic);
        tv_todaytraffic.setText("今日已用："+strtoday);

//        Calendar c = Calendar.getInstance();
//        int year = c.get(Calendar.YEAR);
//        int month = c.get(Calendar.MONTH);
//        int day = c.get(Calendar.DAY_OF_MONTH);
//        tv_thismonthtraffic.setText(year+"年"+month+"月"+day+"日");


        Calendar c1 = Calendar.getInstance();
        int  hour = c1.get(Calendar.HOUR_OF_DAY);
        int minute = c1.get(Calendar.MINUTE);
        tv_traffic.setText(hour+"时"+minute+"分");
    }

    private void initUI() {
        tv_todaytraffic = (TextView) findViewById(R.id.tv_todaytraffic);
        tv_thismonthtraffic = (TextView) findViewById(R.id.tv_thismonthtraffic);
        tv_traffic = (TextView) findViewById(R.id.tv_traffic);
    }
}
