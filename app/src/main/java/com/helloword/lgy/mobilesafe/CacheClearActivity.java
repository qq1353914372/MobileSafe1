package com.helloword.lgy.mobilesafe;

import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

// TODO: 2017/11/9 一键清理已完成   下一步单个app清理
public class CacheClearActivity extends AppCompatActivity {
    private static final int UP_CLEARAPPINFO = 100;
    private static final int CHECK_CACHE_APP = 101;
    public static final int CHECK_FINISH = 102;
    public static final int CACHE_CLEAR_FINISH = 103;
    private Button btn_clear;
    private ProgressBar pb_clear;
    private TextView tv_clear;
    private LinearLayout ll_clear;
    private PackageManager mpm;
    private int mIndex=0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UP_CLEARAPPINFO:
                    View clearinfo = View.inflate(getApplicationContext(), R.layout.item_cacheinfo, null);
                    ImageView iv_appicon = (ImageView) clearinfo.findViewById(R.id.iv_appicon);
                    TextView tv_appname = (TextView) clearinfo.findViewById(R.id.tv_appname);
                    TextView tv_memory = (TextView) clearinfo.findViewById(R.id.tv_memory);
                    ImageView iv_clear = (ImageView) clearinfo.findViewById(R.id.iv_clear);
                    CacheInfo cacheInfo= (CacheInfo) msg.obj;
                    iv_clear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                            intent.setData(Uri.parse("package:com.helloword.lgy.mobilesafe"));
                            startActivity(intent);
                        }
                    });
                    iv_appicon.setImageDrawable(cacheInfo.icon);
                    tv_appname.setText(cacheInfo.name);
                    String cacheSize= Formatter.formatFileSize(getApplicationContext(),cacheInfo.cachesize);
                    tv_memory.setText(cacheSize);
                    ll_clear.addView(clearinfo,0);
                    break;
                case CHECK_CACHE_APP:
                    tv_clear.setText("正在扫描:"+(String)msg.obj);
                    break;
                case CHECK_FINISH:
                    tv_clear.setText("扫描完成");
                    break;
                case CACHE_CLEAR_FINISH:
                   ll_clear.removeAllViews();

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_clear);
        initUI();
        initData();
    }

    private void initData() {
        new Thread() {


            @Override
            public void run() {
                super.run();
                mpm = getPackageManager();
                List<PackageInfo> packageInfoList = mpm.getInstalledPackages(0);
                //设置进度条最大值
                pb_clear.setMax(packageInfoList.size());
                for (PackageInfo info : packageInfoList) {
                    String packagename = info.packageName;
                    getPackageCache(packagename);
                    try {
                        Thread.sleep(50+new Random().nextInt(100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //更新进度条
                    mIndex++;
                    pb_clear.setProgress(mIndex);
                    //发消息更新TextView
                    Message msg = Message.obtain();
                    msg.what = CHECK_CACHE_APP;
                    String appname;
                    try {
                        appname=mpm.getApplicationInfo(packagename,0).loadLabel(mpm).toString();
                        msg.obj =appname;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    mHandler.sendMessage(msg);

                }
                Message msg = Message.obtain();
                msg.what = CHECK_FINISH;
                mHandler.sendMessage(msg);

            }
        }.start();


    }


    private void getPackageCache(String packagename) {
        //创建了一个IPackageStatsObserver.Stub子类的对象,并且实现了onGetStatsCompleted方法
        IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {

            public void onGetStatsCompleted(PackageStats stats, boolean succeeded) {
                //缓存大小的过程
                long cachesize = stats.cacheSize;
                if (cachesize > 0) {
                    CacheInfo cacheInfo = new CacheInfo();
                    cacheInfo.cachesize = cachesize;
                    cacheInfo.packagename = stats.packageName;
                    try {
                        cacheInfo.name = mpm.getApplicationInfo(stats.packageName, 0).loadLabel(mpm).toString();
                        cacheInfo.icon = mpm.getApplicationInfo(stats.packageName, 0).loadIcon(mpm);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    Message msg = Message.obtain();
                    msg.what = UP_CLEARAPPINFO;
                    msg.obj = cacheInfo;
                    mHandler.sendMessage(msg);

                }
            }
        };
        try {
            //反射：
            //第一步，拿到字节码文件
            Class<?> clazz = Class.forName("android.content.pm.PackageManager");
            //获取调用方法的对象  第一个参数方法名，第二个参数是方法的第一个参数，第三个参数是方法的第二个参数
            Method method = clazz.getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            //对象调用方法
            method.invoke(mpm, packagename, mStatsObserver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public class CacheInfo {
        public String name;
        public String packagename;
        public Drawable icon;
        public long cachesize;

    }

    private void initUI() {
        btn_clear = (Button) findViewById(R.id.btn_clear);
        pb_clear = (ProgressBar) findViewById(R.id.pb_clear);
        tv_clear = (TextView) findViewById(R.id.tv_clear);
        ll_clear = (LinearLayout) findViewById(R.id.ll_clear);

        //一键清理缓存
        //调用PackageManager的freeStorageAndNotify方法(已被系统隐藏)，需要用反射调用
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1.获取指定类的字节码文件
                try {
                    Class<?> clazz = Class.forName("android.content.pm.PackageManager");
                    //2.获取调用方法对象
                    Method method = clazz.getMethod("freeStorageAndNotify", long.class,IPackageDataObserver.class);
                    //3.获取对象调用方法
                    method.invoke(mpm, Long.MAX_VALUE,new IPackageDataObserver.Stub() {
                        @Override
                        public void onRemoveCompleted(String packageName, boolean succeeded)
                                throws RemoteException {
                            //清除缓存完成后调用的方法(考虑权限)
                            Message msg = Message.obtain();
                            msg.what = CACHE_CLEAR_FINISH;
                            mHandler.sendMessage(msg);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
