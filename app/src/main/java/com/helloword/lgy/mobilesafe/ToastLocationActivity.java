package com.helloword.lgy.mobilesafe;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;


// TODO: 2017/10/25 问题  拖拽完后保存的位置，与弹出吐司时位置不一致。
// TODO: 2017/10/25 下一步  双击居中未完成
public class ToastLocationActivity extends AppCompatActivity {
    private ImageView iv_drag;
    private Button but_top, but_bottom;
    private WindowManager mWM;
    int screenHeight;
    int screenWidth;

    long [] Hits=new long[2];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast_location);


        initUI();


    }

    private void initUI() {
        //拿到手机屏幕宽高
        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
        screenHeight = mWM.getDefaultDisplay().getHeight();
        screenWidth = mWM.getDefaultDisplay().getWidth();
        //sp
        final SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        //找到控件
        iv_drag = (ImageView) findViewById(R.id.iv_drag);
        but_top= (Button) findViewById(R.id.but_top);

        but_bottom= (Button) findViewById(R.id.but_bottom);
        //从sp取出保存的位置，将控件展示到指定位置
        int locationX = sp.getInt("locationX", 0);
        int locationY = sp.getInt("locationY", 0);
        //控件在相对布局中，所以其位置规则由相对布局提供
        //设置宽高
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        //将左上角的坐标参数对应到规则参数上
        layoutParams.leftMargin = locationX;
        layoutParams.topMargin = locationY;

        if (locationY<screenHeight/2){
            but_bottom.setVisibility(View.VISIBLE);
            but_top.setVisibility(View.INVISIBLE);
        }else {
            but_bottom.setVisibility(View.INVISIBLE);
            but_top.setVisibility(View.VISIBLE);
        }
        //把位置规则对应到控件
        iv_drag.setLayoutParams(layoutParams);


        iv_drag.setOnTouchListener(new View.OnTouchListener() {
            int startX;
            int startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_MOVE:
                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();
                        //移动了的距离
                        int disX = moveX - startX;
                        int disY = moveY - startY;
                        //拿到移动了距离的上下左右的坐标
                        int left = iv_drag.getLeft() + disX;
                        int top = iv_drag.getTop() + disY;
                        int right = iv_drag.getRight() + disX;
                        int bottom = iv_drag.getBottom() + disY;
                        //容错处理（拖拽吐司不能越出手机屏幕）
                        if (left < 0 || top < 0 || right > screenWidth || bottom > screenHeight -40) {
                            return true;
                        }

                        //当iv_drag拖拽到屏幕一半的时候两个button进行可见不可见转换
                        if (top<screenHeight/2){
                            but_bottom.setVisibility(View.VISIBLE);
                            but_top.setVisibility(View.INVISIBLE);
                        }else {
                            but_bottom.setVisibility(View.INVISIBLE);
                            but_top.setVisibility(View.VISIBLE);
                        }

                        //告知控件要移动的位置
                        iv_drag.layout(left, top, right, bottom);
                        //重置坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();


                        break;
                    case MotionEvent.ACTION_UP:
                        //保存当前坐标，以便下次进来时显示

                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("locationX", iv_drag.getLeft());
                        editor.putInt("locationY", iv_drag.getTop());
                        editor.commit();

                        break;

                }


                iv_drag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //arraycopy(原数组，原数组开始复制的位置，目标数组，目标数组放置复制过来元素的起始位置，复制长度)
                        System.arraycopy(Hits,1,Hits,0,Hits.length-1);
                        Hits[Hits.length-1]= SystemClock.uptimeMillis();
                        if (Hits[Hits.length-1]-Hits[0]<500){
                            int left=screenWidth/2-iv_drag.getWidth()/2;
                            int top=screenHeight/2-iv_drag.getHeight()/2;
                            int right=screenWidth/2+iv_drag.getWidth()/2;
                            int bottom=screenHeight/2+iv_drag.getHeight()/2;
                            iv_drag.layout(left,top,right,bottom);
                        }
                    }
                });


                //返回flase事件不响应，返回true才会响应
                return false;
            }
        });
    }
}
