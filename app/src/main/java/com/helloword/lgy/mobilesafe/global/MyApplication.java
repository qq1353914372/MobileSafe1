package com.helloword.lgy.mobilesafe.global;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import static android.content.ContentValues.TAG;

/**
 * Created by hasee on 2017/11/13.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //捕获本应用未捕获的异常
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Log.i(TAG, "捕获到了一个异常");
                //拿到sd卡路径
                String path= Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"error.log";
                File file= new File(path);
                try {
                    //保存异常信息到sd卡
                    PrintWriter printWriter=new PrintWriter(file);
                   e.printStackTrace(printWriter);
                    printWriter.close();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                //退出应用
                System.exit(0);
            }
        });
    }
}
