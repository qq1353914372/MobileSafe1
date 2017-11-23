package com.helloword.lgy.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.helloword.lgy.mobilesafe.db.BlackNunberDb;
import com.helloword.lgy.mobilesafe.db.db.domain.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasee on 2017/10/25.
 */

//将BlackNumberDAO类设置为单例模式
public class BlackNumberDAO {
    private BlackNunberDb openHelper;


    //私有化构造函数
    private BlackNumberDAO(Context context) {
        openHelper = new BlackNunberDb(context);
    }

    //定义一个实例
    private static BlackNumberDAO blackNumberDAO = null;

    //定义一个静态方法将实例返回
    public static BlackNumberDAO getInstance(Context context) {
        if (blackNumberDAO == null) {
            blackNumberDAO = new BlackNumberDAO(context);
        }
        return blackNumberDAO;
    }
    //增加
    public void insert(String phone,String mode){
       SQLiteDatabase db= openHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("phone",phone);
        values.put("mode",mode);
        db.insert("blacknumber",null,values);
        db.close();
    }
    //删除
    public void delete(String phone){
        SQLiteDatabase db= openHelper.getWritableDatabase();
        db.delete("blacknumber","phone=?",new String[]{phone});
        db.close();
    }
    //改
    public void update(String phone,String mode){
        SQLiteDatabase db=openHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("mode",mode);
        db.update("blacknumber",values,"phone=?",new String[]{phone});
        db.close();
    }
    //查所有
    public List<BlackNumberInfo> findAll(){
        List<BlackNumberInfo> blackNumberInfos=new ArrayList<BlackNumberInfo>();
        SQLiteDatabase db=openHelper.getWritableDatabase();
      Cursor cursor= db.query("blacknumber",new String[]{"phone","mode"},null,null,null,null,"_id desc");
        while (cursor.moveToNext()){
            BlackNumberInfo blackNumberInfo=new BlackNumberInfo();
         blackNumberInfo.phone= cursor.getString(0);
            blackNumberInfo.mode=cursor.getString(1);
            blackNumberInfos.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return blackNumberInfos;
    }
    //查询20条数据
    public List<BlackNumberInfo> find(int index){
        List<BlackNumberInfo> blackNumberInfos=new ArrayList<BlackNumberInfo>();
        SQLiteDatabase db=openHelper.getWritableDatabase();
        Cursor cursor= db.rawQuery("select * from blacknumber order by _id desc limit ?,20;",new String[]{index+""});
        while (cursor.moveToNext()){
            BlackNumberInfo blackNumberInfo=new BlackNumberInfo();
            blackNumberInfo.phone= cursor.getString(0);
            blackNumberInfo.mode=cursor.getString(1);
            blackNumberInfos.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return blackNumberInfos;
    }
    //拿到数据库总条目
    public int getCount(){
        int count=0;
        SQLiteDatabase db=openHelper.getWritableDatabase();
       Cursor cursor= db.rawQuery("select count(*) from blacknumber;",null);
        if (cursor.moveToNext()){
        count= cursor.getInt(0);
        }
        return count;
    }
    //查询拦截模式
    public int getMode(String phone){
        int mode=0;
        SQLiteDatabase db=openHelper.getWritableDatabase();
        Cursor cursor= db.query("blacknumber",new String[]{"mode"},"phone=?",new String[]{phone},null
        ,null,null);
        if (cursor.moveToNext()){
            mode= cursor.getInt(0);
        }
        return mode;
    }

}
