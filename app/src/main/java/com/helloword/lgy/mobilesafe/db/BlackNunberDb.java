package com.helloword.lgy.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hasee on 2017/10/25.
 */

public class BlackNunberDb extends SQLiteOpenHelper {
    public BlackNunberDb(Context context) {
        super(context, "blacknumber.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table blacknumber(_id integer primary key " +
                "autoincrement ,phone varchar(20),mode varchar(5));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
