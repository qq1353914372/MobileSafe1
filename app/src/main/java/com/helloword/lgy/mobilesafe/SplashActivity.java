package com.helloword.lgy.mobilesafe;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.helloword.lgy.mobilesafe.com.helloword.lgy.mobilesafe.utils.SteamToos;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
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
//    private static final int CONNECTERROR = 5;

    //json内容
    private String desc;
    private String apkurl;

    private TextView tv_sqlash_version;
    private TextView tv_sqlash_progress;
    private SharedPreferences sp;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        tv_sqlash_version = (TextView) findViewById(R.id.tv_sqlash_version);
        tv_sqlash_version.setText("版本号：" + getVersion());

        tv_sqlash_progress = (TextView) findViewById(R.id.tv_sqlash_progress);
            sp=getSharedPreferences("config",MODE_PRIVATE);
            boolean update=sp.getBoolean("update",false);
        if (update){
            //联网检查版本
            checkUpdate();
        }else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    enter();
                }
            },1000);
        }
        //设置朦胧动画效果
        AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
        aa.setDuration(500);
        findViewById(R.id.activity_splash).setAnimation(aa);
        //初始化数据库
        initDB();
        //生成快捷方式
        initShortCut();
    }

    private void initShortCut() {
       boolean initShortCut= sp.getBoolean("initShortCut",false);
        if (!initShortCut){
            //发一个广播给桌面应用，让桌面生成快捷方式
            //并告知桌面快捷方式的图标，名字，点击快捷方式到达哪个页面
            Intent intent=new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON,
                    BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,"快捷方式");
            //隐式意图打不开页面    原因：可能是设置参数时写错
//        Intent i=new Intent("android.intent.action.HOME");
//        i.addCategory("android.intent.category.R");
            Intent i=new Intent(this,HomeActivity.class);

            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,i);
            sendBroadcast(intent);
        }
        SharedPreferences.Editor edit=sp.edit();
        edit.putBoolean("initShortCut",true);
        edit.commit();


    }

    private void initDB() {
        //初始化归属地数据库
        initAdressDB("address.db");
        //初始化常用号码数据库
        initAdressDB("commonnum.db");
        //初始化病毒数据库
        initAdressDB("antivirus.db");


    }
    //将assets目录下的归属地数据库拷贝到file目录
    private void initAdressDB(String dbnaame) {
    //在File目录创建同名数据库文件
        File files=getFilesDir();
        File file=new File(files,dbnaame);
        //做一个判断，
        if (file.exists()){
            return;
        }
        InputStream stream=null;
        FileOutputStream fos=null;
        try {
            stream= getAssets().open(dbnaame);
            //将读取到的内容写入指定的文件
            fos =new FileOutputStream(file);
            byte[] bs=new byte[1024];
            int len=-1;
            while ((len=stream.read(bs))!=-1){
                fos.write(bs,0,len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
         if (stream!=null&&fos!=null){
             try {
                 stream.close();
                 fos.close();
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
        }
    }


    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示升级");
        builder.setMessage(desc);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                    enter();
                    dialog.dismiss();
            }
        });
        builder.setPositiveButton("立即升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                //检查sdcard是否挂载
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    //sdcard已挂载
                    FinalHttp finalHttp = new FinalHttp();
                    finalHttp.download(apkurl, Environment.getExternalStorageDirectory().getAbsolutePath(), new AjaxCallBack<File>() {
                        @Override
                        public void onLoading(long count, long current) {//下载中
                            super.onLoading(count, current);
                            int progress = (int) (current * 100 / count);
                            // TODO: 2017/10/1 下载更新模块 未实现

                        }

                        @Override
                        public void onSuccess(File file) {//下载成功
                            super.onSuccess(file);
                            installAPK(file);
                        }

                        private void installAPK(File file) {
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.VIEW");
                            intent.addCategory("android.intent.categroy.DEFAULT");
                            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                            startActivity(intent);

                        }

                        @Override
                        public void onFailure(Throwable t, int errorNo, String strMsg) {//下载失败
                            super.onFailure(t, errorNo, strMsg);
                            Toast.makeText(SplashActivity.this, "下载失败", Toast.LENGTH_SHORT).show();

                            enter();
                            dialog.dismiss();
                        }
                    });
                } else {//未挂载

                }


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
//                    else {
//                        msg.what=CONNECTERROR;
//                    }
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
