package com.helloword.lgy.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.helloword.lgy.mobilesafe.R;

/**
 * Created by hasee on 2017/10/3.
 */

public class SettingItemView extends RelativeLayout {
    private CheckBox cb_setting;
    private TextView tv_desc;
    private TextView tv_title;

    private void initview(Context context) {
        View.inflate(context, R.layout.item_setting_view,this);
        cb_setting= (CheckBox) this.findViewById(R.id.cb_setting);
        tv_desc= (TextView) this.findViewById(R.id.tv_desc);
        tv_title= (TextView) this.findViewById(R.id.tv_title);

    }
    public SettingItemView(Context context) {
        super(context);
        initview(context);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initview(context);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initview(context);
    }
    public boolean isChecked(){//检查组合控件的状态是否被选中
        return cb_setting.isChecked();
    }
    public void setChecked(boolean checked){//设置组合控件的状态
        cb_setting.setChecked(checked);
    }
    public void setDesc(String text){//设置描述信息
        tv_desc.setText(text);
    }
    public void setTitle(String text){//设置描述信息
        tv_title.setText(text);
    }


}
