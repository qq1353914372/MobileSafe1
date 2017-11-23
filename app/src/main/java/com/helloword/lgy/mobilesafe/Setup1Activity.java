package com.helloword.lgy.mobilesafe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Setup1Activity extends SetupFatherActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);

    }

    @Override
    public void next() {
        Intent intent=new Intent(this,Setup2Activity.class);

        finish();
        startActivity(intent);
        //设置Activitry切换动画
        //overridePendingTransition方法必须在finish或者startavtivity方法后面
        //第一个参数是进来动画，第二个参数是出去动画
        overridePendingTransition(R.anim.next_tran_in,R.anim.next_tran_out);
}

    @Override
    public void pre() {

    }


    public void next(View view){//下一步的点击事件
        Intent intent=new Intent(this,Setup2Activity.class);

        finish();
        startActivity(intent);
        //设置Activitry切换动画
        //overridePendingTransition方法必须在finish或者startavtivity方法后面
        //第一个参数是进来动画，第二个参数是出去动画
        overridePendingTransition(R.anim.next_tran_in,R.anim.next_tran_out);
    }
}
