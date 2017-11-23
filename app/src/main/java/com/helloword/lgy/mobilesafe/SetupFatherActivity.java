package com.helloword.lgy.mobilesafe;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

//抽出一个SetupActivity的公共类
public abstract class SetupFatherActivity extends AppCompatActivity {
    private GestureDetector gestureDetector;//定义手势识别器

    protected SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp=getSharedPreferences("config",MODE_PRIVATE);
        //实例化手势识别器
        gestureDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {//按下屏幕
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {//如果是按下的时间超过瞬间，而且在按下的时候没有松开或者是拖动的，

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {//单独的轻击抬起操作
                return false;
            }

            @Override    //拖动事件
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {//长按触摸屏

            }

            @Override      //滑屏
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
               //屏蔽慢滑屏幕（放止早口袋中误滑屏幕）
                if (Math.abs(velocityX)<200){
                    Toast.makeText(SetupFatherActivity.this, "不能慢滑", Toast.LENGTH_SHORT).show();
                    return true;
                }
                //屏蔽斜滑屏幕
                if (Math.abs(e1.getY()-e2.getY())>200){
                    Toast.makeText(SetupFatherActivity.this, "不能斜滑", Toast.LENGTH_SHORT).show();
                    return true;
                }


                //处理滑屏事件
                if ((e1.getX() - e2.getX()) > 200) {
                    System.out.println("向左滑，显示下一页");
                    next();

                }
                if ((e2.getX() - e1.getX()) > 200) {
                    System.out.println("向右滑，显示上一页");
                    pre();
                }
                return true;
            }
        });
    }

    public abstract void next();

    public abstract void pre();

    @Override//使用手势识别器
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);

    }
}
