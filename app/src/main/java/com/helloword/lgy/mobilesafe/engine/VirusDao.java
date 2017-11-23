package com.helloword.lgy.mobilesafe.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasee on 2017/10/11.
 */

public class VirusDao {
    private static String path = "data/data/com.helloword.lgy.mobilesafe/files/antivirus.db";

    public static List<String> getVirus(){
        SQLiteDatabase db=SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.OPEN_READONLY);
      Cursor cursor= db.query("datable",new String[]{"md5"},null,null,null,null,null,null);
        List<String> virusLists=new ArrayList<String>();
        while (cursor.moveToNext()){
            virusLists.add(cursor.getString(0));
        }
        db.close();
        return virusLists;
    }

}
