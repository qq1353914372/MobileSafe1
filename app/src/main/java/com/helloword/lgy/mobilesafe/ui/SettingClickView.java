package com.helloword.lgy.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.helloword.lgy.mobilesafe.R;

/**
 * Created by hasee on 2017/10/3.
 */

public class SettingClickView extends RelativeLayout {
    private TextView tv_desc;
    private TextView tv_title;

    private void initview(Context context) {
        View.inflate(context, R.layout.click_setting_view,this);
        tv_desc= (TextView) this.findViewById(R.id.tv_desc);
        tv_title= (TextView) this.findViewById(R.id.tv_title);

    }
    public SettingClickView(Context context) {
        super(context);
        initview(context);
    }

    public SettingClickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initview(context);
    }

    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initview(context);
    }


    public void setDesc(String text){//设置描述信息
        tv_desc.setText(text);
    }
    public void setTitle(String text){//设置描述信息
        tv_title.setText(text);
    }


}
