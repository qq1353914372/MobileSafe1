package com.helloword.lgy.mobilesafe;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.helloword.lgy.mobilesafe.com.helloword.lgy.mobilesafe.utils.SteamToos;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends AppCompatActivity {
    //handerwhat常量
    public static final int JSONERROR = 4;
    public static final int IOERROR = 3;
    public static final int URLERROR = 2;
    public static final int SHOWDIALOG = 1;
    public static final int ENTERHOME = 0;

    //json内容
    private String desc;
    private String apkurl;

    private TextView tv_sqlash_version;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ENTERHOME://进入主页面
                    enter();
                    break;
                case SHOWDIALOG://弹出升级对话框
                    showUpdateDialog();


                    System.out.println("需要升级啦");
                    break;
                case URLERROR://进入主页面
                    enter();
                    Toast.makeText(getApplicationContext(), "URLERROR", Toast.LENGTH_LONG).show();
                    break;
                case IOERROR://进入主页面
                    enter();
                    Toast.makeText(getApplicationContext(), "IOERROR", Toast.LENGTH_LONG).show();
                    break;
                case JSONERROR://进入主页面
                    enter();
                    Toast.makeText(getApplicationContext(), "JSONERROR", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示升级");
        builder.setMessage(desc);
        builder.setPositiveButton("立即升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO: 2017/10/1 下载安装新版本apk

            }
        });
        builder.setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();//关闭对话框
                enter();//进入主页面


            }
        });
        builder.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        tv_sqlash_version = (TextView) findViewById(R.id.tv_sqlash_version);
        tv_sqlash_version.setText("版本号：" + getVersion());

        //联网检查版本
        checkUpdate();
        Toast.makeText(SplashActivity.this, "联网成功", Toast.LENGTH_SHORT).show();
        //设置朦胧动画效果
        AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
        aa.setDuration(500);
        findViewById(R.id.activity_splash).setAnimation(aa);
    }

    private void enter() {//进入主页面

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        //关闭当前页面
        finish();

    }

    private void checkUpdate() {//联网检查版本
        new Thread() {
            @Override
            public void run() {
                super.run();
                Message msg = Message.obtain();
                long startTime = System.currentTimeMillis();
                try {

                    URL url = new URL("http://110.72.235.203:8080/updateinfo.html");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setReadTimeout(4000);
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        InputStream is = conn.getInputStream();
                        String result = SteamToos.readStram(is);
                        System.out.println(result + "=========================");

                        //解析json
                        JSONObject jsonObject = new JSONObject(result);
                        String verson = (String) jsonObject.get("verson");
                        desc = (String) jsonObject.get("desc");
                        apkurl = (String) jsonObject.get("apkurl");

                        if (getVersion().equals(verson)) {//没有新版本，进入主页面
                            msg.what = ENTERHOME;
                        } else {//有新版本，弹出提示升级对话框
                            msg.what = SHOWDIALOG;
                        }
                    }
                } catch (MalformedURLException e) {
                    msg.what = URLERROR;
                    e.printStackTrace();

                } catch (IOException e) {
                    msg.what = IOERROR;
                    e.printStackTrace();

                } catch (JSONException e) {
                    msg.what = JSONERROR;
                    e.printStackTrace();
                } finally {
                    long endTime = System.currentTimeMillis();
                    long dTime = startTime - endTime;
                    if (dTime < 2000) {
                        try {
                            Thread.sleep(2000 - dTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    handler.sendMessage(msg);
                }

            }
        }.start();
    }

    private String getVersion() {//拿到当前版本
        PackageManager pm = getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return "";
    }
}
