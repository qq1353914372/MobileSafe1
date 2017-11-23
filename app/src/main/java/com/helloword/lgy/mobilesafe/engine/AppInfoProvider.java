package com.helloword.lgy.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.helloword.lgy.mobilesafe.db.db.domain.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasee on 2017/10/31.
 */

public class AppInfoProvider {
    public static List<AppInfo> getAppInfoList(Context context){
       PackageManager pm= context.getPackageManager();
        List<PackageInfo> packageInfoList=pm.getInstalledPackages(0);
        List<AppInfo> appInfoList=new ArrayList<AppInfo>();
        //遍历集合
        for (PackageInfo packageInfo:packageInfoList) {
            AppInfo appInfo=new AppInfo();
            //包名
            appInfo.packageName=packageInfo.packageName;
            //应用名（从清单文件得到）
          ApplicationInfo applicationInfo= packageInfo.applicationInfo;
            appInfo.name=applicationInfo.loadLabel(pm).toString();
            //图标
            appInfo.icon=applicationInfo.loadIcon(pm);
            //判断是否为系统应用（每一个手机上的应用对应的flag不一致）
            if ((applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)==
                   ApplicationInfo.FLAG_SYSTEM){
                //系统应用
                appInfo.isSystem=true;

            }else {
                //非系统应用
                appInfo.isSystem=false;
            }
            //判断是否为sd卡安装应用
            if ((applicationInfo.flags& ApplicationInfo.FLAG_EXTERNAL_STORAGE)
                    ==ApplicationInfo.FLAG_EXTERNAL_STORAGE){
                appInfo.isCdCard=true;
            }else {

                appInfo.isCdCard=false;
            }
            appInfoList.add(appInfo);
        }
        return appInfoList;
    }
}
