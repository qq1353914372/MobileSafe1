package com.helloword.lgy.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {
    private GridView gv_home;
    private MyAdater myAdater;

    private SharedPreferences sp;

    // TODO: 2017/11/15 在每个子页面添加返回箭头
    private static String[] names = {
            "手机防盗", "通讯卫士", "软件管理",
            "进程管理", "电量管理", "手机杀毒",
            "缓存清理", "高级工具", "设置中心"};
    private static int[] ids = {//图片
            R.mipmap.fangdao, R.mipmap.tongxunweis, R.mipmap.guanl,
            R.mipmap.jinc, R.mipmap.battery, R.mipmap.shadu,
            R.mipmap.huancun, R.mipmap.gaoji, R.mipmap.shezhe
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        sp = getSharedPreferences("config", MODE_PRIVATE);
        //设置GridView
        gv_home = (GridView) findViewById(R.id.gv_home);
        myAdater = new MyAdater();
        gv_home.setAdapter(myAdater);

        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
                View mView = View.inflate(HomeActivity.this, R.layout.toast_view, null);
                WindowManager mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
                switch (position) {
                    case 0://手机防盗
                        showAlterDialog();
                        break;
                    case 1://黑名单
                        startActivity(new Intent(HomeActivity.this, BlackNumberMainActivity.class));
                        break;
                    case 2://软件管理
                        Intent intent1 = new Intent(HomeActivity.this, AppManagerActivity.class);
                        startActivity(intent1);
                        break;
                    case 3:
                        Intent intent3 = new Intent(HomeActivity.this, ProcessActivity.class);
                        startActivity(intent3);

                        break;
                    case 4:
                        Intent powerUsageIntent = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
                        ResolveInfo resolveInfo = getPackageManager().resolveActivity(powerUsageIntent, 0);
                      // check that the Battery app exists on this device
                        if (resolveInfo != null) {
                            startActivity(powerUsageIntent);
                        }
                        break;
                    case 5:
                        Intent intent5 = new Intent(HomeActivity.this, KillVirusActivity.class);
                        startActivity(intent5);

                        break;
                    case 6:
                        Intent intent6 = new Intent(HomeActivity.this, CacheClearActivity.class);
                        startActivity(intent6);

                        break;

                    case 7:
                        Intent intent7 = new Intent(HomeActivity.this, AToolsActivity.class);
                        startActivity(intent7);
                        break;
                    case 8://点击设置中心
                        Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, TrafficService.class);
        startService(intent);

    }

    private EditText et_setpwd;
    private EditText et_set_enterpwd;
    private EditText et_enterpwd;
    private Button btn_ok;
    private Button btn_cancal;
    private AlertDialog alertDialog;

    private void showAlterDialog() {//点击手机防盗条目弹出对话框
        //判断是否已经设置密码 sp

        if (isSetupPwd()) {//设置了密码，弹出输入密码对话框
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            View view = View.inflate(HomeActivity.this, R.layout.dialog_enterpassword, null);
            //取消
            btn_cancal = (Button) view.findViewById(R.id.btn_cancal);
            btn_cancal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            //确定
            et_enterpwd = (EditText) view.findViewById(R.id.et_enterpwd);
            btn_ok = (Button) view.findViewById(R.id.btn_ok);
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //判断密码
                    String savepwd = sp.getString("pwd", null);
                    String pwd = et_enterpwd.getText().toString().trim();
                    if (savepwd.equals(pwd)) {//进入手机防盗页面
                        Toast.makeText(HomeActivity.this, "进入手机防盗页面", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                        Intent intent = new Intent(HomeActivity.this, LostFindActivity.class);
                        startActivity(intent);

                    } else {//密码错误
                        Toast.makeText(HomeActivity.this, "密码错误！", Toast.LENGTH_SHORT).show();
                        et_enterpwd.setText("");
                        return;
                    }

                }
            });

            builder.setView(view);
            alertDialog = builder.show();


        } else {//没有设置密码，弹出设置密码对话框
            final AlertDialog.Builder setPwdDialog = new AlertDialog.Builder(HomeActivity.this);
            View view = View.inflate(HomeActivity.this, R.layout.dialog_setpassword, null);
            et_setpwd = (EditText) view.findViewById(R.id.et_setpwd);
            et_set_enterpwd = (EditText) view.findViewById(R.id.et_set_enterpwd);
            btn_ok = (Button) view.findViewById(R.id.btn_ok);
            btn_cancal = (Button) view.findViewById(R.id.btn_cancal);
            //取消按钮的地点击事件
            btn_cancal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //关闭对话框
                    alertDialog.dismiss();
                }
            });
            //确定按钮的点击事件
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //拿到输入框的密码 保存
                    //关闭对话框 进入手机防盗页面
                    String setpwd = et_setpwd.getText().toString().trim();
                    String setpwdEnter = et_set_enterpwd.getText().toString().trim();
                    if (TextUtils.isEmpty(setpwd) || TextUtils.isEmpty(setpwdEnter)) {
                        Toast.makeText(HomeActivity.this, "密码不能为空！", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("pwd", setpwd);

                        editor.commit();
                        String p = sp.getString("pwd", null);
                        Toast.makeText(HomeActivity.this, "保存成功==" + p, Toast.LENGTH_LONG).show();
                        //关闭对话框 进入手机防盗页面

                        alertDialog.dismiss();
                        Intent intent = new Intent(HomeActivity.this, LostFindActivity.class);
                        startActivity(intent);
                        System.out.println("进入手机防盗页面");
                    }

                }
            });
            setPwdDialog.setView(view);
            alertDialog = setPwdDialog.show();
        }


    }

    public boolean isSetupPwd() {//判断是否设置了密码
        String pwd = sp.getString("pwd", null);
        if (TextUtils.isEmpty(pwd)) {
            return false;
        } else {
            return true;
        }


    }

    private class MyAdater extends BaseAdapter {//数据匹配器  用于设置GridView的数据

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(HomeActivity.this, R.layout.item_home, null);
            ImageView iv_item = (ImageView) view.findViewById(R.id.iv_item);
            TextView tv_item = (TextView) view.findViewById(R.id.tv_item);
            iv_item.setImageResource(ids[position]);
            tv_item.setText(names[position]);

            return view;
        }
    }
}
