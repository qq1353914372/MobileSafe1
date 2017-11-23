package com.helloword.lgy.mobilesafe;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.helloword.lgy.mobilesafe.com.helloword.lgy.mobilesafe.utils.Md5Util;
import com.helloword.lgy.mobilesafe.engine.VirusDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KillVirusActivity extends AppCompatActivity {
    private final int SCANNING=100;
    private final int SCAN_FINISH=101;
    private ImageView iv_scanning;
    private TextView tv_name;
    private ProgressBar pb;
    private LinearLayout ll_add;
    private int index=0;
    private List<ScanInfo> mVirusInfoList;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SCANNING:
                    ScanInfo scanInfo= (ScanInfo) msg.obj;
                    tv_name.setText(scanInfo.appName);
                    TextView textView=new TextView(getApplicationContext());
                    if (scanInfo.isVirus){//病毒
                        textView.setText("发现病毒："+scanInfo.appName);
                        textView.setTextColor(Color.RED);
                    }else {//非病毒
                        textView.setText("扫描安全："+scanInfo.appName);
                        textView.setTextColor(Color.BLACK);
                    }
                    ll_add.addView(textView,0);

                    break;
                case SCAN_FINISH:
                    tv_name.setText("扫描完成");
                    iv_scanning.clearAnimation();
                    //遍历包含病毒应用的集合，卸载
                    for (ScanInfo info:mVirusInfoList) {
                        un(info.packageName);
                    }
                    break;
            }
        }
    };
    public  void un(String packagename){//卸载应用
        Intent intent = new Intent("android.intent.action.DELETE");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:" + packagename));
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kill_virus);
        initUI();
        initAnimation();
        checkVieus();
    }

    private void checkVieus() {
        //耗时操作
       new Thread(){
           @Override
           public void run() {
               super.run();
               //拿到数据库中的病毒数据
               List<String> virusList= VirusDao.getVirus();
               PackageManager pm=getPackageManager();
               //拿到手机上所有应用的签名文件  已安装应用+未安装应用
               List<PackageInfo> packageInfoList= pm.getInstalledPackages(PackageManager.GET_SIGNATURES
                       +PackageManager.GET_UNINSTALLED_PACKAGES);
               //设置进度条
               pb.setMax(packageInfoList.size());
               //创建扫描的所有应用的集合
               List<ScanInfo> scanInfoList=new ArrayList<ScanInfo>();
               //创建查到病毒的集合
               mVirusInfoList  =new ArrayList<ScanInfo>();
               //遍历集合
               for (PackageInfo info:packageInfoList) {
                   ScanInfo scanInfo=new ScanInfo();
                   //获取签名文件数组
                   Signature[] signatures= info.signatures;
                   //数组第一位，拿到签名文件
                   Signature signature=signatures[0];
                   String str=  signature.toCharsString();
                   //装换为md5
                   String encoder=  Md5Util.encoder(str);
                   if (virusList.contains(encoder)){
                       //记录病毒
                       scanInfo.isVirus=true;
                       mVirusInfoList.add(scanInfo);
                   }else {
                       scanInfo.isVirus=false;
                   }
                   scanInfo.packageName=info.packageName;
                   scanInfo.appName=info.applicationInfo.loadLabel(pm).toString();
                   //扫描过的所有应用都加入集合，以便设置进度条
                   scanInfoList.add(scanInfo);
                   index++;
                   pb.setProgress(index);
                   //睡眠一下让UI效果更美观
                   try {
                       Thread.sleep(50 +new Random().nextInt(100));

                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }

                   //发消息告知主线程更新UI
                   Message msg=Message.obtain();
                   msg.what=SCANNING;
                   msg.obj=scanInfo;
                   mHandler.sendMessage(msg);
               }
               // 扫描完成
               Message msg=Message.obtain();
               msg.what=SCAN_FINISH;
               mHandler.sendMessage(msg);
           }
       }.start();

    }
    public class ScanInfo{
        public boolean isVirus;
        public String packageName;
        public String appName;
    }
    private void initAnimation() {
        RotateAnimation rotateAnimation=new RotateAnimation(
                0,360,
                Animation.RELATIVE_TO_SELF,0.5f,
                Animation.RELATIVE_TO_SELF,0.5f
        );
        //动画时长
        rotateAnimation.setDuration(1000);
        //动画结束时停留在该状态
        rotateAnimation.setFillAfter(true);
        //无限循环
        rotateAnimation.setRepeatCount(RotateAnimation.INFINITE);
        iv_scanning.startAnimation(rotateAnimation);
    }


    private void initUI() {
        iv_scanning= (ImageView) findViewById(R.id.iv_scanning);
        tv_name= (TextView) findViewById(R.id.tv_name);
        pb= (ProgressBar) findViewById(R.id.pb);
        ll_add= (LinearLayout) findViewById(R.id.ll_add);
    }
}
