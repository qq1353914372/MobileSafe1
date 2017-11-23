package com.helloword.lgy.mobilesafe.com.helloword.lgy.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by hasee on 2017/10/30.
 */

public class ServiceUtils {
    private static ActivityManager mAM;
    public static boolean isRunning(Context context,String servicename){
        mAM= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
       List<ActivityManager.RunningServiceInfo> serviceInfos= mAM.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo infos:serviceInfos
        ){
            if (servicename.equals(infos.service.getClassName())){
                return true;
            }
        }

        return false;
    }
}
