package com.helloword.lgy.mobilesafe.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by hasee on 2017/10/11.
 */

public class AdressDao {
    private static String path = "data/data/com.helloword.lgy.mobilesafe/files/address.db";
    private static String mAdress = "未知号码";

    //传一个电话号码进来，去数据库查询号码的归属地，并将归属地返回
    public static String getAdress(String phone) {
        mAdress="未知号码";//每次查询前都重置一下
        //电话号码的正则表达式
        //^代表匹配输入字符串的开始位置--是1。[]代表字符范围。匹配指定范围内的任意字符。--第二位数字范围是3-8。
        // \d代表匹配一个数字字符。{}代表组  \d{9}后面9位数的范围是1到9
        String regularExpression = "^1[3-8]\\d{9}";//\\表示\的转义
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        if (phone.matches(regularExpression)) {//如果输入的手机号码符合正则手机号的正则表达式则进入这个逻辑
            phone = phone.substring(0, 7);

            Cursor cursor = db.query("data1", new String[]{"outkey"}, "id=?", new String[]{phone}, null, null, null);
            if (cursor.moveToNext()) {
                String outkey = cursor.getString(0);

                System.out.println("=========" + outkey);


                Cursor indexcursor = db.query("data2", new String[]{"location"}, "id=?", new String[]{outkey}, null, null, null);
                if (indexcursor.moveToNext()) {
                    mAdress = indexcursor.getString(0);
                    System.out.println("=========" + mAdress);
                }

            } else {//在表中查不到
                mAdress = "未知号码";
            }

        } else {
            int length = phone.length();
            switch (length) {
                case 3:
                    mAdress = "报警电话";
                    break;
                case 4:
                    mAdress = "模拟器";
                    break;
                case 5:
                    mAdress = "服务电话";
                    break;
                case 7:
                    mAdress = "固定电话";
                    break;
                case 8:
                    mAdress = "固定电话";
                    break;
                case 9:
                    mAdress = "固定电话";
                    break;
                case 11://3+8  区号+固定电话
                  String  area=phone.substring(1,3);
               Cursor cursor=  db.query("data2",new String[]{"location"},"area=?",new String[]{area},null,null,null);
                    if (cursor.moveToNext()){
                        mAdress=cursor.getString(0);
                    }else {
                        mAdress="未知号码";
                    }

                    break;
                case 12://4+8  区号+固定电话
                    String  area1=phone.substring(1,4);
                    Cursor cursor1=  db.query("data2",new String[]{"location"},"area=?",new String[]{area1},null,null,null);
                    if (cursor1.moveToNext()){
                        mAdress=cursor1.getString(0);
                    }else {
                        mAdress="未知号码";
                    }

                    break;
            }
        }

        return mAdress;
    }
}
