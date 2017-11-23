package com.helloword.lgy.mobilesafe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Debug;

import com.helloword.lgy.mobilesafe.R;
import com.helloword.lgy.mobilesafe.db.db.domain.ProcessInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasee on 2017/11/1.
 */

public class ProcessInfoProvider {
    //拿到当前进程数
    public static int getProcessCount(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        return processInfos.size();
    }

    //拿到可用空间
    public static long getAvailableSpace(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem;
    }

    //拿到总空间
    public static long getCountSpace(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        return memoryInfo.totalMem;
        //拿总空间方法二
        //每一步手机的pro目录下都有手机的配置信息
        //可以通过读取流的形式将数据读取出来
    }

    public static List<ProcessInfo> getProcessInfo(Context context) {
        //需要拿到的数据：应用名，包名，进程占用大小，应用图标，是否为系统应用
        //1,拿到ActivityManager和PackageManeger
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm=context.getPackageManager();
        //线程集合
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfo = am.getRunningAppProcesses();
        //要返回的集合
        List<ProcessInfo> processInfoList=new ArrayList<ProcessInfo>();
        //遍历进程集合，拿到进程信息
        for (ActivityManager.RunningAppProcessInfo info : runningAppProcessInfo) {
            ProcessInfo processInfo = new ProcessInfo();
            //获取进程名称     进程名==包名
            processInfo.packageName = info.processName;
            //获取进程占用内存大小  参数是pid，进程号
            Debug.MemoryInfo[] memoryInfos = am.getProcessMemoryInfo(new int[]{info.pid});
            //拿到当前进程对象
            Debug.MemoryInfo memoryInfo = memoryInfos[0];
            //获取当前进程所占大小
            processInfo.processmemory = memoryInfo.getTotalPrivateDirty() * 1024;
            try {
           ApplicationInfo applicationInfo= pm.getApplicationInfo(processInfo.packageName,0);
                //拿到应用名称
               processInfo.name= applicationInfo.loadLabel(pm).toString();
                //拿到图标
               processInfo.icon= applicationInfo.loadIcon(pm);
                //判断是否为系统应用
                if ((applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)==ApplicationInfo.FLAG_SYSTEM){
                    processInfo.isSystem=true;
                }else {
                    processInfo.isSystem=false;
                }
            } catch (PackageManager.NameNotFoundException e) {
                //找不到应用名异常，通常这类应用都是系统应用
                processInfo.name=processInfo.packageName;
                processInfo.icon=context.getResources().getDrawable(R.mipmap.ic_launcher);
                processInfo.isSystem=true;
                e.printStackTrace();
            }
            processInfoList.add(processInfo);

        }
        return processInfoList;
    }
    public static void killProcess(Context context,ProcessInfo processInfo){
        ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(processInfo.packageName);
    }
    public static void killAllProcess(Context context){
        ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
      List<ActivityManager.RunningAppProcessInfo> processInfos=  am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info:processInfos) {
            if (info.processName.equals(context.getPackageName())){
                continue;
            }else {
                am.killBackgroundProcesses(info.processName);
            }
        }

    }

}
