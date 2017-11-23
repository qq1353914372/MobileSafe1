package com.helloword.lgy.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.helloword.lgy.mobilesafe.db.AppLockDb;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasee on 2017/10/25.
 */

//将BlackNumberDAO类设置为单例模式
public class AppLockDAO {
    private  Context context;
    private AppLockDb openHelper;


    //私有化构造函数
    private AppLockDAO(Context context) {
        openHelper = new AppLockDb(context);
        this.context=context;
    }

    //定义一个实例
    private static AppLockDAO appLockDAO = null;

    //定义一个静态方法将实例返回
    public static AppLockDAO getInstance(Context context) {
        if (appLockDAO == null) {
            appLockDAO = new AppLockDAO(context);
        }
        return appLockDAO;
    }
    public void insert(String packagename){
     SQLiteDatabase db= openHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("packagename",packagename);
        db.insert("applock",null,contentValues);
        db.close();
        //通知数据发生变化
        context.getContentResolver().notifyChange(Uri.parse("content://applock//change"),null);

    }
    public void delete(String packagename){
        SQLiteDatabase db=openHelper.getWritableDatabase();
        db.delete("applock","packagename=?",new String[]{packagename});
        db.close();
        //通知数据发生变化
        context.getContentResolver().notifyChange(Uri.parse("content://applock//change"),null);

    }
    public List<String> queryAll(){
        SQLiteDatabase db=openHelper.getWritableDatabase();
       Cursor cursor= db.query("applock",new String[]{"packagename"},null,null,null,null,null);
        List<String> lockPackagenameList=new ArrayList<String>();
        while (cursor.moveToNext()){
          lockPackagenameList.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return lockPackagenameList;
    }


}
